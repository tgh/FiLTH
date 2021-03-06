import java.io.*;
import tylerhayes.tools.*;
import java.sql.*;
import com.csvreader.*;
import java.util.*;

/**
 * This program is used to populate the oscar_given_to table in the database.
 * The scripts/oscarGivenTo.sh shell script massages the original
 * data/oscars.csv file into the data/oscarsOfCategory.csv file.  This
 * program then parses that csv file and outputs appropriate sql insert
 * statements for inserting the proper data into the oscar_given_to table.
 * User interaction is may occur, and various sql files are created based
 * on what is already in the database and what isn't.  For example, if a
 * movie that is nominated is not in the database, the user is prompted for
 * imformation, and the movie is added to a separate sql file to be used
 * later to add to the movie table in the database.  Same goes for an
 * unknown crew person (e.g. actor, director, etc).
 */
public class OscarParser implements GracefulShutdown {

  //absolute path to FiLTH project
  private String filthPath = "/home/tgh/workspace/FiLTH";
  //for a file to log certain actions and events
  private Log log = null;
  //for a connection to the database
  private Connection dbConn = null;
  //Csv file reader for the csv file
  private CsvReader oscars = null;
  //for file output to oscar_given_to.sql (the sql commands to populate the
  // oscar_given_to table in the db)
  private BufferedWriter oscarFileWriter = null;
  //for file output to a new movie sql file (oscar movies I haven't seen)
  private BufferedWriter movieFileWriter = null;
  //for file output to crew_person sql file (crew persons not in the db)
  private BufferedWriter crewFileWriter = null;
  //for file output to worked_on sql file (crew persons worked on a movie)
  private BufferedWriter workedFileWriter = null;
  //for reading from stdin
  private BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
  //name of the db
  private static String dbname;
  //password for db
  private static String dbpw;
  //flag for no command-line arg
  private static boolean noarg;
  //for querying the database
  private PostgreSQLConsole db = null;
  //the id of then next new movie
  private int nextMid;
  //the id of then next new crew person
  private int nextCid;
  //these maps are used as cache storage for mid's and cid's from the database
  // The strings used in the movie maps are the title plus the year of the
  // movie.
  private HashMap<String, Integer> movieMap = null;
  private HashMap<String, Integer> crewMap  = null;
  private HashMap<String, Integer> movieNotFoundMap = null;
  private HashMap<String, Integer> crewNotFoundMap  = null;

  //--------------------------------------------------------------------------

  /**
   * Main.
   *
   * Sets up the graceful shutDown mechanism, then calls start().
   */
  public static void main (String[] args) {
    //no arg given for the db password
    if (args.length != 2) {
      System.out.println("\n - usage: OscarParser {database name} {database password}\n");
      noarg = true;
      System.exit(1);
    }
    noarg = false;
    //setup shutDown hook mechanism in order to close objects properly in case
    // of premature termination (by the OS or the user, for example).  This is
    // pretty much just used in order to write out everything in the Buffered-
    // Writer object in the Log object to the log file before ending the
    // process completely.
    GracefulShutdown op = new OscarParser();
    TerminationInterceptor ti = new TerminationInterceptor(op);
    Runtime.getRuntime().addShutdownHook(ti);
    //grab the name of the database
    dbname = args[0];
    //grab the password for the database
    dbpw = args[1];
    //start program
    op.start();
  }

  //--------------------------------------------------------------------------

