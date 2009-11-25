<html>
<body style="background-color:black">

<form name="Login" action="tmdb_checklogin.php" method="post">
<div align="center">
    <?php
	for ($i=0; $i<10; ++$i)
	   echo "\n    <br>";
    ?>

    <p style="color:white; font-family:Palatino;">
    Password:
    <input type="password"
	   size="20"
	   maxlength="20"
	   name="password">
    <input type="submit"
	   value="login">
</p>
</div>
</form>
</body>
</html>
