<?php
require 'KFStatsXReader.php';
require 'MySqlInfo.php';

$reader= new KFStatsXReader(MySqlInfo::$dbAddress, MySqlInfo::$dbName, MySqlInfo::$dbUser, MySqlInfo::$dbPwd);
$jsonData= array();

function convert($timeStr) {
    $intTime= intval($timeStr);
    
    $seconds= $intTime % 60;
    $minutes= ($intTime / 60) % 60;
    $hours= ($intTime / 3600) % 24;
    $days= (($intTime / 3600) / 24);

    return sprintf("%d days %02d:%02d:%02d", $days, $hours, $minutes, $seconds);
}

$jsonData["cols"]= array(array("label" => "Name", "type" => "string"), 
        array("label" => "Wins", "type" => "number"), array("label" => "Losses", "type" => "number"), 
        array("label" => "Time", "type" => "string"));

$jsonData["rows"]= array();
$index= 0;
foreach($reader->getLevels() as $level) {
    $row= array();

    $row["c"]= array();
    $row["c"][0]= array("v" => $level["name"], "f" => null);
    $row["c"][1]= array("v" => intval($level["wins"]), "f" => null, "p" => array("style" => 'text-align: center;'));
    $row["c"][2]= array("v" => intval($level["losses"]), "f" => null, "p" => array("style" => 'text-align: center;'));
    $row["c"][3]= array("v" => intval($level["time"]), "f" => convert($level["time"]), "p" => array("style" => 'text-align: center;'));
    $jsonData["rows"][$index]= $row;
    $index++;
}

echo  json_encode($jsonData);
?>
