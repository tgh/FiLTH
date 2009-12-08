<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'user')
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
<title>TMDB: Tyler's Movie Database</title>
</head>

<body style="font-family: 'Palatino Linotype', 'Book Antiqua', Palatino, serif">
<div align="center">
<font color="maroon"><h1 style="font-variant: small-caps">Tyler's Movie Database</h1></font>
</div>
<p style="font-size: 14px; font-style: italic;"> 
<a href="tmdb_logout.php">Logout</a>
</p>
<br>

<?php
/*--------------------------- USER MAIN MENU --------------------------------*/
?>

<form action="<?php echo $PHP_SELF?>" method="post">
    <b><i>Search movies by:</i></b>
    <select name="searchBy">
        <?php
        if ($_POST['searchBy'] == "byTitle")
            echo "<option value=\"byTitle\" selected=\"selected\">Title</option>";
        else
            echo "<option value=\"byTitle\">Title</option>";
/*
        if ($_POST['searchBy'] == "byYear")
            echo "<option value=\"byYear\" selected=\"selected\">Year</option>";
        else
            echo "<option value=\"byYear\">Year</option>";
        if ($_POST['searchBy'] == "byStar")
            echo "<option value=\"byStar\" selected=\"selected\">Tyler's star rating</option>";
        else
            echo "<option value=\"byStar\">Tyler's star rating</option>";
        if ($_POST['searchBy'] == "byCountry")
            echo "<option value=\"byCountry\" selected=\"selected\">Country</options>";
        else
            echo "<option value=\"byCountry\">Country</options>";
        if ($_POST['searchBy'] == "byDirector")
            echo "<option value=\"byDirector\" selected=\"selected\">Director</option>";
        else
            echo "<option value=\"byDirector\">Director</option>";
        if ($_POST['searchBy'] == "byActor")
            echo "<option value=\"byActor\" selected=\"selected\">Actor</option>";
        else
            echo "<option value=\"byActor\">Actor</option>";
        if ($_POST['searchBy'] == "byScreen")
            echo "<option value=\"byScreen\" selected=\"selected\">Screenwriter</option>";
        else
            echo "<option value=\"byScreen\">Screenwriter</option>";
        if ($_POST['searchBy'] == "byCine")
            echo "<option value=\"byCine\" selected=\"selected\">Cinematographer</option>";
        else
            echo "<option value=\"byCine\">Cinematographer</option>";
        if ($_POST['searchBy'] == "byOscar")
            echo "<option value=\"byOscar\" selected=\"selected\">Oscar category</option>";
        else
            echo "<option value=\"byOscar\">Oscar category</option>";
*/
        ?>
        <input type="submit" name="submit" value="Go">
    </select>
