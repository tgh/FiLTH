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
    <select name="action">
        <option value="addMovie">Add a movie</option>
        <option value="addDirector">Add a director</option>
        <option value="addActor">Add an actor</option>
        <option value="addOscar">Add an oscar</option>
        <option value="changeRating">Change movie rating</options>
        <option value="removeActor">Remove an actor</option>
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
                for ($i=1900; $i != 2013; ++$i)
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
            <input type="submit" name="submit" value="Submit">
            </form>
            <?php
            break;

/*--------------------------- Add a director ---------------------------------*/

        case "addDirector":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Name: <input type="text" size="25" maxlength="25" name="addDirectorDirname">
            <br>
            <input type="submit" name="submit" value="Submit">
            </form>
            <?php
            break;

/*--------------------------- Add an actor -----------------------------------*/

        case "addActor":
            ?>
            <form action="tmdb_dba_result.php" method="post">
            Name: <input type="text" size="25" maxlength="25" name="addActorActname">
            <br>
            <input type="submit" name="submit" value="Submit">
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
                for ($i=1925; $i != 2013; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                ?>
            </select>
            <br>
            Category:
            <select name="addOscarCategory">
                <option value="picture">Best Picture</option>
                <option value="actor">Best Actor</option>
                <option value="actress">Best Actress</option>
                <option value="supactor">Best Supporting Actor</option>
                <option value="supactress">Best Supporting Actress</option>
                <option value="director">Best Director</option>
                <option value="cine">Best Cinematography</option>
                <option value="adapted">Best Adapted Screenplay</option>
                <option value="original">Best Original Screenplay</option>
                <option value="foreign">Best Foreign Language Film</option>
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
                for ($i=1900; $i != 2013; ++$i)
                    echo "<option value=\"$i\">$i</option>";
                ?>
            </select>
            <br>
            New rating:
            <select name="changeRatingStarRating">
                <option value="default"></option>
                <option value="n/a">N/A</option>
                <option value="noStars">NO STARS</option>
                <option value="1/2*">&frac12*</option>
                <option value="*">*</option>
                <option value="*1/2">*&frac12</option>
                <option value="**">**</option>
                <option value="**1/2">**&frac12</option>
                <option value="***">***</option>
                <option value="***1/2">***&frac12</option>
                <option value="****">****</option>
            </select>
            <br>
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
            <br>
            <input type="submit" name="submit" value="Submit">
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
