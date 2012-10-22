<?php
require_once 'Common.php';
require_once 'KFStatsXReader.php';
require_once 'MySqlInfo.php';

$reader= new KFStatsXReader(MySqlInfo::$dbAddress, MySqlInfo::$dbName, MySqlInfo::$dbUser, MySqlInfo::$dbPwd);
$jsonData= array();

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
    $row["c"][3]= array("v" => intval($level["time"]), "f" => Common::formatTime(intval($level["time"])), 
            "p" => array("style" => 'text-align: center;'));
    $jsonData["rows"][$index]= $row;
    $index++;
}

echo  json_encode($jsonData);
?>
