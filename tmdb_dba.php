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
<a href="http://www.cs.pdx.edu/~tgh/tmdb_logout.php">Logout</a>
</p>
<br>

<?php
/*--------------------- ADMINISTRATIVE MAIN MENU -----------------------------*/
?>

<form action="<?php echo $PHP_SELF?>" method="post">
    What do you want to do?
    <select name="action">
        <option value="addMovie">Add a movie</option>
        <?php
        if ($_POST['action'] == "addDirector")
            echo "<option value=\"addDirector\" selected=\"selected\">Add a director</option>";
        else
            echo "<option value=\"addDirector\">Add a director</option>";
        if ($_POST['action'] == "addActor")
            echo "<option value=\"addActor\" selected=\"selected\">Add an actor</option>";
        else
            echo "<option value=\"addActor\">Add an actor</option>";
        if ($_POST['action'] == "addOscar")
            echo "<option value=\"addOscar\" selected=\"selected\">Add an oscar</option>";
        else
            echo "<option value=\"addOscar\">Add an oscar</option>";
        if ($_POST['action'] == "changeRating")
            echo "<option value=\"changeRating\" selected=\"selected\">Change a movie's star rating</options>";
        else
            echo "<option value=\"changeRating\">Change a movie's star rating</options>";
        if ($_POST['action'] == "removeActor")
            echo "<option value=\"removeActor\" selected=\"selected\">Remove an actor</option>";
        else
            echo "<option value=\"removeActor\">Remove an actor</option>";
        ?>
        <input type="submit" name="submit" value="Go">
    </select>
</form>
<br>

<?php
    switch($_POST['action']) {

/*---------------------------- Add a movie -----------------------------------*/

        case "addMovie":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Title: <input type="text" size="50" maxlength="100" name="addMovieTitle">
            <br>
            Year:
            <select name="addMovieYear">
                <?
                for ($i=1900; $i != 2009; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                echo "<option value=\"2009\" selected=\"selected\">2009</option>";
                for ($i=2010; $i != 2013; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                ?>
            </select>
            <br>
            Star rating:
            <select name="addMovieStarRating">
                <option value="DEFAULT"></option>
                <option value="N/A">N/A</option>
                <option value="NO STARS">NO STARS</option>
                <option value="&frac12*">&frac12*</option>
                <option value="*">*</option>
                <option value="*&frac12">*&frac12</option>
                <option value="**">**</option>
                <option value="**&frac12">**&frac12</option>
                <option value="***">***</option>
                <option value="***&frac12">***&frac12</option>
                <option value="****">****</option>
            </select>
            <br>
            Country: <input type="text" size="15" maxlength="20" name="addMovieCountry">
		    <br>
            View status:
            <select name="addMovieView">
                <option value="seen">seen</option>
                <option value="not seen">not seen</option>
                <option value="want to see">want to see</option>
            </select>
            <br>
            Director:
            <select name="addMovieDirector">
                <option value="blank"></option>
                <?php
                $query="SELECT dirname FROM director ORDER BY dirname";
                $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
                if (!$connection)
	                die('Could not connect');
                $result = pg_query($connection, $query) or
                    die("Error in query: $query." . pg_last_error($connection));
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
            <br>
            <input type="submit" name="submit" value="Add movie">
            </form>
            <?php
            break;

/*--------------------------- Add a director ---------------------------------*/

        case "addDirector":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Name: <input type="text" size="25" maxlength="25" name="addDirectorDirname">
            <input type="submit" name="submit" value="Add director">
            </form>
            <?php
            break;

/*--------------------------- Add an actor -----------------------------------*/

        case "addActor":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Name: <input type="text" size="25" maxlength="25" name="addActorActname">
            <input type="submit" name="submit" value="Add actor">
            </form>
            <?php
            break;

/*--------------------------- Add an oscar -----------------------------------*/

        case "addOscar":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Title: <input type="text" size="50" maxlength="100" name="addOscarTitle">
            <br>
            Year:
            <select name="addOscarYear">
                <?
                for ($i=1900; $i != 2009; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                echo "<option value=\"2009\" selected=\"selected\">2009</option>";
                for ($i=2010; $i != 2013; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                ?>
            </select>
            <br>
            Category:
            <select name="addOscarCategory">
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
            Recipient name: <input type="text" size="25" maxlength="25" name="addOscarName">
            <br>
            Status:
            <select name="addOscarStatus">
                <option value="won">won</option>
                <option value="nominated">nominated</option>
            </select>
            <br>
            <br>
            <input type="submit" name="submit" value="Add Oscar">
            </form>
            <br>
            <?php
            break;

/*----------------------- Change a movie's rating ----------------------------*/

        case "changeRating":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Title and year of movie:
            <br>
            <input type="text" size="50" maxlength="100" name="changeRatingTitle">
            <select name="changeRatingYear">
                <?
                for ($i=1900; $i != 2009; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                echo "<option value=\"2009\" selected=\"selected\">2009</option>";
                for ($i=2010; $i != 2013; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                ?>
            </select>
            <br>
            New rating:
            <select name="changeRatingStarRating">
                <option value="DEFAULT"></option>
                <option value="N/A">N/A</option>
                <option value="NO STARS">NO STARS</option>
                <option value="&frac12*">&frac12*</option>
                <option value="*">*</option>
                <option value="*&frac12">*&frac12</option>
                <option value="**">**</option>
                <option value="**&frac12">**&frac12</option>
                <option value="***">***</option>
                <option value="***&frac12">***&frac12</option>
                <option value="****">****</option>
            </select>
            <br>
            <br>
            <input type="submit" name="submit" value="Change star rating">
            </form>
            <?php
            break;

/*-------------------------- Remove an actor ---------------------------------*/

        case "removeActor":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Actor:
            <select name="removeActorActname">
                <?php
                $query="SELECT actname FROM actor ORDER BY actname";
                $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
                if (!$connection)
	                die('Could not connect');
                $result = pg_query($connection, $query) or
                    die("Error in query: $query." . pg_last_error($connection));
                $rows = pg_num_rows($result);
                echo $rows;
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
            <input type="submit" name="submit" value="Remove actor">
            </form>
            <?php
            break;

/*----------------------------------------------------------------------------*/

    }
?>

<p style="font-style: italic; font-size: 12px;">
&copy;2009 Tyler Hayes</p>

</body>
</html>
