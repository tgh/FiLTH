<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'dba')
    {
	    header("Location: http://www.cs.pdx.edu/~tgh/tmdb.php");
	    exit;
    }

    include '../security.php';
    $host='db.cecs.pdx.edu';
?>

<html>
<head>
<title>Administrator for Tyler's Movie Database</title>
</head>

<body>
<div align="center">
<h2>Administration for Tyler's Movie Database</h2>
</div>
<p style="font-size: 12px; font-style: italic;"> 
<a href="tmdb_logout.php">Logout.</a>
</p>

<?
//TODO: change 'do something else' link to button
//TODO: add an undo button
?>

<?php

/*---------------------------- Add a movie -----------------------------------*/

    if ($_POST['addMovieYear'])
    {
        //make sure there is a title given
        if (!$_POST['addMovieTitle'])
        {
            echo "You must give a title for the movie.";
            ?>
            <p style="font-size: 12px; font-style: italic;"> 
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
                echo "Could not connect to database.";
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
                echo "Error in inserting movie into database.";
                die("Error in query: $movieQuery." . pg_last_error($connection));
            }

            if ($_POST['addMovieDirector'] != "blank")
            {
                //check and see that the director is already in the DB
                $dirCheckQuery = "SELECT * FROM director WHERE dirname = '" . $_POST['addMovieDirector'] . "'";
                $result2 = pg_query($connection, $dirCheckQuery);
                if (!$result2)
                {
                    echo "Error in director query.";
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
                    echo "Error in inserting director/movie relationship.";
                    die("Error in query: $directedQuery." . pg_last_error($connection));
                }
            }

            echo "Movie added.";

            ?>
            <p style="font-size: 12px; font-style: italic;"> 
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
        <p style="font-size: 12px; font-style: italic;"> 
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
        <p style="font-size: 12px; font-style: italic;"> 
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
        <p style="font-size: 12px; font-style: italic;"> 
        <a href="tmdb_dba.php">Do something else.</a>
        </p>
        <?

        pg_close($connection);
    }

/*----------------------- Change a movie's rating ----------------------------*/

    else if ($_POST['changeRatingTitle'])
    {
        $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
        if (!$connection)
        {
	        die('Could not connect');
        }

        ?>
        <p style="font-size: 12px; font-style: italic;"> 
        <a href="tmdb_dba.php">Do something else.</a>
        </p>
        <?

        pg_close($connection);
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
        <p style="font-size: 12px; font-style: italic;"> 
        <a href="tmdb_dba.php">Do something else.</a>
        </p>
        <?

        pg_close($connection);
    }

/*----------------------------------------------------------------------------*/

?>

<p style="font-style: italic; font-size: 12px;">
&copy;2009 Tyler Hayes
</p>

</body>
</html>