</form>
<hr width=33% align=left>
<br>
<?php
    switch($_POST['searchBy']) {

/*-------------------------- Search by Title ---------------------------------*/

    case "byTitle":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Title (or title keyword):</b></i></font>
        <input type="text" size="30" maxlength="80" name="title">
        <br>
        <?php
        printDisplayAndSort("submitTitle");
        break;

/*-------------------------- Search by Year ----------------------------------*/

    case "byYear":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Year:</i></b></font>&nbsp&nbsp&nbsp
        <font size=-1 style="font-variant: small-caps">from:
        <select name="yearStart">
            <?
            for ($i=2012; $i != 2009; --$i)
                echo "<option value=\"$i\">$i</option>";
            echo "<option value=\"2009\" selected=\"selected\">2009</option>";
            for ($i=2008; $i != 1899; --$i)
                echo "<option value=\"$i\">$i</option>";
            ?>
        </select>
        &nbsp&nbsp to:</font>
        <select name="yearEnd">
            <option value="blank"></option>
            <?
            for ($i=2012; $i != 2009; --$i)
                echo "<option value=\"$i\">$i</option>";
            echo "<option value=\"2009\" selected=\"selected\">2009</option>";
            for ($i=2008; $i != 1899; --$i)
                echo "<option value=\"$i\">$i</option>";
            ?>
        </select>
        <br>
        <?php
        printDisplayAndSort("submitYear");
        break;

/*----------------------- Search by Star rating ------------------------------*/

    case "byStar":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Star rating:</i></b></font>&nbsp&nbsp&nbsp
        <font size=-1 style="font-variant: small-caps">from:
        <select name=starStart">
            <option value="not seen">Haven't seen</option>
            <option value="N/A">N/A</option>
            <option value="NO STARS" selected="selected">NO STARS</option>
            <option value="&frac12*">&frac12*</option>
            <option value="*">*</option>
            <option value="*&frac12">*&frac12</option>
            <option value="**">**</option>
            <option value="**&frac12">**&frac12</option>
            <option value="***">***</option>
            <option value="***&frac12">***&frac12</option>
            <option value="****">****</option>
        </select>
        &nbsp&nbsp to: </font>
        <select name="starEnd">
            <option value="not seen">Haven't seen</option>
            <option value="N/A">N/A</option>
            <option value="NO STARS">NO STARS</option>
            <option value="&frac12*">&frac12*</option>
            <option value="*">*</option>
            <option value="*&frac12">*&frac12</option>
            <option value="**">**</option>
            <option value="**&frac12">**&frac12</option>
            <option value="***">***</option>
            <option value="***&frac12">***&frac12</option>
            <option value="****" selected="selected">****</option>
        </select>
        <br>
        <?php
        printDisplayAndSort("submitStar");
        break;

/*------------------------- Search by Country --------------------------------*/

    case "byCountry":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Country:</i></b></font>
        <select name="country">
            <?php
            $countryQuery="SELECT country FROM movie GROUP BY country ORDER BY country";
            $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
            if (!$connection)
                die('Could not connect');
            $result = pg_query($connection, $countryQuery) or
                die("Error in query: $countryQuery." . pg_last_error($connection));
            $rows = pg_num_rows($result);
            if ($rows > 0)
            {
                for($i=0; $i < $rows; ++$i)
                {
                    $row = pg_fetch_row($result, $i);
                    ?>
                    <option value="<?php echo $row[0];?>"
                                   <?php if ($row[0] == "USA") {
                                            echo " selected=\"selected\"";
                                   }?>>
                                   <?php if ($row[0] == "") {
                                            echo "UNDEFINED";
                                   }
                                   echo $row[0];?></option>
                    <?php
                }
            }
            pg_close($connection);
            ?>
        </select>
        <br>
        <?php
        printDisplayAndSort("submitCountry");
        break;

/*------------------------ Search by Director --------------------------------*/

    case "byDirector":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Director:</i></b></font>
        <select name="director">
            <?php
            $dirQuery="SELECT dirname FROM director ORDER BY dirname";
            $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
            if (!$connection)
                die('Could not connect');
            $result = pg_query($connection, $dirQuery) or
                die("Error in query: $dirQuery." . pg_last_error($connection));
            $rows = pg_num_rows($result);
            if ($rows > 0)
            {
                for($i=0; $i < $rows; ++$i)
                {
                    $row = pg_fetch_row($result, $i);
                    ?>
                    <option value="<?php echo $row[0];?>"><?php echo $row[0];?></option>
                    <?php
                }
            }
            pg_close($connection);
            ?>
        </select>
        <br>
        <?php
        printDisplayAndSort("submitDirector");
        break;

/*-------------------------- Search by Actor ---------------------------------*/

    case "byActor":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Actor:</i></b></font>
        <select name="actor">
            <?php
            $actQuery="SELECT actname FROM actor ORDER BY actname";
            $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
            if (!$connection)
                die('Could not connect');
            $result = pg_query($connection, $actQuery) or
                die("Error in query: $actQuery." . pg_last_error($connection));
            $rows = pg_num_rows($result);
            if ($rows > 0)
            {
                for($i=0; $i < $rows; ++$i)
                {
                    $row = pg_fetch_row($result, $i);
                    ?>
                    <option value="<?php echo $row[0];?>"><?php echo $row[0];?></option>
                    <?php
                }
            }
            pg_close($connection);
            ?>
        </select>
        <br>
        <?php
        printDisplayAndSort("submitActor");
        break;

/*---------------------- Search by Screenwriter ------------------------------*/

    case "byScreen":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Screenwriter:</i></b></font>
        <select name="screen">
            <?php
            $scrQuery="SELECT scrname FROM screenwriter ORDER BY scrname";
            $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
            if (!$connection)
                die('Could not connect');
            $result = pg_query($connection, $scrQuery) or
                die("Error in query: $scrQuery." . pg_last_error($connection));
            $rows = pg_num_rows($result);
            if ($rows > 0)
            {
                for($i=0; $i < $rows; ++$i)
                {
                    $row = pg_fetch_row($result, $i);
                    ?>
                    <option value="<?php echo $row[0];?>"><?php echo $row[0];?></option>
                    <?php
                }
            }
            pg_close($connection);
            ?>
        </select>
        <br>
        <?php
        printDisplayAndSort("submitScreen");
        break;

/*--------------------- Search by Cinematographer ----------------------------*/

    case "byCine":
        ?>
        <form action="tmdb_result.php" method="post">
        <font color="maroon"><b><i>Cinematographer:</i></b></font>
        <select name="cine">
            <?php
            $cinQuery="SELECT cinname FROM cinematographer ORDER BY cinname";
            $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
            if (!$connection)
                die('Could not connect');
            $result = pg_query($connection, $cinQuery) or
                die("Error in query: $cinQuery." . pg_last_error($connection));
            $rows = pg_num_rows($result);
            if ($rows > 0)
            {
                for($i=0; $i < $rows; ++$i)
                {
                    $row = pg_fetch_row($result, $i);
                    ?>
                    <option value="<?php echo $row[0];?>"><?php echo $row[0];?></option>
                    <?php
                }
            }
            pg_close($connection);
            ?>
        </select>
        <br>
        <?php
        printDisplayAndSort("submitCine");
        break;

/*-------------------------- Search by Oscar ---------------------------------*/

    case "byOscar":
        ?>
        <form action="tmdb_result.php" method="post">
            <font color="maroon"><b><i>Category:</i></b></font>
            <select name="oscar">
                <option value="Best Picture">Best Picture</option>
                <option value="Best Actor">Best Actor</option>
                <option value="Best Actress">Best Actress</option>
                <option value="Best Supporting Actor">Best Supporting Actor</option>
                <option value="Best Supporting Actress">Best Supporting Actress</option>
                <option value="Best Director">Best Director</option>
                <option value="Best Cinematography">Best Cinematography</option>
                <option value="Best Adapted Screenplay">Best Adapted Screenplay</option>
                <option value="Best Original Screenplay">Best Original Screenplay</option>
                <option value="Best Foreign Language Film">Best Foreign Language Film</option>
            </select>
        <br>
        <?php
        printDisplayAndSort("submitOscar");
        break;

    }

