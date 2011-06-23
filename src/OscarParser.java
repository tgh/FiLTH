import java.io.*;
import tylerhayes.tools.*;
import java.sql.*;
import com.csvreader.*;

/**
 * This program is used in the scripts/oscarGivenTo.sh shell script to help
 * populate the oscar_given_to table in the database.
 * The scripts/oscarGivenTo.sh shell script massages the original
 * data/oscars.csv file into the data/oscarsOfCategory.csv file.  This
 * program then parses that csv file and outputs appropriate sql insert
 * statements for inserted the proper data into the oscar_given_to table.
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

        /*---------- BEST PICTURE (oid = 1) ---------------------------------*/

        if (category.equals("Best Picture")) {
          //clean up the title
          title = checkForSpecialCases(oscars.get(2));
          title = title.toLowerCase().replace("'","''");
          title = title.replace(" & "," ").replace(" ","&").replace("!","");
          //get the corresponding movie id from the database (if there)
          mid = queryForMovie(title, oscars.get(2), year);
          //movie was found in database
          if (mid != -1) {
            //get the status of the nomination
            status = Integer.parseInt(oscars.get(4));
            //log the find
            log.logData("mid = " + mid, 1, false);
            //write the appropriate SQL insert statement for this nomination
            try {
              bw.write("INSERT INTO oscar_given_to VALUES(" + mid + ", 1, DEFAULT, " + status + ");");
              bw.newLine();
            }
            catch (IOException ioe) {
              log.logFatalError("Writing insert statement for best picture.",0,false);
              ioe.printStackTrace();
              System.exit(1); 
            }
          }
        }

        /*---------- BEST ACTOR (oid = 2) -----------------------------------*/

        if (category.equals("Best Actor")) {
          //get the title as defined in the record
          title = oscars.get(3);
          //this actor is nominated for two movies within the same nomination
          int idx = title.indexOf("; and ");
          if (idx != -1) {
            //extract the second movie title
            String uncleanedSecondTitle = title.substring(idx+6, title.indexOf(" {", idx));
            //clean the second movie title
            String secondTitle = checkForSpecialCases(uncleanedSecondTitle);
            secondTitle = secondTitle.toLowerCase().replace("'","''");
            secondTitle = secondTitle.replace(" & "," ").replace(" ","&").replace("!","");
            //create and write the appropriate 'Best Actor' sql insert statement
            // for this nomination
            bestActor(secondTitle, uncleanedSecondTitle, year);
          }
          //extract the title (first title if there were two)
          String uncleanedTitle = title.substring(0, title.indexOf(" {"));
          //clean the title
          title = checkForSpecialCases(uncleanedTitle);
          title = title.toLowerCase().replace("'","''");
          title = title.replace(" & "," ").replace(" ","&").replace("!","");
          //create and write the appropriate 'Best Actor' sql insert statement
          // for this nomination
          bestActor(title, uncleanedTitle, year);
        }

        /*---------- BEST ACTRESS (oid = 3) ---------------------------------*/

        if (category.equals("Best Actress")) {
        }

        /*---------- BEST SUPPORTING ACTOR (oid = 4) ------------------------*/

        if (category.equals("Best Supporting Actor")) {
        }

        /*---------- BEST SUPPORTING ACTRESS (oid = 5) ----------------------*/

        if (category.equals("Best Supporting Actress")) {
        }

        /*---------- BEST DIRECTOR (oid = 6) --------------------------------*/

        if (category.equals("Best Director")) {
        }

        /*---------- BEST CINEMATOGRAPHY (oid = 7) --------------------------*/

        if (category.equals("Best Cinematography")) {
        }

        /*---------- BEST CINEMATOGRAPHY (b & w) (oid =81) ------------------*/

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
   * There are going to be quite a few special cases probably, where some
   * movies just aren't going to be found in the database even though I've
   * seen them.  This method checks for those special (hard-coded) cases.
   */
  private String checkForSpecialCases(String title) {
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
    if (title.equals("Give 'em Hell, Harry!")) {
      return "Give em Hell, Harry!";
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
   */
  private int queryForMovie(String formattedTitle, String realTitle, int year) {
    int mid = 0;
    int status = 0;
    String response = null;
    ResultSet qResult = null;

    try {
      //special case: Being There
      //  Postgres's full text search includes both 'being' and 'there' as stop
      //  words, so they are ignored when searching.  Since this title only
      //  contains stop words, nothing is searched for, and thus is not
      //  found.
      if (realTitle.equals("Being There")) {
        qResult = db.selectScrollable("SELECT mid, title FROM movie WHERE title = 'Being There';");
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
          return qResult.getInt(1);
        }

        /* multiple movies matched title */
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
            return qResult.getInt(1);
          }
          //no more matches
          if (!qResult.next()) {
            log.logGeneralMessageWithoutIndicator("-- " + realTitle + " not found.",1,false);
            break;
          }
        }
      }
      //no matches found at all
      else {
        log.logGeneralMessageWithoutIndicator("-- " + realTitle + " not found.",1,false);
      }
    }
    catch (SQLException sqle) {
      handleSQLException("SQLException caught in queryForMovie().",sqle);
    }
    catch (IOException ioe) {
      System.out.println("IOException caught in queryForMovie().");
      ioe.printStackTrace();
      System.exit(1);
    }

    //no match for the movie
    return -1;
  }

  //--------------------------------------------------------------------------

  /**
   * Query the database for the crew person id of the given person name.
   */
  private int queryForCrewperson(String[] names) {
    String lname = null;
    String fname = null;
    String mname = null;
    ResultSet qResult = null;

    try {
      switch(names.length) {
        //e.g. Cher, Madonna, etc.
        case 1: lname = names[0];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname + "' AND f_name is NULL;");
                if (qResult.next()) {
                  return qResult.getInt(1);
                }
                break;
        //standard first name, last name
        case 2: lname = names[1].replace("'","''");
                fname = names[0];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname
                                    + "' AND f_name = '" + fname + "';");
                if (qResult.next()) {
                  return qResult.getInt(1);
                }
                break;
        //e.g. Phillip Seymour Hoffman, Samuel L. Jackson, etc.
        case 3: lname = names[2].replace("'","''");
                fname = names[0];
                mname = names[1];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname
                                    + "' AND f_name = '" + fname + "' AND m_name = '" + mname + "';");
                if (qResult.next()) {
                  return qResult.getInt(1);
                }
                break;
        default: log.logWarning("Recipient name is not 1, 2, or 3 names in length.", 1, false);
                 break;
      }
    }
    catch (SQLException sqle) {
      handleSQLException("SQLException caught in queryForCrewperson().", sqle);
    }

    //recipient name was not found or was empty or more than 3 names long
    log.logGeneralMessageWithoutIndicator("-- Crewperson not found.", 1, false);
    return -1;
  }

  //--------------------------------------------------------------------------

  /**
   * Create and write the appropriate SQL insert statement for the current
   * nomination--it is assumed to be a Best Actor nomination.
   *
   * @param title This is the cleaned movie title--needed for a call to
   * queryForMovie().
   * @param realTitle This is the title as seen in the csv record--needed
   * for a call to queryForMovie().
   * @param year An integer for the year of the movie--also needed for the
   * call to queryForMovie().
   */
  private void bestActor(String title, String realTitle, int year) {
    //get the movie id for the given movie title
    int mid = queryForMovie(title, realTitle, year);

    try {
      //movie was found in database
      if (mid != -1) {
        //get the name of the actor
        String[] name = oscars.get(2).split(" ");
        //clean any special cases
        if (name.length == 3) {
          name = checkForNameSpecialCases(name);
        }
        //get the crew person id for this actor from the database
        int cid = queryForCrewperson(name);
        //actor was found in the database
        if (cid != -1) {
          //get the status of the nomination
          int status = Integer.parseInt(oscars.get(4));
          //log what was found
          log.logData("mid = " + mid, 1, false);
          log.logData("cid = " + cid, 1, false);
          //write the sql insert statement for this best actor nomination
          try {
            bw.write("INSERT INTO oscar_given_to VALUES(" + mid + ", 2, " + cid + ", " + status + ");");
            bw.newLine();
          }
          catch (IOException ioe) {
            log.logFatalError("Writing insert statement for best picture.",0,false);
            ioe.printStackTrace();
            System.exit(1); 
          }
        }
      }
    }
    catch (IOException ioe) {
      log.logFatalError("I/O Error in bestActor().",0,false);
      log.logGeneralMessageWithoutIndicator(ioe.toString(),0,false);
      ioe.printStackTrace();
      System.exit(1);
    }
  }
}
