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
<a href="tmdb_logout.php">Logout.</a>
</p>

<?php

//TODO: add an undo button

/******************************** SEARCH **************************************/

/*------------------------------ by Title ------------------------------------*/

    if ($_POST['submitTitle'])
    {
        ?>
        <p style="font-size: 14px; font-style: italic;"> 
        <a href="tmdb_dba.php">Back to main menu.</a>
        </p>
        <?
/*
        //DEBUG output
        foreach ($_POST as $key => $value)
            echo "key is: " . $key . " - value is: " . $value . "<br />";
*/
        //determine WHERE sql string
        if (!$_POST['title'])
            $whereString = "";
        else
        {
            //check for apostrophe
            $titleToSearch = cleanTitleString($_POST['title']);
            $whereString = " WHERE movie.title ILIKE '%" . $titleToSearch . "%'";
        }

        //get the sql query for the title search
        $titleQuery = createSql($whereString);
        //check for user not specifying any display checkboxes
        if (!$titleQuery)
        {
            echo "<font color=\"maroon\"><i>You didn't specify any attributes to display.";
            echo "  Search aborted.</i></font>\n";
            die();
        }

        //connect to db
        $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
        if (!$connection)
	        die('Could not connect');
        //run sql query on db
        $result = pg_query($connection, $titleQuery) or
        die("Error in query: $titleQuery." . pg_last_error($connection));
        //get the number of rows returned
        $rows = pg_num_rows($result);
        //display the number of matches
        echo "<br>\n";
        echo "<font face=\"palatino\"><i>Number of matches: </i><b>" . $rows . "</b></font>\n";
        echo "<br>\n";
        echo "<br>\n";
        echo "<hr width=50% align=left>\n";
        echo "<br>\n";
        //display the results
        if($rows > 0)
        {
            setDisplayVars();

	        for($i=0; $i<$rows; ++$i)
	        {
	            $row = pg_fetch_row($result, $i);
                $itemCount = count($row);
                for ($j=0; $j < $itemCount; ++$j)
                    displayAttribute($j, $row);
                echo "<br>\n";
	        }
        }
        else
	        echo "<font size=\"-1\"><i>No matching movies found.</i></font>";

        pg_close($connection);
    }

/******************************************************************************/

