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
<body style="background-color:black">
<div align="center">

<?php
    for ($i=0; $i<10; ++$i)
	echo "\n<br>";
?>

<div align="center">
<form name="Login"
      action="<?php echo $PHP_SELF ?>"
      method="post">
    <p style="color:white; font-family:Palatino;">
    Password:
    <input type="password"
	   size="20"
	   maxlength="20"
	   name="password">
    <input type="submit"
	   value="login">
</form>
<p style="color:darkred; font-family:Palatino; font-weight=bold;">
Invalid password </p>

</div>
</body>
</html>
