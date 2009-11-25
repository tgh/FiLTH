<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'dba')
    {
	header("Location: http://www.cs.pdx.edu/~tgh/tmdb.php");
	exit;
    }

    $host='db.cecs.pdx.edu';  
    include '../security.php';
    $connection = pg_connect("host=$host dbname=tgh user=tgh password=$db_pw");
    if (!$connection)
    {
	die('Could not connect');
    }
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

<?php pg_close($connection);?>

<p style="font-style: italic; font-size: 12px;">
&copy;2009 Tyler Hayes</p>

</body>
</html>
