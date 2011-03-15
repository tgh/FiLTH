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

  //--------------------------------------------------------------------------

  /**
   * Main.
   *
   * Sets up the graceful shutdown mechanism, then calls start().
   */
  public static void main (String[] args) {
    //setup shutdown hook mechanism in order to close objects properly in case
    // of premature termination (by the OS or the user, for example).  This is
    // pretty much just used in order to write out everything in the Buffered-
    // Writer object in the Log object to the log file before ending the
    // process completely.
    GracefulShutdown op = new OscarParser();
    TerminationInterceptor ti = new TerminationInterceptor(op);
    Runtime.getRuntime().addShutdownHook(ti);
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
    //open the oscarsGivenTo.csv file
    try {
      oscars = new CsvReader(filthPath + "/data/oscarsOfCategory.csv");
    }
    catch (FileNotFoundException fnfe) {
      log.logFatalError("CSV file not found",0,false);
      fnfe.printStackTrace();
    }

    //open a log file
    log = new Log(filthPath + "/temp/oscarParser.log");

    //connect to the database
    dbConn = DatabaseConnector.connectToPostgres("jdbc:postgresql://localhost/filth",
                                                 "postgres",
                                                 "0o9i8u7y");
    //setup virtual SQL console with the db
    PostgresSQLConsole db = new PostgresSQLConsole(dbConn);

    log.logHeader("START");

    //parse the csv file
    try {
      while (oscars.readRecord()) {
        String category = oscars.get(1);

        //log the current record
        log.logData("RECORD: " + oscars.get(0) + ", " + category + ", " + oscars.get(2),0,false);

        /*---- BEST PICTURE (oid = 1) ----*/
        if (category.equals("Best Picture")) {

        }

        /*---- BEST ACTOR (oid = 2) ----*/
        if (category.equals("Best Actor")) {
        }

        /*---- BEST ACTRESS (oid = 3) ----*/
        if (category.equals("Best Actress")) {
        }

        /*---- BEST SUPPORTING ACTOR (oid = 4) ----*/
        if (category.equals("Best Supporting Actor")) {
        }

        /*---- BEST SUPPORTING ACTRESS (oid = 5) ----*/
        if (category.equals("Best Supporting Actress")) {
        }

        /*---- BEST DIRECTOR (oid = 6) ----*/
        if (category.equals("Best Director")) {
        }

        /*---- BEST CINEMATOGRAPHY (oid = 7) ----*/
        if (category.equals("Best Cinematography")) {
        }

        /*---- BEST CINEMATOGRAPHY (b & w) (oid =81) ----*/
        if (category.equals("Best Cinematography (black and white)")) {
        }

        /*---- BEST CINEMATOGRAPHY (color) (oid = 9) ----*/
        if (category.equals("Best Cinematography (color)")) {
        }

        /*---- BEST ORIGINAL SCREENPLAY (oid = 10) ----*/
        if (category.equals("Best Original Screenplay")) {
        }

        /*---- BEST ADAPTED SCREENPLAY (oid = 11) ----*/
        if (category.equals("Best Adapted Screenplay")) {
        }

        /*---- BEST FOREIGN LANGUAGE FILM (oid = 12) ----*/
        if (category.equals("Best Foreign Language Film")) {
        }
      }
    }
    catch (IOException ioe) {
      log.logFatalError("I/O Error of some kind",0,false);
      log.logGeneralMessageWithoutIndicator(ioe.toString(),0,false);
      ioe.printStackTrace();
    }
  }

  //--------------------------------------------------------------------------

  /**
   * What occurs when the program gets terminated.
   */
  public void shutDown() {
    System.out.println("Program terminated. Closing necessary resources...");
    log.logKill(0, false);
    log.logFooter("END");
    try { if (dbConn != null) dbConn.close(); }
    catch (SQLException sqle) { sqle.printStackTrace(); }
    log.close();
    if (oscars != null)
      oscars.close();
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
  }
}
