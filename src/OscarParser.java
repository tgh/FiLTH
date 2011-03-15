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

  public void start () {
    //open a log file
    log = new Log(filthPath + "/temp/oscarsParser.log");

    //open the oscarsGivenTo.csv file
    try {
      oscars = new CsvReader(filthPath + "/data/oscarsOfCategory.csv");
    }
    catch (FileNotFoundException fnfe) {
      log.logFatalError("CSV file not found",0,false);
      log.logFooter("END");
      log.close();
      fnfe.printStackTrace();
      System.exit(1);
    }

    //connect to the database
    try {
      Class.forName("org.postgresql.Driver");
      dbConn = DriverManager.getConnection("jdbc:postgresql://localhost/filth","postgres","0o9i8u7y");
    }
    catch (SQLException sqle) {
      log.logFatalError("Failure to connect to database",0,false);
      log.logFooter("END");
      log.close();
      oscars.close();
      sqle.printStackTrace();
      System.exit(1);
    }
    catch (ClassNotFoundException cnfe) {
      log.logFatalError("org.postgresql.Driver not found",0,false);
      log.logFooter("END");
      log.close();
      oscars.close();
      cnfe.printStackTrace();
      System.exit(1);
    }

    //parse the csv file
    try {
      while (oscars.readRecord()) {
        String category = oscars.get(1);

        /*---- BEST PICTURE ----*/
        if (category.equals("Best Picture")) {
          System.out.println("Best Picture");
        }

        /*---- BEST ACTOR ----*/
        if (category.equals("Best Actor")) {
        }

        /*---- BEST ACTRESS ----*/
        if (category.equals("Best Actress")) {
        }

        /*---- BEST SUPPORTING ACTOR ----*/
        if (category.equals("Best Supporting Actor")) {
        }

        /*---- BEST SUPPORTING ACTRESS ----*/
        if (category.equals("Best Supporting Actress")) {
        }

        /*---- BEST DIRECTOR ----*/
        if (category.equals("Best Director")) {
        }

        /*---- BEST CINEMATOGRAPHY ----*/
        if (category.equals("Best Cinematography")) {
        }

        /*---- BEST CINEMATOGRAPHY (b & w) ----*/
        if (category.equals("Best Cinematography (black and white)")) {
        }

        /*---- BEST CINEMATOGRAPHY (color) ----*/
        if (category.equals("Best Cinematography (color)")) {
        }

        /*---- BEST ORIGINAL SCREENPLAY ----*/
        if (category.equals("Best Original Screenplay")) {
        }

        /*---- BEST ADAPTED SCREENPLAY ----*/
        if (category.equals("Best Adapted Screenplay")) {
        }

        /*---- BEST FOREIGN LANGUAGE FILM ----*/
        if (category.equals("Best Foreign Language Film")) {
        }
      }
      oscars.close();
      try {
        dbConn.close();
      }
      catch (SQLException sqle) {
        System.out.println("ERROR closing db connection.");
      }
      log.logFooter("END");
      log.close();
    }
    catch (IOException ioe) {
      log.logFatalError("I/O Error of some kind",0,false);
      log.logGeneralMessageWithoutIndicator(ioe.toString(),0,false);
      log.logFooter("END");
      log.close();
      oscars.close();
      try {
        dbConn.close();
      }
      catch (SQLException sqle) {
        System.out.println("ERROR closing db connection while handling IOException.");
      }
      ioe.printStackTrace();
    }
  }


  /**
   * What occurs when the program gets terminated prematurely.
   */
  public void shutDown() {
    System.out.println("Program terminated. Closing necessary resources...");
    log.logKill(0, false);
    log.logFooter("END");
    try { dbConn.close(); }
    catch (SQLException sqle) { sqle.printStackTrace(); }
    log.close();
    oscars.close();
  }

  /**
   * Since there's going to be a lot of SQLExceptions that need to be caught,
   * and it's good to know just where it was caught for debugging purposes,
   * this method will take care of all of them in order to not clutter up the
   * meat code with a lot of exception handling code.
   */
  private void handleSQLException(String message, SQLException sqle) {
    log.logFatalError(message,0,false);
    log.logGeneralMessageWithoutIndicator(sqle.toString(),0,false);
    log.logFooter("END");
    log.close();
    oscars.close();
    try {
      dbConn.close();
    }
    catch (SQLException sqle2) {
      System.out.println("ERROR closing db connection while handling SQLException.");
    }
    System.exit(1);
  }
}
