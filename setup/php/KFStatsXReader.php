<?php

/**
 * Retrieves statistics from the MySql database, and wraps the information in arrays
 * @author etsai
 */
class KFStatsXReader {
    private $dbConn;

    /**
     * Create a connection to the MySql database.
     * @param   $address    Address of the database
     * @param   $database   Database name to use
     * @param   $user       User name to login as
     * @param   $password   Password for the login
     * @throws  Exception   If database connection failed
     */
    function __construct($address, $database, $user, $password) {
        $this->dbConn= new mysqli($address, $user, $password, $database);
        if ($this->dbConn->connect_errno) {
            throw new Exception("Failed to connect to MySQL: (" . $this->dbConn->connect_errno . ") " . $this->dbConn->connect_error);
        }
    }

    /**
     * Performs a query returning the results in an array.
     * @param   $table      Table name to query
     * @param   $order      Criteria to order the results by
     * @param   $constraints    Constraints to filter the results by, default value is blank
     * @return  Array containing all of the rows
     */
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

    /**
     * Retrieve the death counts represented as an array of maps, sorted by name.  The inner maps are
     * indexed with the keys: name and count.
     * @return  Array containing the death names and counts.
     */
    function getDeaths() {
        return $this->query("deaths", "name");
    }

    /**
     * Retrieve the difficulty results as an array of maps, sorted by name and length.  The inner maps 
     * are indexed with the keys: name, length, wins, losses, wave, and time.
     * @return  Array containing the difficulty results
     */
    function getDifficulties() {
        return $this->query("difficulties", "name, length");
    }

    /**
     * Retrieve the level results as an array of maps, sorted by name .  The inner maps 
     * are indexed with the keys: name, wins, losses, and time.
     * @return  Array containing the level results
     */
    function getLevels() {
        return $this->query("levels", "name");
    }

    /**
     * Retrieve all stat categories as a numeric array.  Categories are used to different between perks, weapons, 
     * actions, etc.  They are used when retrieving aggregate or player stats.
     * @return  All used stat categories
     */
    function getCategories() {
        $index= 0;
        $categories= array();

        $result= $this->dbConn->query("select category from aggregate group by category;");
        while ($row = $result->fetch_assoc()) {
            $categories[$index]= $row["category"];
            $index++;
        }
        return $categories;
    }

    /**
     * Retrieve the aggregate stats under the given category as an array of maps.  The inner maps 
     * are indexed with the keys: stat and value
     * @param   $category   Stat category to filter on
     * @return  Array of aggregate stats
     */
    function getAggregate($category) {
        return $this->query("aggregate", "category, stat", "category='" . $category . "'");
    }

    /**
     * Retrieve all stored steamID64 as a numeric array.
     * @return  All steamID64
     */
    function getSteamID64() {
        $index= 0;
        $steamID64= array();

        $result= $this->dbConn->query("select steamID64 from records order by id;");
        while ($row = $result->fetch_assoc()) {
            $steamID64[$index]= $row["steamID64"];
            $index++;
        }
        return $steamID64;
    }

    /**
     * Retrieve the player stats of a specific player and stat category as an array of maps.  The inner 
     * maps are indexed with the keys: stat and value
     * @param   $steamID64      SteamID64 of the player
     * @param   $category       Stats category to filter on
     * @return  Array of player stats
     */
    function getPlayerStats($steamID64, $category) {
        return $this->query("player", "category, stat", "steamID64='" . $steamID64 . "', category='" . $category . "'");
    }

    /**
     * Retrive all of the sessions for a specific player as an array of maps.  The inner maps are indexed 
     * with the keys: level, difficulty, length, result, wave, and time
     * @param   $steamID64      SteamID64 of the player
     * @return  Array of session history
     */
    function getSessions($steamID64) {
        return $this->query("sessions", "id", "steamID64='" . $steamID64 . "'");
    }

    /**
     * Retrieve overall records for all players as an array of maps.  The inner maps are indexed with the keys: 
     * wins, losses, disconnects
     * @return  Overall records for all players
     */
    function getRecords() {
        return $this->query("records", "id");
    }

    /**
     * Retrieve overall records for the specific player as a map.  The map is indexed with the keys: wins, losses, disconnects
     * @return  Overall records for the specific player
     */
    function getRecord($steamID64) {
        $record= $this->query("records", "id", "steamID64='" . $steamID64 . "'");
        return $record[0];
    }
}

?>
