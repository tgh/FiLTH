<?php
    session_start(); 
    include '../security.php';
    if ($_POST['password'] == $user_pw)
    {
	$_SESSION['who'] = 'user';
        header("Location: http://www.cs.pdx.edu/~tgh/tmdb_home.php");
	exit;
    }
    elseif ($_POST['password'] == $dba_pw)
    {
	$_SESSION['who'] = 'dba';
        header("Location: http://www.cs.pdx.edu/~tgh/tmdb_dba.php");
	exit;
    }
?>

<html>
<head>
<title>TMDB: Tyler's Movie Database LOGIN</title>
</head>
<body style="background-color: black; font-family: Palatino;">
<div align="center">
<br>
<br>
<br>
<font color="red" size=7 style="font-variant: small-caps">T M D B</font><br>
<font color="white" size=2><i>Tyler's Movie Database</i></font>

<?php
    for ($i=0; $i<7; ++$i)
	echo "\n<br>";
?>

<div align="center">
<form name="Login" action="<?php echo $PHP_SELF ?>" method="post">
    <p style="color:white;">
    Password:
    <input type="password" size="20" maxlength="20" name="password">
    <input type="submit" value="login">
</form>
<p style="color:darkred; font-weight=bold;">
Invalid password </p>
<br>
<font color="white">
<p style="font-style: italic; font-size: 10px;">
&copy;2009 Tyler Hayes</p></font>
</div>
</body>
</html>
