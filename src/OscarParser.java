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
        int year = Integer.parseInt(oscars.get(0));
        String category = oscars.get(1);
        String title = null;
        int mid = 0;
        int cid = 0;
        int status = 0;

        //log the current record
        log.logData("RECORD: " + oscars.get(0) + ", " + category + ", " + oscars.get(2)
                    + ", " + oscars.get(3) + ", " + oscars.get(4), 0, false);

        /*---------- BEST PICTURE (oid = 1) ---------------------------------*/

        if (category.equals("Best Picture")) {
          title = checkForSpecialCases(oscars.get(2));
          title = title.toLowerCase().replace("'","''");
          title = title.replace(" & "," ").replace(" ","&").replace("!","");
          mid = queryForMovie(title, oscars.get(2), year);
          if (mid != -1) {
            status = Integer.parseInt(oscars.get(4));
            log.logData("mid = " + mid, 1, false);
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
          title = oscars.get(3);
          //remove the character name from the title
          String realTitle = title.substring(0, title.indexOf(" {"));
          title = checkForSpecialCases(realTitle);
          title = title.toLowerCase().replace("'","''");
          title = title.replace(" & "," ").replace(" ","&").replace("!","");
          mid = queryForMovie(title, realTitle, year);
          if (mid != -1) {
            String[] name = oscars.get(2).split(" ");
            if (name.length == 3) {
              name = checkForNameSpecialCases(name);
            }
            cid = queryForCrewperson(name);
            if (cid != -1) {
              status = Integer.parseInt(oscars.get(4));
              log.logData("mid = " + mid, 1, false);
              log.logData("cid = " + cid, 1, false);
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
    if (noarg == false) {
      log.logFooter("END");
      try { if (dbConn != null) dbConn.close(); }
      catch (SQLException sqle) { sqle.printStackTrace(); }
      log.close();
      if (oscars != null)
        oscars.close();
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
   *
   */
  private String[] checkForNameSpecialCases(String[] name) {
    if (name[1].equals("De")) {
      String[] n = {name[0],"De " + name[2]};
      return n;
    }
    if (name[2].equals("Jr.")) {
      String[] n = {name[0],name[1] + " Jr."};
      return n;
    }
    return name;
  }

  //--------------------------------------------------------------------------

  /**
   *
   */
  private int queryForMovie(String formattedTitle, String realTitle, int year) {
    int mid = 0;
    int status = 0;
    String response = null;
    ResultSet qResult = null;

    try {
      //special case: Being There
      //  Postgres's full text search includes both 'being' and 'there' as
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
            log.logGeneralMessageWithoutIndicator("-- Movie not found.",1,false);
            break;
          }
        }
      }
      //no matches found at all
      else {
        log.logGeneralMessageWithoutIndicator("-- Movie not found.",1,false);
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
   *
   */
  private int queryForCrewperson(String[] names) {
    String lname = null;
    String fname = null;
    String mname = null;
    ResultSet qResult = null;

    try {
      switch(names.length) {
        //e.g. Cher
        case 1: lname = names[0];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname + "' AND f_name is NULL;");
                if (qResult.next()) {
                  return qResult.getInt(1);
                }
                break;
        case 2: lname = names[1].replace("'","''");
                fname = names[0];
                qResult = db.select("SELECT cid FROM crew_person WHERE l_name = '" + lname
                                    + "' AND f_name = '" + fname + "';");
                if (qResult.next()) {
                  return qResult.getInt(1);
                }
                break;
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
}
