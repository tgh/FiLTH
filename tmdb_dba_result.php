<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'dba')
    {
	    header("Location: http://www.cs.pdx.edu/~tgh/tmdb.php");
	    exit;
    }

    include '../security.php';
    $host='db.cecs.pdx.edu';

/*------------------------ PAGE TITLE AND HEADER -----------------------------*/

?>

<html>
<head>
<title>TMDB Administrator</title>
</head>

<body style="font-family: 'Palatino Linotype', 'Book Antiqua', Palatino, serif">
<div align="center">
<font color="maroon"><h1 style="font-variant: small-caps">TMDB Administration</h1></font>
<br>
</div>
<p style="font-size: 14px; font-style: italic;">
<a href="http://www.cs.pdx.edu/~tgh/tmdb_logout.php">Logout</a>
</p>
<br>

<?php

//TODO: add an undo button

/*---------------------------- Add a movie -----------------------------------*/

    if ($_POST['addMovieYear'])
    {
        //make sure there is a title given
        if (!$_POST['addMovieTitle'])
        {
            echo "<font color=\"maroon\"><i>You must give a title for the movie.</i></font>\n";
            ?>
            <p style="font-size: 14px; font-style: italic;"> 
            <a href="tmdb_dba.php">Try again.</a>
            </p>
            <?
        }
        else
        {
            //connect to DB
            $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
            if (!$connection)
            {
                echo "<font color=\"maroon\"><i>Could not connect to database.</i></font>\n";
	            die('Could not connect');
            }

            //insert the movie into movie table
            $movieQuery = "INSERT INTO movie VALUES ('" . $_POST['addMovieTitle'] . "', ";
            $movieQuery = $movieQuery . $_POST['addMovieYear'] . ", ";
            if ($_POST['addMovieStarRating'] != "DEFAULT")
                $movieQuery = $movieQuery . "'" . $_POST['addMovieStarRating'] . "', ";
            else
                $movieQuery = $movieQuery . "DEFAULT, ";
            if ($_POST['addMovieCountry'])
                $movieQuery = $movieQuery . "'" . $_POST['addMovieCountry'] . "', ";
            else
                $movieQuery = $movieQuery . "DEFAULT, ";
            $movieQuery = $movieQuery . "'" . $_POST['addMovieView'] . "')";

            $result1 = pg_query($connection, $movieQuery);
            if (!$result1) 
            {
                echo "<font color=\"maroon\"><i>Error in inserting movie into database.</i></font>\n";
                die("Error in query: $movieQuery." . pg_last_error($connection));
            }

            if ($_POST['addMovieDirector'] != "blank")
            {
                //check and see that the director is already in the DB
                $dirCheckQuery = "SELECT * FROM director WHERE dirname = '" . $_POST['addMovieDirector'] . "'";
                $result2 = pg_query($connection, $dirCheckQuery);
                if (!$result2)
                {
                    echo "<font color=\"maroon\"><i>Error in director query.</i></font>\n";
                    die("Error in query: $dirCheckQuery." . pg_last_error($connection));
                }
                $rows = pg_num_rows($result2);

                //insert director into director table if director not already there
                if ($rows == 0)
                {
                    $dirInsertQuery = "INSERT INTO director VALUES ('" . $_POST['addMovieDirector'] . "')";
                    $result3 = pg_query($connection, $dirInsertQuery);
                    if (!$result3)
                    {
                        echo "Error in inserting director into database.";
                        die("Error in query: $dirInsertQuery." . pg_last_error($connection));
                    }
                }

                //insert directed relationship
                $directedQuery = "INSERT INTO directed VALUES ('" . $_POST['addMovieTitle'] . "', ";
                $directedQuery = $directedQuery . $_POST[addMovieYear] . ", '" . $_POST['addMovieDirector'] . "')";

                $result4 = pg_query($connection, $directedQuery);
                if (!$result4)
                {
                    echo "<font color=\"maroon\"><i>Error in inserting director/movie relationship.</i></font>\n";
                    die("Error in query: $directedQuery." . pg_last_error($connection));
                }
            }

            echo "<font color=\"maroon\"><i>Movie added.</i></font>\n";

            ?>
            <p style="font-size: 14px; font-style: italic;"> 
            <a href="tmdb_dba.php">Do something else.</a>
            </p>
            <?

            pg_close($connection);
        }
    }

