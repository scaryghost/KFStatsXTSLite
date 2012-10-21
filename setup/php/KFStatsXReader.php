<?php

class KFStatsXReader {
    private $dbConn;

    function __construct($address, $database, $user, $password) {
        $this->dbConn= new mysqli($address, $user, $password, $database);
        if ($this->dbConn->connect_errno) {
            echo "Failed to connect to MySQL: (" . $this->dbConn->connect_errno . ") " . $this->dbConn->connect_error;
        }
    }

    private function query($table, $order, $constraints= '') {
        $index= 0;
        $stats= array();

        $sql= "SELECT * FROM " . $table ;
        if ($constraints !== '') {
            $sql= $sql . " WHERE " . $constraints;
        }
        $sql= $sql . " ORDER BY " . $order . " ASC";
        $result= $this->dbConn->query($sql);
        while ($row = $result->fetch_assoc()) {
            $stats[$index]= $row;
            $index++;
        }
        return $stats;
    }

    function getDeaths() {
        return $this->query("deaths", "name");
    }

    function getDifficulties() {
        return $this->query("difficulties", "name, length");
    }

    function getLevels() {
        return $this->query("levels", "name");
    }

    function getAggregate() {
        return $this->query("aggregate", "category, stat");
    }
    function getSteamID64() {
        return $this->query("records", "id");
    }

    function getPlayerStats($steamID64) {
        return $this->query("player", "category, stat", "steamID64='" . $steamID64 . "'");
    }

    function getSessions($steamID64) {
        return $this->query("sessions", "id", "steamID64='" . $steamID64 . "'");
    }

    function getRecords() {
        return $this->query("records", "id");
    }

    function getRecord($steamID64) {
        return $this->query("records", "id", "steamID64='" . $steamID64 . "'");
    }
}

?>
