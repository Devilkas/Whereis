<?php
$date_today = date("Y-m-d");
//$date_today_time = date("H:m:s");

//$response["success"] = true;
//$responseJson = json_encode($response);

$finalName = $date_today.".json";

$username =$_POST["username"];
$day = $_POST["day"];
$time = $_POST["time"];
$lat = $_POST["latitude"];
$lon = $_POST["longitude"];

$AdditionalArray = array(
    'day' => $day,
    'time' => $time,
    'latitude' => $lat,
    'longitude' => $lon
);


if($_SERVER['REQUEST_METHOD'] === 'POST'){
if (!(file_exists($date_today.'.json'))){
  $file = $finalName;
  $tempArray[$username][] = $AdditionalArray;
  $jsonData = json_encode($tempArray);
  file_put_contents($file, $jsonData);
} else {
  $file = $finalName;
  $data_results = file_get_contents($file);
  $tempArray = json_decode($data_results, true);
  $tempArray[$username][] = $AdditionalArray;
  $jsonData = json_encode($tempArray);
  file_put_contents($file,$jsonData);
	
  }
}
//echo json_encode($response);
?>