/*----------------------------- PAGE FOOTER ----------------------------------*/

?>

<p style="font-style: italic; font-size: 10px;">
&copy;2009 Tyler Hayes</p>

</body>
</html>

<?php

/******************************************************************************/
/*                              PHP FUNCTIONS                                 */
/******************************************************************************/

/*
 * Displays the options for what attributes to display fo rthe user's search
 * results, and the menu for how the results are to be sorted.
 */
function printDisplayAndSort($buttonName) {
    echo "<br>\n";
    echo "<b><i>What would you like displayed in the result?</i></b>\n";
    echo "<br>\n";
    echo "<font face=\"courier\"><i>";
        //title
        printSpaces(3);
        echo "Title";
        printSpaces(11);
        echo "<input type=\"checkbox\" name=\"dispTitle\" value=\"movie.title\" checked=\"checked\"><br>\n";
        //year
        printSpaces(3);
        echo "Year";
        printSpaces(12);
        echo "<input type=\"checkbox\" name=\"dispYear\" value=\"movie.year\" checked=\"checked\"><br>";
        //star rating
        printSpaces(3);
        echo "Star rating";
        printSpaces(5);
        echo "<input type=\"checkbox\" name=\"dispStar\" value=\"mystarrating\" checked=\"checked\"><br>";
        //country
        printSpaces(3);
        echo "Country";
        printSpaces(9);
        echo "<input type=\"checkbox\" name=\"dispCountry\" value=\"country\" checked=\"checked\"><br>";
        //director
        printSpaces(3);
        echo "Director";
        printSpaces(8);
        echo "<input type=\"checkbox\" name=\"dispDirector\" value=\"dirname\"><br>";
        //actor
        printSpaces(3);
        echo "Actor";
        printSpaces(11);
        echo "<input type=\"checkbox\" name=\"dispActor\" value=\"actname\"><br>";
        //screenwriter
        printSpaces(3);
        echo "Screenwriter";
        printSpaces(4);
        echo "<input type=\"checkbox\" name=\"dispScreen\" value=\"scrname\"><br>";
        //cinematographer
        printSpaces(3);
        echo "Cinematographer";
        printSpaces(1);
        echo "<input type=\"checkbox\" name=\"dispCine\" value=\"cinname\"><br>";
        //oscar
        printSpaces(3);
        echo "Oscar";
        printSpaces(11);
        echo "<input type=\"checkbox\" name=\"dispOscar\" value=\"OSCAR\"><br>";

    echo "<br>\n";
    echo "</i></font>";
    echo "<b><i>Sort results by:</i></b>\n\n";
    echo "<select name=\"sort\">\n";
        echo "<option value=\"movie.title\">Title</option>\n";
        echo "<option value=\"movie.year\">Year</option>\n";
//        echo "<option value=\"mystarrating\">Star Rating</option>\n";
        echo "<option value=\"country\">Country</option>\n";
/*
        echo "<option value=\"dirname\">Director</option>\n";
        echo "<option value=\"actname\">Actor</option>\n";
        echo "<option value=\"scrname\">Screenwriter</option>\n";
        echo "<option value=\"cinname\">Cinematographer</option>\n";
*/
    echo "</select>\n";
    echo "<br><br>\n";
    echo "<input type=\"submit\" name=\"" . $buttonName . "\" value=\"Search\">\n";
    echo "</form>\n";
}

/*----------------------------------------------------------------------------*/

/*
 * Print the specified number of html spaces.
 */
function printSpaces($n)
{
    for ($i=0; $i < $n; ++$i)
        echo "&nbsp";
    echo " ";
}

?>
