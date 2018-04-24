<?php

$servername = "localhost";
$username = "REPLACE_ME";
$password = "REPLACE_ME";
$dbname = "app";

try {
    	$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }
catch(PDOException $e)
    {
    	die("No database connection");
    }

?>