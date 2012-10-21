<?php

class KFStatsXReader {
    private $dbConn;

    function __construct($address, $database, $user, $password) {
        $this->dbConn= new mysqli($address, $user, $password, $database);
        if ($this->dbConn->connect_errno) {
            echo "Failed to connect to MySQL: (" . $this->dbConn->connect_errno . ") " . $this->dbConn->connect_error;
        }
    }

    private function query($table, $constraints= '') {
        $index= 0;
        $stats= array();

        $sql= "SELECT * FROM " . $table ;
        if ($constraints !== '') {
            $sql= $sql . " WHERE " . $constraints;
        }
        $sql= $sql . " ORDER BY id ASC";
        $result= $this->dbConn->query($sql);
        while ($row = $result->fetch_assoc()) {
            $stats[$index]= $row;
            $index++;
        }
        return $stats;
    }

    function getDeaths() {
        return $this->query("deaths");
    }

    function getDifficulties() {
        return $this->query("difficulties");
    }

    function getLevels() {
        return $this->query("levels");
    }

    function getAggregate() {
        return $this->query("aggregate");
    }
    function getSteamID64() {
        return $this->query("records");
    }

    function getPlayerStats($steamID64) {
        return $this->query("player", "steamID64='" . $steamID64 . "'");
    }

    function getSessions($steamID64) {
        return $this->query("sessions", "steamID64='" . $steamID64 . "'");
    }

    function getRecords() {
        return $this->query("records");
    }

    function getRecord($steamID64) {
        return $this->query("records", "steamID64='" . $steamID64 . "'");
    }
}

?>