/*---------------------------- Add a movie -----------------------------------*/

    if ($_POST['addMovieYear'])
    {
        ?>
        <p style="font-size: 14px; font-style: italic;"> 
        <a href="tmdb_dba.php">Back to main menu.</a>
        </p>
        <?

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
                $movieQuery = $movieQuery . "'" . cleanStarString($_POST['addMovieStarRating']) . "', ";
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

            //insert director relationship to the movie
            if ($_POST['addMovieDirector'] != "blank")
            {
/*
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
*/
                //insert directed relationship
                $directedQuery = "INSERT INTO directed VALUES ('" . $_POST['addMovieTitle'] . "', ";
                $directedQuery = $directedQuery . $_POST['addMovieYear'] . ", '" . $_POST['addMovieDirector'] . "')";

                $result4 = pg_query($connection, $directedQuery);
                if (!$result4)
                {
                    echo "<font color=\"maroon\"><i>Error in inserting director/movie relationship.</i></font>\n";
                    die("Error in query: $directedQuery." . pg_last_error($connection));
                }
            }

            //insert actor relationship to the movie
            if ($_POST['addMovieActor'] != "blank")
            {
/*
                //check and see that the actor is already in the DB
                $actCheckQuery = "SELECT * FROM actor WHERE actname = '" . $_POST['addMovieActor'] . "'";
                $result3 = pg_query($connection, $actCheckQuery);
                if (!$result3)
                {
                    echo "<font color=\"maroon\"><i>Error in actor query.</i></font>\n";
                    die("Error in query: $actCheckQuery." . pg_last_error($connection));
                }
                $rows = pg_num_rows($result3);

                //insert actor into actor table if actor not already there
                if ($rows == 0)
                {
                    $actInsertQuery = "INSERT INTO actor VALUES ('" . $_POST['addMovieActor'] . "')";
                    $result4 = pg_query($connection, $actInsertQuery);
                    if (!$result4)
                    {
                        echo "Error in inserting actor into database.";
                        die("Error in query: $actInsertQuery." . pg_last_error($connection));
                    }
                }
*/
                //insert actedin relationship
                $actorQuery = "INSERT INTO actedin VALUES ('" . $_POST['addMovieTitle'] . "', ";
                $actorQuery = $actorQuery . $_POST['addMovieYear'] . ", '" . $_POST['addMovieActor'] . "')";

                $result5 = pg_query($connection, $actorQuery);
                if (!$result5)
                {
                    echo "<font color=\"maroon\"><i>Error in inserting actor/movie relationship.</i></font>\n";
                    die("Error in query: $actorQuery." . pg_last_error($connection));
                }
            }

            echo "<font color=\"maroon\"><i>Movie added.</i></font>\n";

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
        ?>
        <p style="font-size: 14px; font-style: italic;"> 
        <a href="tmdb_dba.php">Back to main menu.</a>
        </p>
        <?

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
                $chRatingQuery = $chRatingQuery . "UPDATE movie SET mystarrating = DEFAULT ";
            else
            {
                $newRating = cleanStarString($newRating);
                $chRatingQuery = $chRatingQuery . "UPDATE movie SET mystarrating = '";
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

<br>
<br>
<p style="font-style: italic; font-size: 10px;">
&copy;2009 Tyler Hayes
</p>

</body>
</html>

<?php

/******************************************************************************/
/*                              PHP FUNCTIONS                                 */
/******************************************************************************/

/*
 * Creates the SQL query for the search.  Returns string.
 */
function createSql($whereString)
{
    global $displayBitmap;
    $postCount = count($_POST)-1;
    $selectString = "SELECT ";
    $fromString = " FROM movie";
    $orderByString = " ORDER BY ";

    //check if no display options were checked
    if (!$_POST['dispTitle']
        && !$_POST['dispYear']
        && !$_POST['dispStar']
        && !$_POST['dispCountry']
        && !$_POST['dispDirector']
        && !$_POST['dispActor']
        && !$_POST['dispScreen']
        && !$_POST['dispCine']
        && !$_POST['dispOscar'])
    return NULL;

    //set up the SELECT and FROM sql strings
    for (reset($_POST); key($_POST) != "sort"; next($_POST))
    {
        switch (current($_POST))
        {
            case "movie.title":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $displayBitmap |= TTL_MASK;
                break;

            case "movie.year":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $displayBitmap |= YER_MASK;
                break;

            case "mystarrating":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $displayBitmap |= STR_MASK;
                break;

            case "country":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $displayBitmap |= COU_MASK;
                break;

            case "dirname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN directed USING (title, year)";
                $displayBitmap |= DIR_MASK;
                break;

            case "actname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN actedin USING (title, year)";
                $displayBitmap |= ACT_MASK;
                break;

            case "scrname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN wrote USING (title, year)";
                $displayBitmap |= SCR_MASK;
                break;

            case "cinname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN shot USING (title, year)";
                $displayBitmap |= CIN_MASK;
                break;

            case "OSCAR":
                $fromString = $fromString . " JOIN oscar USING (title, year)";
                $selectString = $selectString . "category, recipientName, status";
                $displayBitmap |= OSC_MASK;
                continue 2;

            default:
                continue 2;
        }
    }

    //get the order by attribute
    $orderByString = $orderByString . $_POST['sort'];

    return $selectString . $fromString . $whereString . $orderByString;
}

/*----------------------------------------------------------------------------*/

/*
 * Parse a string for an apostrophe and change the apostrophe to '%'.
 * Returns string.
 */
function cleanTitleString($string)
{
    $count = count($string);

    for ($i=0; $i < $count; ++$i)
    {
        if ($string[$i] == '\'')
            $string[$i] = '%';
    }

    return $string;
}

/*----------------------------------------------------------------------------*/

/*
 * Cleans a star rating string when it is supposed to have a ½ character.
 */
function cleanStarString($string)
{
    switch ($string)
    {
        case "1/2*": return "&frac12*";

        case "*1/2": return "*&frac12";

        case "**1/2": return "**&frac12";

        case "***1/2": return "***&frac12";

        default: return $string;
    }
}

/*----------------------------------------------------------------------------*/

/*
 * Set the global display variables in order to format how the results of the
 * search query are to be displayed.
 */
function setDisplayVars()
{
    global $displayBitmap;
    global $title;
    global $year;
    global $star;
    global $country;
    global $director;
    global $actor;
    global $screen;
    global $cine;
    global $oscar;
    $index = 0;

    //title
    if (($displayBitmap & TTL_MASK) == TTL_MASK)
    {
        $title = $index;
        ++$index;
    }
    //year
    if (($displayBitmap & YER_MASK) == YER_MASK)
    {
        $year = $index;
        ++$index;
    }
    //star rating
    if (($displayBitmap & STR_MASK) == STR_MASK)
    {
        $star = $index;
        ++$index;
    }
    //country
    if (($displayBitmap & COU_MASK) == COU_MASK)
    {
        $country = $index;
        ++$index;
    }
    //director
    if (($displayBitmap & DIR_MASK) == DIR_MASK)
    {
        $director = $index;
        ++$index;
    }
    //actor
    if (($displayBitmap & ACT_MASK) == ACT_MASK)
    {
        $actor = $index;
        ++$index;
    }
    //screenwriter
    if (($displayBitmap & SCR_MASK) == SCR_MASK)
    {
        $screen = $index;
        ++$index;
    }
    //cinematographer
    if (($displayBitmap & CIN_MASK) == CIN_MASK)
    {
        $cine = $index;
        ++$index;
    }
    //oscar
    if (($displayBitmap & OSC_MASK) == OSC_MASK)
    {
        $oscar = $index;
        ++$index;
    }
}

/*----------------------------------------------------------------------------*/

/*
 * Outputs the given attribute from the search query result.
 */
function displayAttribute($i, $row)
{
    global $title;
    global $year;
    global $star;
    global $country;
    global $director;
    global $actor;
    global $screen;
    global $cine;
    global $oscar;
    $separator = true;

    switch ($i)
    {
        //title
        case $title:
            echo "<font size=\"2\" face=\"palatino\"><b>" . $row[$i] . "</b>\n";
            break;

        //year
        case $year:
            echo "<font size=\"2\" face=\"palatino\">" . $row[$i] . "\n";
            break;

        //star rating
        case $star:
            if (!$row[$i])
                echo "<font size=\"2\" face=\"palatino\"><i>not seen</i>\n";
            else
                echo "<font size=\"2\" face=\"palatino\">" . $row[$i] . "\n";
            break;

        //country
        case $country:
            if (!$row[$i])
                $separator = false;
            else
                echo "<font size=\"2\" face=\"palatino\">" . $row[$i] . "\n";
            break;

        //director
        case $director:
            if (!$row[$i])
                $separator = false;
            else
                echo "<font size=\"2\" face=\"palatino\"><b>" . $row[$i] . "</b>\n";
            break;

        //actor
        case $actor:
            if (!$row[$i])
                $separator = false;
            else
                echo "<font size=\"2\" face=\"palatino\">" . $row[$i] . "\n";
            break;

        //screenwriter
        case $screen:
            if (!$row[$i])
                $separator = false;
            else
                echo "<font size=\"2\"> face=\"palatino\"" . $row[$i] . "\n";
            break;

        //cinematographer
        case $cine:
            if (!$row[$i])
                $separator = false;
            else
                echo "<font size=\"2\"> face=\"palatino\"" . $row[$i] . "\n";
            break;

        //oscar
        case $oscar:
            if (!$row[$i])
                $separator = false;
            else
                echo "<font size=\"2\"> face=\"palatino\"" . $row[$i] . "\n";
            break;
    }

    if ($separator)
    {
        echo " / ";
        echo "</font>\n";
    }
}

?>