  /**
   * start().
   *
   * The meat of the program.
   */
  public void start () {
    //for storing input from stdin
    String response = null;

    //open a log file
    log = new Log(filthPath + "/temp/oscarParser.log");

    //open the oscarsGivenTo.csv file
    try {
      oscars = new CsvReader(filthPath + "/data/oscarsOfCategory.csv");
    }
    catch (FileNotFoundException fnfe) {
      log.logFatalError("CSV file not found",0,false);
      fnfe.printStackTrace();
      System.exit(1); 
    }

    //open file to write the sql insert staments for the oscar_given_to table
    try {
      oscarFileWriter = new BufferedWriter(new FileWriter(filthPath + "/sql/oscar_given_to.sql"));
    }
    catch (IOException ioe) {
      log.logFatalError("Unable to create/open oscar_given_to.sql",0,false);
      ioe.printStackTrace();
      System.exit(1); 
    }
    //open file to write the sql insert staments for the worked_on table
    try {
      workedFileWriter = new BufferedWriter(new FileWriter(filthPath + "/sql/worked_on.sql"));
    }
    catch (IOException ioe) {
      log.logFatalError("Unable to create/open worked_on.sql",0,false);
      ioe.printStackTrace();
      System.exit(1); 
    }

    //create a BufferedReader to read from stdin
    stdinReader = new BufferedReader(new InputStreamReader(System.in));

    //connect to the database
    dbConn = DatabaseConnector.connectToPostgres("jdbc:postgresql://localhost/" + dbname,
                                                 "postgres",
                                                 dbpw);
    //setup virtual SQL console with the db
    db = new PostgreSQLConsole(dbConn);

    //initialize the next new ids for movie and crew person
    initNextIds();

    //init the hash maps
    movieMap = new HashMap<String, Integer>();
    crewMap  = new HashMap<String, Integer>();
    movieNotFoundMap = new HashMap<String, Integer>();
    crewNotFoundMap  = new HashMap<String, Integer>();

    log.logHeader("START");

    System.out.println("\nOscarParser\n");

    int prevYear = 2008;  //for logging purposes...

    //parse the csv file
    try {
      while (oscars.readRecord()) {
        //get the year of the oscar nomination
        int year = Integer.parseInt(oscars.get(0));
        
        //get the category of the nomination
        String category = oscars.get(1);

        //log a new section header for a new category in the log file
        if (prevYear == 2008 && year != 2008) {
          log.logHeader(category);
          if (category.equals("Best Actor")) {
            System.out.println("\tBest Actor / Best Supporting Actor...");
          }
          else if (category.equals("Best Actress")) {
            System.out.println("\tBest Actress / Best Supporting Actress...");
          }
          else {
            System.out.println("\t" + category + "...");
          }
        }
        prevYear = year;

        //log the current record
        log.logData("RECORD: " + year + ", " + category + ", " + oscars.get(2)
                    + ", " + oscars.get(3) + ", " + oscars.get(4), 0, false);

        //write the appropriate sql insert statement for this nomination based on the category:

        /*---------- BEST PICTURE (oid = 1) ---------------------------------*/

        if (category.equals("Best Picture")) {
          nonCrewCategory(year, 1);
        }

        /*---------- BEST ACTOR (oid = 2) -----------------------------------*/

        if (category.equals("Best Actor")) {
          actingCategory(year, 2);
        }

        /*---------- BEST ACTRESS (oid = 3) ---------------------------------*/

        if (category.equals("Best Actress")) {
          actingCategory(year, 3);
        }

        /*---------- BEST SUPPORTING ACTOR (oid = 4) ------------------------*/

        if (category.equals("Best Supporting Actor")) {
          actingCategory(year, 4);
        }

        /*---------- BEST SUPPORTING ACTRESS (oid = 5) ----------------------*/

        if (category.equals("Best Supporting Actress")) {
          actingCategory(year, 5);
        }

        /*---------- BEST DIRECTOR (oid = 6) --------------------------------*/

        if (category.equals("Best Director")) {
          nonActingCrewCategory(year, 6);
        }

        /*---------- BEST CINEMATOGRAPHY (b & w) (oid = 7) ------------------*/

        if (category.equals("Best Cinematography (black and white)")) {
          nonActingCrewCategory(year, 7);
        }

        /*---------- BEST CINEMATOGRAPHY (color) (oid = 8) ------------------*/

        if (category.equals("Best Cinematography (color)")) {
          nonActingCrewCategory(year, 8);
        }

        /*---------- BEST CINEMATOGRAPHY (oid = 9) --------------------------*/

        if (category.equals("Best Cinematography")) {
          nonActingCrewCategory(year, 9);
        }

        /*---------- BEST ADAPTED SCREENPLAY (oid = 10) ---------------------*/

        if (category.equals("Best Adapted Screenplay")) {
          nonActingCrewCategory(year, 10);
        }

        /*---------- BEST ORIGINAL SCREENPLAY (oid = 11) --------------------*/

        if (category.equals("Best Original Screenplay")) {
          nonActingCrewCategory(year, 11);
        }

        /*---------- BEST FOREIGN LANGUAGE FILM (oid = 12) ------------------*/

        if (category.equals("Best Foreign Language Film")) {
          nonCrewCategory(year, 12);
        }

        /*---------- BEST DOCUMENTARY (oid = 13) ----------------------------*/

        if (category.equals("Best Documentary")) {
          nonCrewCategory(year, 13);
        }

        /*---------- BEST SCREENPLAY (oid = 14) -----------------------------*/

        //there are some years where simply "Best Screenplay" is appropriate
        // since the categories don't really distingiush between original or
        // adapted screenplays for those years
        if (category.equals("Best Screenplay")) {
          nonActingCrewCategory(year, 14);
        }
      } //end while loop
    } //end try
    catch (IOException ioe) {
      log.logFatalError("I/O Error of some kind",0,false);
      log.logGeneralMessageWithoutIndicator(ioe.toString(),0,false);
      ioe.printStackTrace();
      System.exit(1);
    }
  }

  //--------------------------------------------------------------------------

  /**
   * What occurs when the program gets terminated.
   */
  public void shutDown() {
    //only do this cleanup if the appropriate arguments were given, because
    // that situation is already dealt with
    if (noarg == false) {
      log.logFooter("END");
      //close database connection
      try { if (dbConn != null) dbConn.close(); }
      catch (SQLException sqle) { sqle.printStackTrace(); }
      //close the Log object
      log.close();
      //close the csv file
      if (oscars != null)
        oscars.close();
      //close buffered objects
      try {
        oscarFileWriter.close();
        workedFileWriter.close();
        if (movieFileWriter != null) {
          movieFileWriter.close();
        }
        if (crewFileWriter != null) {
          crewFileWriter.close();
        }
        stdinReader.close();
      }
      catch (IOException ioe) {
        System.out.println("Error in closing BufferedWriter or BufferedReader object.");
        ioe.printStackTrace();
      }
    }
  }
  
  //--------------------------------------------------------------------------

