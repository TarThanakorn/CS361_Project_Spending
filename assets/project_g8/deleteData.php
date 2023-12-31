<?php

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "cs361_project_g8";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$request_uri = $_SERVER['REQUEST_URI'];
$uri_parts = explode('/', $request_uri);
$id = end($uri_parts);
$sql = "DELETE FROM account WHERE id = $id";

if ($conn->query($sql) === TRUE) {
    $arr['StatusID'] = "1"; 
    $arr['Error'] = "";
} else {
    $arr['StatusID'] = "0"; 
    $arr['Error'] = "Cannot save data!";
}

$conn->close();

?>
