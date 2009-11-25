<?php
    session_start();

    if (!isset($_SESSION['who']) || $_SESSION['who'] != 'user')
    {
        header("Location: http://www.cs.pdx.edu/~tgh/tmdb.php");
        exit;
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
<a href="tmdb_logout.php">Logout</a>
</p>
<br>
<form name="Query"
      action="tmdb_result.php"
      method="post">
      Year: <input type="text"
	     size="9"
	     maxlength="9"
	     name="year">
      <br>
      Tyler's Rating: <input type="text"
		       size="4"
		       maxlength="4"
		       name="myRating">
      <br>
      <input type="submit" name="submit">
</form>

<p style="font-style: italic; font-size: 12px;">
&copy;2009 Tyler Hayes</p>

</body>
</html>
