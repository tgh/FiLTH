<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'user')
    {
        header("Location: http://www.cs.pdx.edu/~tgh/tmdb.php");
        exit;
    }

    include '../security.php';
    $host='db.cecs.pdx.edu';
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
<a href="tmdb_logout.php">Logout</a>
</p>

<br>
General keyword search:
<br>

<form action="tmdb_result.php" method="post">
    <input type="text" size="20" maxlength="50" name="generalSearch">
    <input type="submit" name="submit" value="Search">
</form>

<br>
Advanced Search:
<br>

<form action=tmdb_result.php" method="post">
    Title: <input type="text" size="50" maxlength="100" name="advTitle">
           <br>
    Year: from:
    <select name="advYearStart">
        <option value="blank"></option>
        <?
        for ($i=2013; $i != 1899; --$i)
            echo "<option value=\"$i\">$i</option>";
        ?>
    </select>
    to:
    <select name="advYearEnd">
        <option value="blank"></option>
        <?
        for ($i=2013; $i != 1899; --$i)
            echo "<option value=\"$i\">$i</option>";
        ?>
    </select>
    <br>
    Star rating: from:
    <select name=advStarStart">
        <option value="blank"></option>
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
    to:
    <select name="advStarEnd">
        <option value="blank"></option>
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
    Country:
    <select name="advCountry">
        <option value="blank"></option>
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
                <option value="<?php echo $row[0];?>"><?php echo $row[0];?></option>
                <?php
            }
        }
        pg_close($connection);
        ?>
    </select>
    <br>
    View status: &nbsp&nbsp
    Seen <input type="checkbox" name="advView" value="seen" checked="checked">
    &nbsp&nbsp
    Not seen <input type="checkbox" name="advView" value="unseen">
    <br>
    Director:
    <select name="advDirector">
        <option value="blank"></option>
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
    Actor:
    <select name="advActor">
        <option value="blank"></option>
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
    Screenwriter:
    <select name="advScreen">
        <option value="blank"></option>
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
    Cinematographer:
    <select name="advCine">
        <option value="blank"></option>
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
    <br>
    Displayed in result:
    <br>
        Title <input type="checkbox" name="advDisplay" value="title" checked="checked">
        &nbsp&nbsp
        Year <input type="checkbox" name="advDisplay" value="year" checked="checked">
        &nbsp&nbsp
        Star rating <input type="checkbox" name="advDisplay" value="star" checked="checked">
        &nbsp&nbsp
        Country <input type="checkbox" name="advDisplay" value="country" checked="checked">
        &nbsp&nbsp
        View status <input type="checkbox" name="advDisplay" value="view" checked="checked">
        <br>
        Director <input type="checkbox" name="advDisplay" value="dir" checked="checked">
        &nbsp&nbsp
        Actor <input type="checkbox" name="advDisplay" value="dir" checked="checked">
        &nbsp&nbsp
        Screenwriter <input type="checkbox" name="advDisplay" value="dir" checked="checked">
        &nbsp&nbsp
        Cinematographer <input type="checkbox" name="advDisplay" value="dir" checked="checked">
        &nbsp&nbsp
        Oscar <input type="checkbox" name="advDisplay" value="oscar" checked="checked">
    <br>
    <br>
    Sort by:
    <select name="advSort">
        <option value="title">Title</option>
        <option value="year">Year</option>
        <option value="star">Star Rating</option>
        <option value="country">Country</option>
        <option value="director">Director</option>
        <option value="actor">Actor</option>
        <option value="screen">Screenwriter</option>
        <option value="cine">Cinematographer</option>
    </select>
    <input type="submit" name="submit" value="Search">
</form>

<p style="font-style: italic; font-size: 10px;">
&copy;2009 Tyler Hayes</p>

</body>
</html>