/*--------------------------- Add a director ---------------------------------*/

    else if ($_POST['addDirectorDirname'])
    {
        $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
        if (!$connection)
        {
	        die('Could not connect');
        }

        ?>
        <p style="font-size: 14px; font-style: italic;"> 
        <a href="tmdb_dba.php">Do something else.</a>
        </p>
        <?

        pg_close($connection);
    }

/*--------------------------- Add an actor -----------------------------------*/

    else if ($_POST['addActorActname'])
    {
        $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
        if (!$connection)
        {
	        die('Could not connect');
        }

        ?>
        <p style="font-size: 14px; font-style: italic;"> 
        <a href="tmdb_dba.php">Do something else.</a>
        </p>
        <?

        pg_close($connection);
    }

/*--------------------------- Add an oscar -----------------------------------*/

    else if ($_POST['addOscarTitle'])
    {
        $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
        if (!$connection)
        {
	        die('Could not connect');
        }

        ?>
        <p style="font-size: 14px; font-style: italic;"> 
        <a href="tmdb_dba.php">Do something else.</a>
        </p>
        <?

        pg_close($connection);
    }

/*----------------------- Change a movie's rating ----------------------------*/

    else if ($_POST['changeRatingYear'])
    {
        //make sure a title is given
        if (!$_POST['changeRatingTitle'])
        {
            echo "<font color=\"maroon\"><i>Title cannot be empty.</i></font>";
            ?>
            <p style="font-size: 14px; font-style: italic;"> 
            <a href="tmdb_dba.php">Try again.</a>
            </p>
            <?
        }
        else
        {
            $title = strtolower($_POST['changeRatingTitle']);
            $year = $_POST['changeRatingYear'];
            $newRating = $_POST['changeRatingStarRating'];

            //connect to DB
            $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
            if (!$connection)
	            die("Could not connect to database.");

            //check that the given movie is in the database
            $movCheckQuery = "SELECT * FROM movie WHERE lower(title) = '";
            $movCheckQuery = $movCheckQuery . $title;
            $movCheckQuery = $movCheckQuery . "' AND year = ";
            $movCheckQuery = $movCheckQuery . $year;

            $result1 = pg_query($connection, $movCheckQuery);
            if (!$result1)
                die("Error in movie existence query:\n $movCheckQuery." . pg_last_error($connection));
            $rows = pg_num_rows($result1);

            //report error if the movie does not exist in the database
            if ($rows == 0)
            {?>
                <font color="maroon"><i>The movie you're refering to doesn't exist in the database.</i>
                <br>
                <br></font>
                <p style="font-size: 14px; font-style: italic;">
                <a href="tmdb_dba.php">Try again.</a></p>
                </i>
             <?
                die();
            }

            //change the movie's star rating
            if ($newRating == "DEFAULT")
                $chRatingQuery = "UPDATE movie SET mystarrating = DEFAULT ";
            else
            {
                $chRatingQuery = "UPDATE movie SET mystarrating = '";
                $chRatingQuery = $chRatingQuery . $newRating . "' ";
            }
            $chRatingQuery = $chRatingQuery . "WHERE lower(title) = '";
            $chRatingQuery = $chRatingQuery . $title;
            $chRatingQuery = $chRatingQuery . "' AND year = ";
            $chRatingQuery = $chRatingQuery . $year;

            $result2 = pg_query($connection, $chRatingQuery);
            if (!$result2)
            {
                echo "<font color=\"maroon\"><i>Error in changing the star rating in the database.</i></font>\n";
                die("Error in query: $chRatingQuery." . pg_last_error($connection));
            }

            echo "<font color=\"maroon\"><i>Star rating changed.</i></font>";

            ?>
            <p style="font-size: 14px; font-style: italic;"> 
            <a href="tmdb_dba.php">Do something else.</a>
            </p>
            <?

            pg_close($connection);
        }
    }

/*-------------------------- Remove an actor ---------------------------------*/

    else if ($_POST['removeActorActname'])
    {
        $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
        if (!$connection)
        {
	        die('Could not connect');
        }

        ?>
        <p style="font-size: 14px; font-style: italic;"> 
        <a href="tmdb_dba.php">Do something else.</a>
        </p>
        <?

        pg_close($connection);
    }

/*----------------------------- PAGE FOOTER ----------------------------------*/

?>

<p style="font-style: italic; font-size: 10px;">
&copy;2009 Tyler Hayes
</p>

</body>
</html>
