/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite

import static com.github.etsai.kfstatsxtslite.message.PlayerStat.MatchInfo.Result.*
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
        stat.getStats().each {name, count ->
            sql.execute("call update_deaths($name, $count);")
        }
        
        def result= stat.getResult()
        def wins= (result == MatchStat.Result.WIN) ? 1 : 0
        def losses= (result == MatchStat.Result.WIN) ? 1 : 0
        sql.execute("call update_difficulty(?, ?, ?, ?, ?, ?)", [
            stat.getDifficulty(), stat.getLength(), wins, losses, stat.getWave(), stat.getElapsedTime()
        ])
        sql.execute("call update_level(${stat.getLevelName()}, $wins, $losses, ${stat.getElapsedTime()})")
    }
    
    public void writeBlankPlayerStat(PlayerStat stat) {
        stat.getStats().each {name, offset ->
            sql.execute("call update_aggregate(?, ?, ?)", [
                name, offset, category
            ])
        }
    }

    public void writePlayerStat(Iterable<PlayerStat> stats) {
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
                def matchInfo= stat.getMatchInfo()
                
                sql.execute("call update_record(?, ?, ?, ?)", [
                    id, matchInfo.result == WIN ? 1 : 0,
                    matchInfo.result == LOSS ? 1 : 0, 
                    matchInfo.result == DISCONNECT ? 1 : 0
                ])

                sql.execute("call insert_session(?, ?, ?, ?, ?, ?)", [
                    id, matchInfo.level, matchInfo.difficulty, matchInfo.length, 
                    matchInfo.result.toString().toLowerCase(), matchInfo.wave
                ])
            }
        }
    }
}

