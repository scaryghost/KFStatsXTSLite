/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite

import static com.github.etsai.kfstatsxtslite.message.PlayerStat.Result.*
import com.github.etsai.kfstatsxtslite.message.*
import groovy.sql.Sql

/**
 * Writes the statistics to the database
 * @author etsai
 */
public class StatWriter {
    private final def sql
    
    public StatWriter(Sql sql) {
        this.sql= sql
    }
    
    public void writeMatchStat(MatchStat stat) {
        def result= stat.getResult()
        def deaths= stat.getStats()
        deaths.each {name, count ->
            sql.execute("call update_deaths($name, $count);")
        }
        
        sql.execute("call update_difficulty_and_level(?, ?, ?, ?, ?, ?, ?)", [
            stat.getDifficulty(), stat.getLength(), stat.getLevelName(), 
            result == MatchStat.Result.WIN ? 1 : 0,
            result == MatchStat.Result.LOSS ? 1 : 0, stat.getWave(), stat.getElapsedTime()
        ])
    }
    
    public void writePlayerStat(Iterable<PlayerStat> stats) {
        def start= System.nanoTime()
        stats.each {stat ->
            def id= stat.getSteamID64()
            def category= stat.getCategory()
            if (category != "match") {
                stat.getStats().each {name, offset ->
                    sql.execute("call update_player_aggregate(?, ?, ?, ?)", [
                        id, name, offset, category
                    ])
                }
            } else {
                def result= stat.getResult()
                
                sql.execute("call update_record(?, ?, ?, ?)", [
                    id, result == WIN ? 1 : 0,
                    result == LOSS ? 1 : 0, 
                    result == DISCONNECT ? 1 : 0
                ])
            }
        }
        println System.nanoTime() - start
    }
}

