<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'user')
    {
        header("Location: http://www.cs.pdx.edu/~tgh/tmdb.php");
        exit;
    }
  
    include '../security.php';
    $host='db.cecs.pdx.edu';

    function createSql($year) {
        $sqlStmt = "SELECT * FROM movie WHERE year = " . $year;
        $sqlStmt = $sqlStmt . " ORDER BY title;";
        return $sqlStmt;
    }
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
<a href="http://www.cs.pdx.edu/~tgh/tmdb_logout.php">Get me out of here!</a>
</p>
<p style="font-size: 12px; font-style: italic;"> 
<a href="http://www.cs.pdx.edu/~tgh/tmdb_home.php">Search again.</a>
</p>

<?php
    $query = createSql($_POST['year']);

    $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
    if (!$connection)
    {
	    die('Could not connect');
    }

    $result = pg_query($connection, $query) or
    die("Error in query: $query." . pg_last_error($connection));

    $rows = pg_num_rows($result);

    if($rows > 0)
    {
	    for($i=0; $i<$rows; ++$i)
	    {
	        $row = pg_fetch_row($result, $i);
?>
	        <font size="-1"><b><?php echo $row[0];?></b>
	        <?php if ($row[2] != NULL)
	                  echo $row[2] . " ";
                  else
                      echo "[not seen] ";
                  ?><i><?php echo $row[3] . " ";?></i>
            </font>
            <br>
<?php
	    }
    }
    else
    {
?>
	    <font size="-1">No movies in the year <?php $_POST['year'] ?>."</font>
<?php
    }

pg_close($connection);
?>

<p style="font-size: 12px; font-style: italic;"> 
<a href="http://www.cs.pdx.edu/~tgh/tmdb_logout.php">Get me out of here!</a>
</p>
<p style="font-size: 12px; font-style: italic;"> 
<a href="http://www.cs.pdx.edu/~tgh/tmdb_home.php">Search again.</a>
</p>
<p style="font-style: italic; font-size: 12px;">
&copy;2009 Tyler Hayes</p>

</body>
</html>
