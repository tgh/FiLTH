import java.io.*;
import tylerhayes.tools.*;
import java.sql.*;
import com.csvreader.*;
import java.util.*;

/**
 * This program is used in the scripts/oscarGivenTo.sh shell script to help
 * populate the oscar_given_to table in the database.
 * The scripts/oscarGivenTo.sh shell script massages the original
 * data/oscars.csv file into the data/oscarsOfCategory.csv file.  This
 * program then parses that csv file and outputs appropriate sql insert
 * statements for inserting the proper data into the oscar_given_to table.
 * The shell script should redirect this output to a sql/ file.
 */
public class OscarParser implements GracefulShutdown {

  //absolute path to FiLTH project
  private String filthPath = "/home/tgh/Projects/FiLTH";
  //for a file to log certain actions and events
  private Log log = null;
  //for a connection to the database
  private Connection dbConn = null;
  //Csv file reader for the csv file
  private CsvReader oscars = null;
  //for file output to oscar_given_to.sql
  private BufferedWriter bw = null;
  //for reading from stdin
  private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  //name of the db
  private static String dbname;
  //password for db
  private static String dbpw;
  //flag for no command-line arg
  private static boolean noarg;
  //for querying the database
  private PostgreSQLConsole db = null;
  //these maps are used as cache storage for mid's and cid's from the database
  // The title used as keys in the move map are the cleaned titles used to
  // query the db.
  private HashMap<String, Integer> movieMap = null;
  private HashMap<String, Integer> crewMap  = null;

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

    //open the sql file we are going to write to
    try {
      bw = new BufferedWriter(new FileWriter("/home/tgh/Projects/FiLTH/sql/oscar_given_to.sql"));
    }
    catch (IOException ioe) {
      log.logFatalError("Unable to create/open oscar_given_to.sql",0,false);
      ioe.printStackTrace();
      System.exit(1); 
    }

    //create a BufferedReader to read from stdin
    br = new BufferedReader(new InputStreamReader(System.in));

    //connect to the database
    dbConn = DatabaseConnector.connectToPostgres("jdbc:postgresql://localhost/" + dbname,
                                                 "postgres",
                                                 dbpw);
    //setup virtual SQL console with the db
    db = new PostgreSQLConsole(dbConn);

    //init the hash maps
    movieMap = new HashMap<String, Integer>();
    crewMap  = new HashMap<String, Integer>();

    log.logHeader("START");

