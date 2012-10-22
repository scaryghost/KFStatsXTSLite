<?php
require 'KFStatsXReader.php';
require 'MySqlInfo.php';

$reader= new KFStatsXReader(MySqlInfo::$dbAddress, MySqlInfo::$dbName, MySqlInfo::$dbUser, MySqlInfo::$dbPwd);
$jsonData= array();

$jsonData["cols"]= array(array("label" => "Death", "type" => "string"), array("label" => "Count", "type" => "number"));
$jsonData["rows"]= array();
$index= 0;
foreach($reader->getDeaths() as $death) {
    $row= array();

    $row["c"]= array();
    $row["c"][0]= array("v" => $death["name"], "f" => null);
    $row["c"][1]= array("v" => intval($death["count"]), "f" => null);
    $jsonData["rows"][$index]= $row;
    $index++;
}

echo  json_encode($jsonData);
?>