  /**
   * Since there's going to be a lot of SQLExceptions that need to be caught,
   * and it's good to know just where it was caught for debugging purposes,
   * this method will take care of all of them in order to not clutter up the
   * meat code with a lot of exception handling code.
   */
  private void handleSQLException(String message, SQLException sqle) {
    log.logFatalError(message,0,false);
    log.logGeneralMessageWithoutIndicator(sqle.toString(),0,false);
    sqle.printStackTrace();
    System.exit(1);
  }

  //--------------------------------------------------------------------------
  
  /**
   * Initialize the ids of what would be the next movie/crew person in the
   * movie/crew_person table respectively.
   */
  private void initNextIds() {
    ResultSet qResult = null;
    try {
      qResult = db.select("SELECT cid FROM crew_person ORDER BY cid DESC LIMIT 1;");
      if (qResult.next()) {
        nextCid = qResult.getInt(1);
      }
      qResult = db.select("SELECT mid FROM movie ORDER BY mid DESC LIMIT 1;");
      if (qResult.next()) {
        nextMid = qResult.getInt(1);
      }
    }
    catch (SQLException sqle) {
      handleSQLException("SQLException caught in initNextIds().",sqle);
    }
  }

  
  //--------------------------------------------------------------------------

  /**
   * Find and open the next file to create for movies not seen.  Since going
   * through all of the oscar nominations is going to take a LONG time, this
   * program is most likely going to be halted somewhere during execution.
   * If this was the first time, for example, sql/movie2.sql and
   * sql/crew_person2.sql would be created.  In order to avoid having to
   * redo all of those movies and recipients, those sql files need to be
   * executed in the database.  But then those sql commands should also
   * be preserved, so the next run of this program should create
   * sql/movie3.sql and sql/crew_person3.sql.  This is where it is
   * determined which numbered file the current run is on.
   */
  private void openMovieSqlFile() {
    int fileCounter = 2;
    try {
      while(new File(filthPath + "/sql/movie" + fileCounter + ".sql").exists()) {
        ++fileCounter;
      }
      movieFileWriter = new BufferedWriter(new FileWriter(filthPath + "/sql/movie" + fileCounter + ".sql"));
    }
    catch (IOException ioe) {
      log.logFatalError("Unable to create/open movie" + fileCounter + ".sql file.",0,false);
      ioe.printStackTrace();
      System.exit(1); 
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Find and open the next file to create for crew persons not in the db.
   *
   * See comments for openMovieSqlFile().
   */
  private void openCrewSqlFile() {
    int fileCounter = 2;
    try {
      while(new File(filthPath + "/sql/crew_person" + fileCounter + ".sql").exists()) {
        ++fileCounter;
      }
      crewFileWriter = new BufferedWriter(new FileWriter(filthPath + "/sql/crew_person" + fileCounter + ".sql"));
    }
    catch (IOException ioe) {
      log.logFatalError("Unable to create/open movie" + fileCounter + ".sql file.",0,false);
      ioe.printStackTrace();
      System.exit(1); 
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Like movie title, there are going to be some special cases for people's
   * names; this method takes care of those cases by matching them to how the
   * database schema is designed.
   */
  private String[] checkForNameSpecialCases(String[] name) {
    //names with 'De' or 'Del' (like Robert De Niro, Benicio Del Toro) combine
    // 'De' and 'Niro' (for example) into one.
    if (name[1].toLowerCase().equals("de")
        || name[1].toLowerCase().equals("del")
        || name[1].equals("Le")
        || name[1].equals("La")) {
      String[] n = {name[0],name[1] + " " + name[2]};
      return n;
    }
    //names with 'Jr.' or 'Sr.' (like Robert Downey, Jr.) include Jr./Sr. with
    // last name.  There is also the case where there is a "Jr." that makes
    // the name 4 tokens long (e.g. D. M. Marshman Jr.).
    if (name[2].equals("Jr.") || name[2].equals("Sr.") || (name.length == 4 && name[3].equals("Jr."))) {
      if (name.length == 4) {
        String[] n = {name[0],name[1],name[2] + " Jr."};
        return n;
      }
      else {
        String[] n = {name[0],name[1] + " " + name[2]};
        return n;
      }
    }
    //Gus Van Sant
    if (name[1].equals("Van") && name[2].equals("Sant")) {
      String[] n = {"Gus","Van Sant"};
      return n;
    }
    //the Coen brothers
    for (String nm : name) {
      //the only other Coen besides the Coen bothers in the relevant oscar
      // nominations is Franklin Coen
      if (nm.equals("Franklin")) {
        break;
      }
      if (nm.equals("Coen")) {
        String[] n = {"Joel and Ethan","Coen"};
        return n;
      }
    }
    //special case: Adela Rogers St. Johns
    if (name[2].equals("St.")) {
      String[] n = {"Adela", "Rogers", "St. Johns"};
      return n;
    }

    return name;
  }

  //--------------------------------------------------------------------------

  /**
   * Query the database for the movie id of the given movie.
   *
   * @param title This String is really only used to output to stdout when
   * communicating to the user (it's the title as seen in the csv file record).
   * @param year An integer for the year of the movie in order to nail down the
   * specific movie.
   * @return The database's unique id of the movie if found, -1 otherwise.
   */
  private int queryForMovie(String title, int year) {
    //first, see if the movie is in cache (hash map)
    Integer Mid = movieMap.get(title + " " + year);
    //movie was in cache, no need to query database
    if (Mid != null) {
      return Mid.intValue();
    }
    //otherwise, see if the movie is in the cache of movies NOT in the database
    Mid = movieNotFoundMap.get(title + " " + year);
    //movie was in cache, no need to query database
    if (Mid != null) {
      log.logGeneralMessageWithoutIndicator("-- " + title + " not found.",1,false);
      return Mid.intValue();
    }

    //movie was not in cache, need to query database
    ResultSet qResult = null;
    int mid = -1;
    try {
      //title only contains stop words in Postgres's full text search or it's
      // a special case
      if (containsOnlyStopWords(title) || isSpecialCase(title)) {
        qResult = db.selectScrollable("SELECT mid, title FROM movie WHERE title = '" + title + "';");
      }
      //query for the movie
      else {
        qResult = db.selectScrollable("SELECT mid FROM movie WHERE lower(title) = '" 
                                      + title.toLowerCase().replace("'","''") 
                                      + "' AND (year = " + year + " OR year = " + (year-1)
                                      + " OR year = " + (year-2) + ");");
      }
      //movie found in db
      if (qResult.next()) {
        mid = qResult.getInt(1);
      }
      //movie not found in db
      else {
        log.logGeneralMessageWithoutIndicator("-- " + title + " not found.",1,false);
        //add movie to the hash map of movies not found
        movieNotFoundMap.put(title + " " + year, new Integer(nextMid));
        //prompt user for movie attribute values and write the sql for the movie
        mid = writeMovieSql(title, year);
      }

      //movie found, add to cache (hash map)
      if (mid != -1) {
        movieMap.put(title + " " + year, new Integer(mid));
      }

      return mid;
    }
    catch (SQLException sqle) {
      handleSQLException("SQLException caught in queryForMovie().",sqle);
    }

    //unreachable code--shut up compiler
    return -1;
  }

  //--------------------------------------------------------------------------

  /**
   * Query the database for the crew person id of the given person name.
   *
   * @param names The noninee's name but split into an array of Strings.
   * @param name The nominee's name as a single String with spaces.  This one
   * is used to check (and insert into) the crewMap HashMap.
   * @return The cid of the nominee (the crew_person unique id), or -1 if not
   * found.
   */
  private int queryForCrewperson(String[] names, String name) {
    String lname = null;
    String fname = null;
    String mname = null;
    ResultSet qResult = null;

    //first see if the name is in cache (the hash map)
    Integer Cid = crewMap.get(name);
    //this nominee is in cache, no need to query database
    if (Cid != null) {
      return Cid.intValue();
    }

    //otherwise, see if the name is in the cache of names NOT in the database
    Cid = crewNotFoundMap.get(name);
    //this nominee is in cache, no need to query database
    if (Cid != null) {
      log.logGeneralMessageWithoutIndicator("-- Crewperson, " + name + ", not found.", 1, false);
      return Cid.intValue();
    }

    //nominee crew id was not in cache, must query database
    int cid = -1;
    try {
      switch(names.length) {
        //e.g. Cher, Madonna, etc.
        case 1: lname = names[0];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname + "' AND f_name is NULL;");
                if (qResult.next()) {
                  cid = qResult.getInt(1);
                }
                break;
        //standard first name, last name
        case 2: lname = names[1].replace("'","''");
                fname = names[0];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname
                                    + "' AND f_name = '" + fname + "';");
                if (qResult.next()) {
                  cid = qResult.getInt(1);
                }
                break;
        //e.g. Phillip Seymour Hoffman, Samuel L. Jackson, etc.
        case 3: lname = names[2].replace("'","''");
                fname = names[0];
                mname = names[1].replace("'","''");
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname
                                    + "' AND f_name = '" + fname + "' AND m_name = '" + mname + "';");
                if (qResult.next()) {
                  cid = qResult.getInt(1);
                }
                break;
        default: log.logWarning("Recipient name is not 1, 2, or 3 names in length.", 1, false);
                 lname = name;
                 //special case: W. S. Van Dyke
                 if (name.equals("W. S. Van Dyke")) {
                   qResult = db.select("SELECT cid FROM crew_person WHERE l_name = 'Van Dyke'"
                                       + " AND f_name = 'W.' AND m_name = 'S.';");
                   if (qResult.next()) {
                     cid = qResult.getInt(1);
                   }
                 }
                 //special case: Willard Van der Veer
                 else if (name.equals("Willard Van der Veer")) {
                   qResult = db.select("SELECT cid FROM crew_person WHERE l_name = 'Van der Veer'"
                                       + " AND f_name = 'Willard';");
                   if (qResult.next()) {
                     cid = qResult.getInt(1);
                   }
                 }
                 break;
      }
    }
    catch (SQLException sqle) {
      handleSQLException("SQLException caught in queryForCrewperson().", sqle);
    }

    //nominee was found
    if (cid != -1) {
      //add this nominee to the cache (hash map)
      crewMap.put(name, new Integer(cid));
    }
    //recipient name was not found or was empty or more than 3 names long
    else {
      log.logGeneralMessageWithoutIndicator("-- Crewperson, " + name + ", not found.", 1, false);
      //add movie to the hash map of crew persons not found
      crewNotFoundMap.put(name, new Integer(nextCid));
      //prompt user for about the new person and write the sql statement for the person
      // (or get the cid if the user happens to provide it)
      cid = writeCrewSql(name, fname, mname, lname);
    }

    return cid;
  }

  //--------------------------------------------------------------------------

  /**
   * Handles Oscar categories that do not have a crew person recipient. (e.g.
   * Best Picture, Best Foreign Language Film, Best Documentary).
   *
   * @param year An integer for the year of the nomination.
   * @param oid The oscar id of the category.
   * @throws IOException
   */
  private void nonCrewCategory(int year, int oid) throws IOException {
    String title    = oscars.get(2);
    int mid         = -1;
    int status      = -1;

    //query for movie id
    mid = queryForMovie(title, year);

    //movie was found
    if (mid != -1) {
      //get the status of the nomination
      status = Integer.parseInt(oscars.get(4));
      //log the find
      log.logData("mid = " + mid, 1, false);
      //write the appropriate SQL insert statement for this nomination
      oscarFileWriter.write("INSERT INTO oscar_given_to VALUES(" + mid + ", " + oid + ", DEFAULT, " + year + ", " + status + ", DEFAULT);");
      oscarFileWriter.write("  -- " + year + " " + getCategoryString(oid) + ": \"" + title + "\"");
      oscarFileWriter.newLine();
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Handles nominations for the acting categories.  The acting categories have
   * their own method (separate from the nonActingCrewCategory() method)
   * because the recipient attribute values for the acting categories in the
   * CSV file have the character names as well, so they must be handled
   * appropriately.
   *
   * @param year An integer for the year of the nomination.
   * @param oid An integer for the oscar category unique id.
   * @throws IOException
   */
  private void actingCategory(int year, int oid) throws IOException {
    //get the title as defined in the record
    String title = oscars.get(3);

    int startIdx = 0;
    int endIdx   = title.indexOf(" {"); //'{' is where the character name starts

    //this nominee is nominated for more than one movie in the same nomination
    if (endIdx != -1) {
      while (true) {
        //the last title in the nomination
        if (title.substring(startIdx).startsWith("and")) {
          startIdx += 4;
        }
        //extract the next movie title
        String nextTitle = title.substring(startIdx, endIdx);
        //query for the movie unique id
        int mid = queryForMovie(nextTitle, year);
        //write the appropriate sql for this nomination
        writeOscarSql(nextTitle, oscars.get(2).split(" "), mid, oid, oscars.get(2), 0);
        //reset the indices for the next title
        startIdx = title.indexOf(";", endIdx);
        if (startIdx == -1) {
          //no more titles for this nomination
          break;
        }
        startIdx += 2;
        endIdx   = title.indexOf(" {", startIdx);
      }
    }
    //nomination is for only one movie (the usual case)
    else {
      //query for the movie unique id
      int mid = queryForMovie(title, year);
      //write the appropriate sql for this nomination
      writeOscarSql(title, oscars.get(2).split(" "), mid, oid, oscars.get(2), 0);
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Handles categories that have a recipient, but are not acting categories.
   *
   * @param year The year of the nomination.
   * @param oid The unique id of the category.
   * @throws IOException
   */
  private void nonActingCrewCategory(int year, int oid) throws IOException {
    int titleIndex = -1;  //index into the CSV record for the title
    int recIndex   = -1;  //index into the CSV record for the recipient
    
    //for some stupid reason, the CSV data switches the order of the attributes
    // for Best Cinematography in 1930 (Best Cinematography oid = 9), Best
    // Director in 1931 (oid == 6), and all of the writing categories in 1930
    // (oid == 10, 11, or 14)
    if (year < 1931 && oid == 6 || year < 1930 && (oid == 9 || isWritingCategory(oid))) {
      titleIndex = 3;
      recIndex   = 2;
    }
    else {
      titleIndex = 2;
      recIndex   = 3;
    }
    //get the title as defined in the record
    String title = oscars.get(titleIndex);

    int startIdx = 0;
    int endIdx   = title.indexOf(";"); 

    //this nominee is nominated for more than one movie in the same nomination
    if (endIdx != -1) {
      while (endIdx != -1) {
        //extract the next movie title
        String nextTitle = title.substring(startIdx, endIdx);
        //create and write the appropriate sql insert statement for this nomination
        nonActingCrewCategoryHelper(nextTitle, year, oid, recIndex);
        //reset the indices for the next title
        startIdx = endIdx + 2;
        endIdx   = title.indexOf(";", endIdx + 1);
      }

      String lastTitle = title.substring(startIdx+4);
      //create and write the appropriate sql insert statement for this nomination
      nonActingCrewCategoryHelper(lastTitle, year, oid, recIndex);
    }
    //nomination is for only one movie (the usual case)
    else {
      //create and write the appropriate sql insert statement for this nomination
      nonActingCrewCategoryHelper(title, year, oid, recIndex);
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Helper method for the nonActingCrewCategory() method.  Processes the
   * recipients accordingly.
   *
   * @param title The title of the movie.
   * @param year The year of the movie.
   * @param oid The unique id of the category.
   * @param recIdx The index into the CSV record for the recipient.
   * @throws IOException
   */
  private void nonActingCrewCategoryHelper(String title, int year, int oid, int recIdx) throws IOException {
    //query for movie id
    int mid = queryForMovie(title, year);

    //get the value of the recipient attribute in the CSV file
    String recipientString = oscars.get(recIdx);
    //for some reason, 1930 records for writing categories and Best
    // Cinematography (oid == 9), as well as 1962 black & white cinematography
    // (oid == 7) have most of the recipients inside parentheses
    if ((year == 1930 && (oid == 9 || isWritingCategory(oid)))
         || (year == 1962 && oid == 7)) {
      recipientString = recipientString.replace("(","").replace(")","");
    }
    //in screenplay categories for years 2000 and on, some recipient values
    // contain " & " and/or " and " rather than ", " to separate the
    // different recipients
    else if (year >= 2000 && isWritingCategory(oid) && !recipientString.equals("Joel and Ethan Coen")) {
      recipientString = recipientString.replace(" & ",", ");
      recipientString = recipientString.replace(" and ",", ");
    }

    //get the number of recipients for this nomination
    int numRecipients = recipientString.split(",").length;

    //nomination has more than one recipient
    if (numRecipients > 1) {
      int startIdx = 0;   //an index into the original CSV string indicating
                          // the beginning of the next recipient
      int endIdx = recipientString.indexOf(",");  //ditto for the end
      String recipient = null;

      //endIdx will be -1 when there is only one more recipient left to process
      while (endIdx != -1) {
        //extract the recipient name
        recipient = recipientString.substring(startIdx, endIdx);
        //pass the name (as an array of Strings) to the second helper method
        writeOscarSql(title, recipient.split(" "), mid, oid, recipient, numRecipients-1);
        //reset the indices for the next recipient
        startIdx = endIdx + 2;
        endIdx = recipientString.indexOf(",", startIdx);
      }
      //process the last recipient
      recipient = recipientString.substring(startIdx);
      writeOscarSql(title, recipient.split(" "), mid, oid, recipient, numRecipients-1);
    }
    //only one recipient for this nomination
    else {
      writeOscarSql(title, recipientString.split(" "), mid, oid, recipientString, 0);
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Creates and writes the appropriate SQL INSERT statement for this
   * nomination.
   *
   * @param title The movie title for this nomination.
   * @param name An array of Strings for the recipient of the nomination.
   * @param mid The unique movie id from the database.
   * @param oid The appropriate oid for this oscar category.
   * @param nameString The name of the recipient as one String. 
   * @param share The number of other recipients this nominee is sharing the
   * nomination with.
   * @throws IOException
   */
  private void writeOscarSql(String title, String[] name, int mid, int oid, String nameString, int share) throws IOException {
    //clean any special cases
    if (name.length >= 3) {
      name = checkForNameSpecialCases(name);
    }
    //a special special case: "Joel Coen" -> "Joel and Ethan Coen"
    else if (oid == 6 && name.length == 2 && name[1].equals("Coen")) {
      String[] temp = {"Joel and Ethan", "Coen"};
      name = temp;
    }
    //get the crew person id for this recipient from the database
    int cid = queryForCrewperson(name, nameString);
    //recipient was found in the database, write the sql statement (if the movie was found too)
    if (cid != -1 && mid != -1) {
      //get the status of the nomination
      int status = Integer.parseInt(oscars.get(4));
      //log what was found
      log.logData("mid = " + mid, 1, false);
      log.logData("cid = " + cid, 1, false);
      //write the sql insert statement for this nomination
      oscarFileWriter.write("INSERT INTO oscar_given_to VALUES(" + mid + ", " + oid + ", " + cid + ", " + Integer.parseInt(oscars.get(0))  + ", " + status + ", " + share + ");");
      oscarFileWriter.write("  -- " + oscars.get(0) + " " + getCategoryString(oid) + ": " + nameString + " for \"" + title + "\"");
      oscarFileWriter.newLine();
      //write the sql insert statement for this person working on this movie
      workedFileWriter.write("INSERT INTO worked_on VALUES(" + mid + ", " + cid + ", '" + getOccupation(oscars.get(1)) + "');");
      workedFileWriter.write("  -- " + nameString + " worked on " + title + " as " + getOccupation(oscars.get(1)));
      workedFileWriter.newLine();
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Determines if the given oscar category id is for a writing category.
   *
   * @param oid The unique id of the category.
   * @return boolean
   */
  private boolean isWritingCategory(int oid) {
    switch (oid) {
      case 10:
      case 11:
      case 14: return true;
      default: return false;
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Writes a sql insert statement for the given movie.  The user is prompted
   * for the values of the other attributes for the db movie table.
   *
   * @param title The title of the movie as a String.
   * @param year The year of the movie (as per the CSV file). 
   * @return unique id of the movie if the user provides it, -1 otherwise.
   */
  private int writeMovieSql(String title, int year) {
    String response = null;
    int mpaa = 0;
    String country = null;

    try {
      //ask user if the title of the movie as it appears in the CSV file is ok
      System.out.print("\"" + title + "\" (" + year + ") not found for "
                        + oscars.get(1) + ". Title ok? (also type 'n' if you already have the correct movie id) ");
      response = stdinReader.readLine();
      //title is not ok, prompt user for a new title
      if (!response.toLowerCase().equals("y")) {
        System.out.print("  Do you have the correct id for this movie? ");
        response = stdinReader.readLine();
        if (response.equals("y")) {
          int mid = -1;
          while (true) {
            System.out.print("  ID: ");
            //make sure user input is valid
            try {
              mid = Integer.parseInt(stdinReader.readLine());
              if (mid < 1) {
                throw new NumberFormatException();
              }
              /* it is assumed that the id is in fact in the database at this point */
              break;
            }
            catch (NumberFormatException nfe) {
              System.out.println("  **oops -- not a valid id number.");
            }
          }
          return mid;
        }
        System.out.print("  Enter new title: ");
        title = stdinReader.readLine();
      }
      title = title.replace("'","''");
      //prompt user for year (most of the time this will be the same as the
      // oscar year, but sometimes the year the movie was actually made/
      // released is a year or two before the oscars--such as the case in
      // some foreign films.
      System.out.print("  Is the year ok? ");
      response = stdinReader.readLine();
      //year not ok, prompt for a new year
      if (!response.toLowerCase().equals("y")) {
        while (true) {
          System.out.print("  Year: ");
          //make sure input is valid
          try {
            year = Integer.parseInt(stdinReader.readLine());
            if (year < 1900 || year > 2014) {
              throw new NumberFormatException();
            }
            break;
          }
          catch (NumberFormatException nfe) {
            System.out.println("  **Oops -- not a valid year.");
          }
        }
      }
      //prompt user for the mpaa rating of the movie (if the movie is in the
      // years of the MPAA rating system)
      if (year >= 1968) {
        while (true) {
          System.out.print("  MPAA (0=NR, 1=G, 2=PG, 3=PG-13, 4=R, 5=X, 6=NC-17): ");
          //make sure the user's entry is valid
          try {
            mpaa = Integer.parseInt(stdinReader.readLine());
            if (mpaa < 0 || mpaa > 6) {
              throw new NumberFormatException();
            }
            break;
          }
          catch (NumberFormatException nfe) {
            System.out.println("**Oops--not a valid mpaa id.");
          }
        }
      }
      //convert the mpaa integer to its respective string representation
      String Mpaa = convertToMpaaString(mpaa);
      //this should never happen
      if (Mpaa == null) {
        Mpaa = "DEFAULT";
      }
      //add apostophes around the mpaa rating
      else {
        Mpaa = "'" + Mpaa + "'";
      }
      //prompt the user for the country of the movie
      System.out.print("  Country (or 'u' for unknown): ");
      country = stdinReader.readLine();
      if (country.equals("u")) {
        country = "DEFAULT";
      }
      else {
        country = "'" + country + "'";
      }
      //open the movie sql if not already opened
      if (movieFileWriter == null) {
        openMovieSqlFile();
      }
      //write the appropriate sql for this movie
      movieFileWriter.write("INSERT INTO movie VALUES (" + nextMid + ", '" + title + "', " + year + ", 'not seen', " + Mpaa + ", " + country + ", NULL);");
      movieFileWriter.write("  -- nominated for Academy Award: " + oscars.get(1));
      movieFileWriter.newLine();
      log.logGeneralMessage("\"" + title + "\" (" + year + ") has been written to the new movie sql file.", 1, false);
      ++nextMid;
    }
    catch (IOException ioe) {
      log.logWarning("IOException caught in writeMovieSql().",1,false);
    }

    return -1;
  }

  //--------------------------------------------------------------------------

  /**
   * Writes a sql insert statement for the given person.
   *
   * @param name The name of the person as a String (as seen in the CSV file).
   * @param first The person's first name.
   * @param middle The person's middle name.
   * @param last The person's last name. (This is assumed not to be null)
   * @return unique id of the recipient if the user provides it, -1 otherwise.
   */
  private int writeCrewSql(String name, String first, String middle, String last) {
    String response = null;

    try {
      System.out.println("Crew person not found for " + oscars.get(0) + " " + oscars.get(1) + ":");
      //show the first, middle, and last name to the user
      if (first != null) {
        System.out.print("  FIRST: " + first);
      }
      if (middle != null) {
        System.out.print(" MIDDLE: " + middle);
      }
      System.out.print(" LAST: " + last + " -- Ok? ");
      //are these names okay for the database?
      response = stdinReader.readLine();
      //the names are not ok
      if (!response.toLowerCase().equals("y")) {
        //maybe the name is in the database, but it just didn't find it.  Can
        // the user provide the crew id?
        System.out.print("  Do you have the correct id for this recipient? ");
        response = stdinReader.readLine();
        //user does have the id
        if (response.equals("y")) {
          int cid = -1;
          while (true) {
            System.out.print("  ID: ");
            //make sure user input is valid
            try {
              cid = Integer.parseInt(stdinReader.readLine());
              if (cid < 1) {
                throw new NumberFormatException();
              }
              /* it is assumed that the id is in fact in the database at this point */
              break;
            }
            catch (NumberFormatException nfe) {
              System.out.println("  **oops -- not a valid id number.");
            }
          }
          return cid;
        }

        //prompt the user for each name
        System.out.print("  Enter first name (or 'i' to ignore, or 'k' to keep the same): ");
        response = stdinReader.readLine();
        if (!response.equals("k")) {
          if (response.equals("i")) {
            first = null;
          }
          else {
            first = response;
          }
        }
        System.out.print("  Enter middle name (or 'i' to ignore, or 'k' to keep the same): ");
        response = stdinReader.readLine();
        if (!response.equals("k")) {
          if (response.equals("i")) {
            middle = null;
          }
          else {
            middle = response;
          }
        }
        System.out.print("  Enter last name (or 'k' to keep the same): ");
        response = stdinReader.readLine();
        if (!response.equals("k")) {
          last = response;
        }
      }

      //surround the names with apostrophes for sql
      if (first != null) {
        first = "'" + first + "'";
      }
      else {
        first = "DEFAULT";
      }
      if (middle != null) {
        middle = "'" + middle + "'";
      }
      else {
        middle = "DEFAULT";
      }
      last = "'" + last + "'";
      
      //determine the position (director, actor, screenwriter, etc) of the crew person
      String position = "'" + getOccupation(oscars.get(1)) + "'";
      //this should never happen
      if (position.equals("'**Unknown occupation**'")) {
        position = "DEFAULT";
      }

      //open the crew_person sql file if not already opened
      if (crewFileWriter == null) {
        openCrewSqlFile();
      }
      //write the sql insert statement for this person to the appropriate file
      crewFileWriter.write("INSERT INTO crew_person VALUES (" + nextCid + ", " + last + ", " + first + ", " + middle + ", " + position + ");");
      crewFileWriter.write("  -- " + position);
      crewFileWriter.newLine();
      log.logGeneralMessage("\"" + first + " " + middle + " " + last + " has been written to new crew sql file.", 1, false);
      ++nextCid;
    }
    catch (IOException ioe) {
      log.logWarning("IOException caught in writeCrewSql().",1,false);
    } 

    return -1;
  }

  //--------------------------------------------------------------------------

  /**
   * Returns the String representation of the oscar category given the oid.
   *
   * @param oid The oscar category's unique id.
   * @return The category name as a String.
   */
  private String getCategoryString(int oid) {
    switch(oid) {
      case  1: return "Best Picture";
      case  2: return "Best Actor";
      case  3: return "Best Actress";
      case  4: return "Best Supporting Actor";
      case  5: return "Best Supporting Actress";
      case  6: return "Best Director";
      case  7: return "Best Cinematography (black & white)";
      case  8: return "Best Cinematography (color)";
      case  9: return "Best Cinematography";
      case 10: return "Best Adapted Screenplay";
      case 11: return "Best Original Screenplay";
      case 12: return "Best Foreign Language Film";
      case 13: return "Best Documentary";
      case 14: return "Best Screenplay";
      default: return "**Unknown category**";
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Returns an occupation of the nominee based on the given category String.
   *
   * @param category The category of the nomination.
   * @return A String for the occupation of the nominee ("Cinematographer",
   * "Director", etc).
   */
  private String getOccupation(String category) {
    if (category.contains("Screen")) {
      return "Screenwriter";
    }
    if (category.startsWith("Best C")) {
      return "Cinematographer";
    }
    if (category.startsWith("Best D")) {
      return "Director";
    }
    if (category.contains("Actor")) {
      return "Actor";
    }
    if (category.contains("Actress")) {
      return "Actress";
    }
    return "**Unknown occupation**";
  }

  //--------------------------------------------------------------------------

  /**
   * Tests whether the given title is a title known to only contain stop words
   * for Postgres's full text search.
   *
   * For example, "Being There":
   *   Postgres's full text search includes both 'being' and 'there' as stop
   *   words, so they are ignored when searching.  Since this title only
   *   contains stop words, nothing is searched for, and thus is not found.
   *
   * @param title A movie title.
   * @return boolean
   */
  private boolean containsOnlyStopWords(String title) {
    if (title.equals("Being There")
        || title.equals("In & Out")
        || title.equals("To Be or Not to Be")
        || title.equals("To Each His Own")
        || title.equals("This Above All")) {
      return true;
    }
    return false;
  }

  //--------------------------------------------------------------------------

  /**
   * Checks to see if this movie is a special case where full text search
   * matches to the wrong movie.
   *
   * The odds of this happening is very low, but it has happened a few times:
   * the title matches to only one movie that just happens to be in the same
   * year as the one being searched for.
   *
   * @param title A movie title.
   * @return boolean
   */
  private boolean isSpecialCase(String title) {
    if (title.equals("Water")
        || title.equals("Evil")
        || title.equals("On Any Sunday")
        || title.equals("The Field")
        || title.equals("My Country, My Country")) {
      return true;
    }
    return false;
  }

  //--------------------------------------------------------------------------

  /**
   * Converts the given integer to its respective MPAA rating string.
   *
   * @param mpaa An integer representing an MPAA rating.
   * @return String
   */
  private String convertToMpaaString(int mpaa) {
    switch(mpaa) {
      case 0: return "NR";
      case 1: return "G";
      case 2: return "PG";
      case 3: return "PG-13";
      case 4: return "R";
      case 5: return "X";
      case 6: return "NC-17";
      default: return null;
    }
  }
}