    //parse the csv file
    try {
      while (oscars.readRecord()) {
        //get the year of the oscar nomination
        int year = Integer.parseInt(oscars.get(0));
        //get the category of the nomination
        String category = oscars.get(1);
        String title = null; //the movie titles aren't always in the same field slot (depends on the category)
        int mid      = 0;    //movie id to be retrieved from the database
        int cid      = 0;    //crew person id to be retrieved from the database
        int status   = 0;    //just nominated = 0, win = 1, tie = 2

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
          acting(year, 2);
        }

        /*---------- BEST ACTRESS (oid = 3) ---------------------------------*/

        if (category.equals("Best Actress")) {
          acting(year, 3);
        }

        /*---------- BEST SUPPORTING ACTOR (oid = 4) ------------------------*/

        if (category.equals("Best Supporting Actor")) {
          acting(year, 4);
        }

        /*---------- BEST SUPPORTING ACTRESS (oid = 5) ----------------------*/

        if (category.equals("Best Supporting Actress")) {
          acting(year, 5);
        }

        /*---------- BEST DIRECTOR (oid = 6) --------------------------------*/

        if (category.equals("Best Director")) {
          director(year, 6);
        }

        /*---------- BEST CINEMATOGRAPHY (oid = 7) --------------------------*/

        if (category.equals("Best Cinematography")) {
          cinematography(year, 7);
        }

        /*---------- BEST CINEMATOGRAPHY (b & w) (oid = 8) ------------------*/

        if (category.equals("Best Cinematography (black and white)")) {
        }

        /*---------- BEST CINEMATOGRAPHY (color) (oid = 9) ------------------*/

        if (category.equals("Best Cinematography (color)")) {
        }

        /*---------- BEST ORIGINAL SCREENPLAY (oid = 10) --------------------*/

        if (category.equals("Best Original Screenplay")) {
        }

        /*---------- BEST ADAPTED SCREENPLAY (oid = 11) ---------------------*/

        if (category.equals("Best Adapted Screenplay")) {
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

        //1929 and 1930 had "WRITING" as a category--did not distinguish
        // between original or adapted screenplays
        if (category.equals("Best Screenplay")) {
        }
      }
    }
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
        bw.close();
        br.close();
      }
      catch (IOException ioe) {
        System.out.println("Error in closing bw or br.");
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
   * Handles creating and writing the appropriate SQL insert statments for
   * Oscar categories that do not have a crew person recipient. (e.g. Best
   * Picture, Best Foreign Language Film, Best Documentary).
   *
   * @param year An integer for the year of the nomination.
   * @param oid The oscar id of the category.
   * @throws IOException
   */
  private void nonCrewCategory(int year, int oid) throws IOException {
    String title    = null;
    int mid         = -1;
    int status      = -1;

    //clean up the title
    title = checkForSpecialCases(oscars.get(2));
    title = title.toLowerCase().replace("'","''");
    title = title.replace(" & "," ").replace(" ","&").replace("!","");
    //query for movie id
    mid = queryForMovie(title, oscars.get(2), year);

    //movie was found
    if (mid != -1) {
      //get the status of the nomination
      status = Integer.parseInt(oscars.get(4));
      //log the find
      log.logData("mid = " + mid, 1, false);
      //write the appropriate SQL insert statement for this nomination
      bw.write("INSERT INTO oscar_given_to VALUES(" + mid + ", " + oid + ", DEFAULT, " + status + ");");
      bw.newLine();
    }
  }

  //--------------------------------------------------------------------------

  /**
   * There are going to be quite a few special cases probably, where some
   * movies just aren't going to be found in the database even though I've
   * seen them.  This method checks for those special (hard-coded) cases.
   */
  private String checkForSpecialCases(String title) {
    //For some reason, when a title has a token that starts with an apostrophe,
    // but does not end with one (e.g. "Give 'em Hell Harry!" and "Adalen '31",
    // but not "Pete 'n' Tillie"), Postgres's full text search fails, or rather
    // a SQL syntax error is given.  Adding another apostrophe to the end of
    // the token fixes this (e.g. "Adalen '31" becomes "Adalen '31'").  Shrug.
    if (title.contains(" '") && !title.contains("' ")) {
      int i = title.indexOf(" '");
      int len = title.length();
      char[] chars = title.toCharArray();
      //where does this token end?
      for (i += 2; i < len && chars[i] != ' '; ++i) {
        ;
      }
      //the token was the end of the title
      if (i == len) {
        return title + "'";
      }
      //token was not at the end of the title
      String sub1 = title.substring(0,i);
      String sub2 = "'" + title.substring(i, len);
      return sub1 + sub2;
    }
    if (title.equals("Good Fellas")) {
      return "GoodFellas";
    }
    if (title.contains("Meredith Willson")) {
      return "The Music Man";
    }
    if (title.contains("Il Postino")) {
      return "Il Postino";
    }
    if (title.equals("Sunset Blvd.")) {
      return "Sunset Boulevard";
    }
    if (title.contains("Les Choristes")) {
      return "The Chorus";
    }
    if (title.contains("Goodbye, Children")) {
      return "Au Revoir, Les Enfants";
    }
    if (title.contains("8-1/2")) {
      return "8\u00BD";
    } 
    if (title.contains("Mulholland D")) {
      return "Mulholland Dr.";
    }
    return title;
  }

  //--------------------------------------------------------------------------
  
  /**
   * Like movie title, there are going to be some special cases for people's
   * names; this method takes care of those cases by matching them to how the
   * database schema is designed.
   */
  private String[] checkForNameSpecialCases(String[] name) {
    //names with 'De' (like Robert De Niro) combine De and [name] into one.
    if (name[1].equals("De")) {
      String[] n = {name[0],"De " + name[2]};
      return n;
    }
    //names with 'Jr.' (like Robert Downey, Jr.) include Jr. with last name.
    if (name[2].equals("Jr.")) {
      String[] n = {name[0],name[1] + " Jr."};
      return n;
    }
    //Gus Van Sant
    if (name[1].equals("Van")) {
      String[] n = {"Gus","Van Sant"};
      return n;
    }
    //Conrad Hall
    if (name[0].equals("Conrad") && name[1].equals("L.")) {
      String[] n = {"Conrad","Hall"};
      return n;
    }
    //Noriyuki 'Pat' Morita
    if (name[1].equals("'Pat'")) {
      String[] n = {"Pat","Morita"};
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
    return name;
  }

  //--------------------------------------------------------------------------

  /**
   * Query the database for the movie id of the given movie.
   *
   * @param formattedTitle This String is the cleaned movie title used to
   * query the database.
   * @param realTitle This String is really only used to output to stdout when
   * communicating to the user (it's the title as seen in the csv file record).
   * @param year An integer for the year of the movie in order to nail down the
   * specific movie.
   * @return The database's unique id of the movie if found, -1 otherwise.
   */
  private int queryForMovie(String formattedTitle, String realTitle, int year) {
    //first, see if the movie is in cache (hash map)
    Integer Mid = movieMap.get(formattedTitle + " " + year);
    //movie was in cache, no need to query database
    if (Mid != null) {
      return Mid.intValue();
    }

    //movie was not in cache, need to query database
    ResultSet qResult = null;
    int mid = -1;
    try {
      //special case: title containing only stop words
      //  For example, "Being There":
      //  Postgres's full text search includes both 'being' and 'there' as stop
      //  words, so they are ignored when searching.  Since this title only
      //  contains stop words, nothing is searched for, and thus is not
      //  found.
      if (realTitle.equals("Being There") || realTitle.equals("In & Out")) {
        qResult = db.selectScrollable("SELECT mid, title FROM movie WHERE title = '" + realTitle + "';");
      }
      //special cases: full text search matches to wrong movie
      // The odds of this happening is very low, but it has happened 3 times:
      // the title matches to only one movie that just happens to be in the
      // same year as the one being searched for.
      else if (realTitle.equals("Water") || realTitle.equals("Evil") || realTitle.equals("On Any Sunday")) {
        qResult = db.selectScrollable("SELECT mid, title FROM movie WHERE title = '" + realTitle + "';");
      }
      //query for the movie
      else {
        qResult = db.selectScrollable("SELECT mid, title FROM movie WHERE to_tsquery('"
                                      + formattedTitle + "') " + "@@ to_tsvector(lower(title)) and "
                                      + "(year = " + year + " or year = " + (year-1) + ");");
      }
      //movie(s) found in db
      if (qResult.next()) {

        /* single movie found */
        if (!qResult.next()) {
          qResult.previous();
          mid = qResult.getInt(1);
        }
        /* multiple movies matched title */
        else {
          String response = null; //for user's input

          //show the title of the movie of the current csv row
          System.out.println("This movie has multiple matches: " + realTitle);
          //go back to the beginning of the query results
          qResult.previous();
          //loop through the query results
          while (true) {
            //ask user if this is the right movie
            System.out.print("  - " + qResult.getString(2) + " ? ");
            //get user's response
            response = br.readLine();
            //this one matches the movie in the csv file
            if (response.toLowerCase().equals("y")) {
              mid = qResult.getInt(1);
              break;
            }
            //no more matches
            if (!qResult.next()) {
              log.logGeneralMessageWithoutIndicator("-- " + realTitle + " not found.",1,false);
              break;
            }
          }
        }
      }
      //no matches found at all
      else {
        log.logGeneralMessageWithoutIndicator("-- " + realTitle + " not found.",1,false);
      }

      //movie found, and to cache (hash map)
      if (mid != -1) {
        movieMap.put(formattedTitle + " " + year, new Integer(mid));
      }

      return mid;
    }
    catch (SQLException sqle) {
      handleSQLException("SQLException caught in queryForMovie().",sqle);
    }
    catch (IOException ioe) {
      System.out.println("IOException caught in queryForMovie().");
      ioe.printStackTrace();
      System.exit(1);
    }

    //unreachable code--shut up compiler
    return -1;
  }

  //--------------------------------------------------------------------------

  /**
   * Query the database for the crew person id of the given person name.
   *
   * @param names The moninee's name but split into an array of Strings.
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
                mname = names[1];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname
                                    + "' AND f_name = '" + fname + "' AND m_name = '" + mname + "';");
                if (qResult.next()) {
                  cid = qResult.getInt(1);
                }
                break;
        default: log.logWarning("Recipient name is not 1, 2, or 3 names in length.", 1, false);
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
    }

    return cid;
  }

  //--------------------------------------------------------------------------

  /**
   * Handle nominations for the acting categories.
   *
   * @param year An integer for the year of the nomination.
   * @param oid An integer for the oscar category unique id.
   * @param category A string for the specific acting category.
   * @throws IOException
   */
  private void acting(int year, int oid) throws IOException {
    //get the title as defined in the record
    String title = oscars.get(3);
    //this actor is nominated for two movies within the same nomination
    int idx = title.indexOf("; and ");
    if (idx != -1) {
      //extract the second movie title
      String uncleanedSecondTitle = title.substring(idx+6, title.indexOf(" {", idx));
      //clean the second movie title
      String secondTitle = checkForSpecialCases(uncleanedSecondTitle);
      secondTitle = secondTitle.toLowerCase().replace("'","''");
      secondTitle = secondTitle.replace(" & "," ").replace(" ","&").replace("!","");
      //create and write the appropriate sql insert statement for this nomination
      actingHelper(secondTitle, uncleanedSecondTitle, year, oid);
    }
    //extract the title (first title if there were two)
    String uncleanedTitle = title.substring(0, title.indexOf(" {"));
    //clean the title
    title = checkForSpecialCases(uncleanedTitle);
    title = title.toLowerCase().replace("'","''");
    title = title.replace(" & "," ").replace(" ","&").replace("!","");
    //create and write the appropriate sql insert statement for this nomination
    actingHelper(title, uncleanedTitle, year, oid);
  }

  //--------------------------------------------------------------------------

  /**
   * Create and write the appropriate SQL insert statement for the current
   * nomination--it is assumed to be a nomination in an acting category.
   *
   * @param title This is the cleaned movie title--needed for a call to
   * queryForMovie().
   * @param realTitle This is the title as seen in the csv record--needed
   * for a call to queryForMovie().
   * @param year An integer for the year of the movie--also needed for the
   * call to queryForMovie().
   * @param oid An integer for the unique id of the oscar category.
   * @throws IOException
   */
  private void actingHelper(String title, String realTitle, int year, int oid) throws IOException {
    //query for movie id
    int mid = queryForMovie(title, realTitle, year);

    //movie was found in database
    if (mid != -1) {
      //get the name of the actor
      String[] name = oscars.get(2).split(" ");
      //clean any special cases
      if (name.length == 3) {
        name = checkForNameSpecialCases(name);
      }
      //get the crew person id for this actor from the database
      int cid = queryForCrewperson(name, oscars.get(2));
      //actor was found in the database
      if (cid != -1) {
        //get the status of the nomination
        int status = Integer.parseInt(oscars.get(4));
        //log what was found
        log.logData("mid = " + mid, 1, false);
        log.logData("cid = " + cid, 1, false);
        //write the sql insert statement for this nomination
        bw.write("INSERT INTO oscar_given_to VALUES(" + mid + ", " + oid + ", " + cid + ", " + status + ");");
        bw.newLine();
      }
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Handles Best Director category nominations.
   *
   * @param year The year of the movie.
   * @param oid The unique oscar category id.
   * @throws IOException
   */
  private void director(int year, int oid) throws IOException {
    int titleIndex = -1;
    int dirIndex   = -1;
    
    if (year < 1931) {
      titleIndex = 3;
      dirIndex   = 2;
    }
    else {
      titleIndex = 2;
      dirIndex   = 3;
    }
    //get the title as defined in the record
    String title = oscars.get(titleIndex);
    //this director is nominated for two movies within the same nomination
    int idx = title.indexOf("; and ");
    int endIdx = title.length();
    if (idx != -1) {
      endIdx = title.indexOf(";");
      //extract the second movie title
      String uncleanedSecondTitle = title.substring(idx+6, title.length());
      //clean the second movie title
      String secondTitle = checkForSpecialCases(uncleanedSecondTitle);
      secondTitle = secondTitle.toLowerCase().replace("'","''");
      secondTitle = secondTitle.replace(" & "," ").replace(" ","&").replace("!","");
      //create and write the appropriate sql insert statement for this nomination
      directorHelper(secondTitle, uncleanedSecondTitle, year, oid, dirIndex);
    }
    //extract the title (first title if there were two)
    String uncleanedTitle = title.substring(0, endIdx);
    //clean the title
    title = checkForSpecialCases(uncleanedTitle);
    title = title.toLowerCase().replace("'","''");
    title = title.replace(" & "," ").replace(" ","&").replace("!","");
    //create and write the appropriate sql insert statement for this nomination
    directorHelper(title, uncleanedTitle, year, oid, dirIndex);
  }

  //--------------------------------------------------------------------------

  /**
   * A helper method for the director() method.  Handles what happens when
   * there are two directors named as the recipients for the nomination.
   *
   * @param title Cleaned String version of the movie title.
   * @param realTitle The title of the movie as written in the CSV file.
   * @param year The year of the movie.
   * @param oid The oscar category unique id for the database.
   * @param dirIdx The index into the CSV file record for the director name.
   * @throws IOException
   */
  private void directorHelper(String title, String realTitle, int year, int oid, int dirIdx) throws IOException {
    //query for movie id
    int mid = queryForMovie(title, realTitle, year);

    //movie was found in database
    if (mid != -1) {
      //get the name of the director
      String[] name = oscars.get(dirIdx).split(" ");
      //nomination has two directors for the movie (no directing nomination has more than 2 recipients)
      if (oscars.get(dirIdx).contains(",")) {
        boolean first = true;         //still on first recipient?
        String firstRecipient = "";   //name of the first recipient
        String secondRecipient = "";  //name of the second recipient

        int len = name.length;
        for (int i = 0; i < len; ++i) {
          //still constructing the name of the first recipient
          if (first) {
            //this name token doesn't contain the ',' separator
            if (!name[i].contains(",")) {
              firstRecipient = firstRecipient + name[i] + " ";
            }
            //this name token is the last one in the first recipient, so add it without the ','
            else {
              firstRecipient = firstRecipient + name[i].substring(0, name[i].indexOf(","));
              first = false;
            }
          }
          //now constructing the second recipient name
          else {
            //this is not the last name token, so add a space character
            if (!(i == len - 1)) {
              secondRecipient = secondRecipient + name[i] + " ";
            }
            //this is the last name token
            else {
              secondRecipient = secondRecipient + name[i];
            }
          }
        }
        directorSecondHelper(firstRecipient.split(" "), mid, oid, firstRecipient);
        directorSecondHelper(secondRecipient.split(" "), mid, oid, secondRecipient);
      }
      //only one recipient for this nomination
      else {
        directorSecondHelper(name, mid, oid, oscars.get(dirIdx));
      }
    }
  }

  //--------------------------------------------------------------------------
  
  /**
   * Create and write the appropriate SQL insert statement for this Best
   * Director nomination.
   *
   * @param name An array of Strings for the recipient of the nomination.
   * @param mid The unique movie id from the database.
   * @param oid The appropriate oid for this oscar category.
   * @param nameString The name of the recipient as one String. 
   * @throws IOException
   */
  private void directorSecondHelper(String[] name, int mid, int oid, String nameString) throws IOException {
    //clean any special cases
    if (name.length >= 3) {
      name = checkForNameSpecialCases(name);
    }
    //a special special case: "Joel Coen" -> "Joel and Ethan Coen"
    else if (name.length == 2 && name[1].equals("Coen")) {
      String[] temp = {"Joel and Ethan", "Coen"};
      name = temp;
    }
    //get the crew person id for this director from the database
    int cid = queryForCrewperson(name, nameString);
    //director was found in the database
    if (cid != -1) {
      //get the status of the nomination
      int status = Integer.parseInt(oscars.get(4));
      //log what was found
      log.logData("mid = " + mid, 1, false);
      log.logData("cid = " + cid, 1, false);
      //write the sql insert statement for this nomination
      bw.write("INSERT INTO oscar_given_to VALUES(" + mid + ", " + oid + ", " + cid + ", " + status + ");");
      bw.newLine();
    }
  }

  //--------------------------------------------------------------------------
  
  /**
   * Handles nominations for "Best Cinematography".
   *
   * @param year The year of the movie.
   * @param oid The unique oscar category id for the nomination.
   * @throws IOException
   */
  private void cinematography(int year, int oid) throws IOException {
    int titleIndex = -1;  //index into the CSV record for the title
    int recIndex = -1;    //index into the CSV record for the recipient
    
    //the CSV data switches the order of the attributes at 1930 for some stupid
    // reason
    if (year < 1930) {
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
        String uncleanedNextTitle = title.substring(startIdx, endIdx);
        //clean the next movie title
        String nextTitle = checkForSpecialCases(uncleanedNextTitle);
        nextTitle = nextTitle.toLowerCase().replace("'","''");
        nextTitle = nextTitle.replace(" & "," ").replace(" ","&").replace("!","");
        //create and write the appropriate sql insert statement for this nomination
        cineHelper(nextTitle, uncleanedNextTitle, year, oid, recIndex);
        //reset the indices for the next title
        startIdx = endIdx + 2;
        endIdx   = title.indexOf(";", endIdx + 1);
      }

      String uncleanedLastTitle = title.substring(startIdx+4, title.length());
      //clean the next movie title
      String lastTitle = checkForSpecialCases(uncleanedLastTitle);
      lastTitle = lastTitle.toLowerCase().replace("'","''");
      lastTitle = lastTitle.replace(" & "," ").replace(" ","&").replace("!","");
      //create and write the appropriate sql insert statement for this nomination
      cineHelper(lastTitle, uncleanedLastTitle, year, oid, recIndex);
    }
    //nomination is for only one movie (the usual case)
    else {
      //clean the title
      String cleanedTitle = checkForSpecialCases(title);
      cleanedTitle = cleanedTitle.toLowerCase().replace("'","''");
      cleanedTitle = cleanedTitle.replace(" & "," ").replace(" ","&").replace("!","");
      //create and write the appropriate sql insert statement for this nomination
      cineHelper(cleanedTitle, title, year, oid, recIndex);
    }
  }

  /**
   * A helper method for the cinematography() method.  Handles what happens when
   * there are two or more cinematographers named as the recipients for the
   * nomination.
   *
   * @param title Cleaned String version of the movie title.
   * @param realTitle The title of the movie as written in the CSV file.
   * @param year The year of the movie.
   * @param oid The oscar category unique id for the database.
   * @param recIndex The index into the CSV file record for the
   * cinematographer(s) name(s).
   * @throws IOException
   */
  private void cineHelper(String title, String realTitle, int year, int oid, int recIdx) throws IOException {
    //query for movie id
    int mid = queryForMovie(title, realTitle, year);

    //movie was found in database
    if (mid != -1) {
      //get the value of the recipient attribute in the CSV file
      String recipientString = oscars.get(recIdx);
      //for some reason, 1930 records have most of the recipients inside parentheses
      if (year == 1930) {
        recipientString = recipientString.replace("(","").replace(")","");
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
          cineSecondHelper(recipient.split(" "), mid, oid, recipient);
          //reset the indeices for the next recipient
          startIdx = endIdx + 2;
          endIdx = recipientString.indexOf(",", startIdx);
        }
        //process the last recipient
        recipient = recipientString.substring(startIdx);
        cineSecondHelper(recipient.split(" "), mid, oid, recipient);
      }
      //only one recipient for this nomination
      else {
        cineSecondHelper(recipientString.split(" "), mid, oid, recipientString);
      }
    }
  }

  //--------------------------------------------------------------------------

  /**
   * Create and write the appropriate SQL insert statement for this Best
   * Cinematography nomination.
   *
   * @param name An array of Strings for the recipient of the nomination.
   * @param mid The unique movie id from the database.
   * @param oid The appropriate oid for this oscar category.
   * @param nameString The name of the recipient as one String. 
   * @throws IOException
   */
  private void cineSecondHelper(String[] name, int mid, int oid, String nameString) throws IOException {
    //clean any special cases
    if (name.length >= 3) {
      name = checkForNameSpecialCases(name);
    }
    //get the crew person id for this recipient from the database
    int cid = queryForCrewperson(name, nameString);
    //recipient was found in the database
    if (cid != -1) {
      //get the status of the nomination
      int status = Integer.parseInt(oscars.get(4));
      //log what was found
      log.logData("mid = " + mid, 1, false);
      log.logData("cid = " + cid, 1, false);
      //write the sql insert statement for this nomination
      bw.write("INSERT INTO oscar_given_to VALUES(" + mid + ", " + oid + ", " + cid + ", " + status + ");");
      bw.newLine();
    }
  }
}
