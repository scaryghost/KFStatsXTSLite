<?php

class KFStatsXReader {
    private $dbConn;

    function __construct($url, $table, $user, $pwd) {
        $this->dbConn= mysql_connect($url, $user, $pwd);
        if (!$this->dbConn) {
            die('Could not connect: ' . mysql_error());
        }
        mysql_select_db($table, $this->dbConn);
    }

    private function query($columns, $sql) {
        $index= 0;
        $stats= array();

        $result= mysql_query($sql);
        while($row= mysql_fetch_array($result)) {
            $values= array();
            foreach($columns as $col) {
                $values[$col]= $row[$col];
            }
            $stats[$index]= $values;
            $index++;
        }
        return $stats;
    }

    function getDeaths() {
        return $this->query(array("name", "count"), "SELECT * FROM deaths");
    }

    function getAggregate() {
        return $this->query(array("category", "stat", "value"), "SELECT * FROM aggregate");
    }

    function getDifficulties() {
        return $this->query(array("name", "length", "wins", "losses", "wave", "time"), "SELECT * FROM diffficulties");
    }

    function getLevels() {
        return $this->query(array("name", "wins", "losses", "time"), "SELECT * FROM levels");
    }
}

?>
