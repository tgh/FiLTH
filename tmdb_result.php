<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'user')
    {
        header("Location: http://www.cs.pdx.edu/~tgh/tmdb.php");
        exit;
    }
  
    include '../security.php';
    $host='db.cecs.pdx.edu';

    /*
     * Bitmap for formatiing the output based on what attributes the user
     * specified to be displayed.
     */
    $displayBitmap = 0;

    //bitmasks for the bitmap
    define ('TTL_MASK', 0x100);
    define ('YER_MASK', 0x80);
    define ('STR_MASK', 0x40);
    define ('COU_MASK', 0x20);
    define ('DIR_MASK', 0x10);
    define ('ACT_MASK', 0x8);
    define ('SCR_MASK', 0x4);
    define ('CIN_MASK', 0x2);
    define ('OSC_MASK', 0x1);

    //globals for displaying attributes
    $title = -1;
    $year = -1;
    $star = -1;
    $country = -1;
    $director = -1;
    $actor = -1;
    $screen = -1;
    $cine = -1;
    $oscar = -1;

/*------------------------ PAGE TITLE AND HEADER -----------------------------*/

?>

<html>
<head>
<title>Tyler's Movie Database</title>
</head>

<body>
<div align="center">
<h1>Tyler's Movie Database</h1>
</div>
<p style="font-size: 12px; font-style: italic;"> 
<a href="http://www.cs.pdx.edu/~tgh/tmdb_logout.php">Logout.</a>
</p>
<p style="font-size: 12px; font-style: italic;"> 
<a href="http://www.cs.pdx.edu/~tgh/tmdb_home.php">Search again.</a>
</p>

<?php

/*-------------------------- Search by Title ---------------------------------*/

    if ($_POST['submitTitle'])
    {
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
            $titleToSearch = cleanString($_POST['title']);
            $whereString = " WHERE movie.title ILIKE '%" . $titleToSearch . "%'";
        }

        //get the sql query for the title search
        $titleQuery = createSql($whereString);
        //check for user not specifying any display checkboxes
        if (!$titleQuery)
        {
            //TODO
        }
//echo $titleQuery . "\n";
        //connect to db
        $connection = pg_connect("host={$host} dbname=tgh user=tgh password={$db_pw}");
        if (!$connection)
	        die('Could not connect');
        //run sql query on db
        $result = pg_query($connection, $titleQuery) or
        die("Error in query: $titleQuery." . pg_last_error($connection));
        //get the number of rows returned
        $rows = pg_num_rows($result);

        //display the results
        if($rows > 0)
        {
            setDisplayVars();

	        for($i=0; $i<$rows; ++$i)
	        {
	            $row = pg_fetch_row($result, $i);
                $itemCount = count($row);
                for ($j=0; $j < $itemCount; ++$j)
                {
                    switch ($j)
                    {
                        case $title:
                            break;
                        case $year:
                            break;
                        case $star:
                            break;
                        case $country:
                            break;
                        case $director:
                            break;
                        case $actor:
                            break;
                        case $screen:
                            break;
                        case $cine:
                            break;
                        case $oscar:
                            break;
                    }

                    if (!$row[$j])
                        echo "<font size=\"-1\"<i>NULL</i>\n";
                    else if ($j % 2 == 0)
	                    echo "<font size=\"-1\"><b>" . $row[$j] . "</b>\n";
                    else
                        echo "<font size=\"-1\">" . $row[$j] . "\n";
                    echo " / ";
                    echo "</font>\n";
                }
                echo "<br>\n";
	        }
        }
        else
	        echo "<font size=\"-1\">No matching movies found.</font>";

        pg_close($connection);
    }

/*----------------------------- PAGE FOOTER ----------------------------------*/

?>

<p style="font-size: 12px; font-style: italic;"> 
<a href="http://www.cs.pdx.edu/~tgh/tmdb_logout.php">Logout.</a>
</p>
<p style="font-size: 12px; font-style: italic;"> 
<a href="http://www.cs.pdx.edu/~tgh/tmdb_home.php">Search again.</a>
</p>
<p style="font-style: italic; font-size: 10px;">
&copy;2009 Tyler Hayes</p>

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
    global $TTL_MASK;
    global $YER_MASK;
    global $STR_MASK;
    global $COU_MASK;
    global $DIR_MASK;
    global $ACT_MASK;
    global $SCR_MASK;
    global $CIN_MASK;
    global $OSC_MASK;
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
                $displayBitmap |= $TTL_MASK;
                break;

            case "movie.year":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $displayBitmap |= $YER_MASK;
                break;

            case "mystarrating":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $displayBitmap |= $STR_MASK;
                break;

            case "country":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $displayBitmap |= $COU_MASK;
                break;

            case "dirname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN directed USING (title, year)";
                $displayBitmap |= $DIR_MASK;
                break;

            case "actname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN actedin USING (title, year)";
                $displayBitmap |= $ACT_MASK;
                break;

            case "scrname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN wrote USING (title, year)";
                $displayBitmap |= $SCR_MASK;
                break;

            case "cinname":
                $selectString = $selectString . current($_POST);
                next($_POST);
                if (key($_POST) != "sort")
                    $selectString = $selectString . ", ";
                prev($_POST);
                $fromString = $fromString . " JOIN shot USING (title, year)";
                $displayBitmap |= $CIN_MASK;
                break;

            case "OSCAR":
                $fromString = $fromString . " JOIN oscar USING (title, year)";
                $selectString = $selectString . "category, recipientName, status";
                $displayBitmap |= $OSC_MASK;
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
function cleanString($string)
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
 * Set the global display variables in order to format how the results of the
 * search query are to be displayed.
 */
function setDisplayVars()
{
    global $displayBitmap;
    global $TTL_MASK;
    global $YER_MASK;
    global $STR_MASK;
    global $COU_MASK;
    global $DIR_MASK;
    global $ACT_MASK;
    global $SCR_MASK;
    global $CIN_MASK;
    global $OSC_MASK;
    $index = 0;

    //title
    if ($displayBitmap & $TTL_MASK == 1)
    {
        $title = $index;
        ++$index;
    }
    //year
    if ($displayBitmap & $YER_MASK == 1)
    {
        $year = $index;
        ++$index;
    }
    //star rating
    if ($displayBitmap & $STR_MASK == 1)
    {
        $star = $index;
        ++$index;
    }
    //country
    if ($displayBitmap & $COU_MASK == 1)
    {
        $country = $index;
        ++$index;
    }
    //director
    if ($displayBitmap & $DIR_MASK == 1)
    {
        $director = $index;
        ++$index;
    }
    //actor
    if ($displayBitmap & $ACT_MASK == 1)
    {
        $actor = $index;
        ++$index;
    }
    //screenwriter
    if ($displayBitmap & $SCR_MASK == 1)
    {
        $screen = $index;
        ++$index;
    }
    //cinematographer
    if ($displayBitmap & $CIN_MASK == 1)
    {
        $cine = $index;
        ++$index;
    }
    //oscar
    if ($displayBitmap & $OSC_MASK == 1)
    {
        $oscar = $index;
        ++$index;
    }
}

?>
