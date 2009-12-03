<html>
<head>
<title>TMDB: Tyler's Movie Database LOGIN</title>
</head>
<body style="background-color: black; font-family: Palatino;">

<form name="Login" action="tmdb_checklogin.php" method="post">
<div align="center">
<br>
<br>
<br>
<font color="red" size=7 style="font-variant: small-caps">T M D B</font><br>
<font color="white" size=2><i>Tyler's Movie Database</i></font>

    <?php
	for ($i=0; $i<7; ++$i)
	   echo "\n    <br>";
    ?>

    <p style="color:white;">
    Password:
    <input type="password" size="20" maxlength="20" name="password">
    <input type="submit" value="login">
</p>
<br>
<br>
<font color="white">
<p style="font-style: italic; font-size: 10px;">
&copy;2009 Tyler Hayes</p></font>
</div>
</form>
</body>
</html>
