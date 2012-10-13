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
        def deaths= stat.getStats();
        deaths.each {name, count ->
            def id= name.hashCode()
            def row= sql.firstRow("select * from deaths where id=$id")
            
            if (row == null) {
                sql.execute("insert into deaths values(?, ?, ?)", [id, name, count])
            } else {
                sql.execute("update deaths set count=? where id=?", [count + row.count, id])
            }
        }
        
        def diffId= "${stat.getDifficulty()}-${stat.getLength()}".hashCode()
        def diffRow= sql.firstRow("select * from difficulties where id=$diffId")
        def levelId= stat.getLevelName().hashCode()
        def levelRow= sql.firstRow("select * from levels where id=$levelId")
        def result= stat.getResult()
        def elapsedTime= new Time(stat.getElapsedTime())
        
        if (diffRow == null) {
            sql.execute("insert into difficulties values(?, ?, ?, ?, ?, ?, ?)", [
                diffId, stat.getDifficulty(), stat.getLength(),
                result == MatchStat.Result.WIN ? 1 : 0,
                result == MatchStat.Result.LOSS ? 1 : 0, stat.getWave(), elapsedTime])
        } else {
            sql.execute("update difficulties set wins=?, losses=?, wave=?, time=? where id=?", [
                result == MatchStat.Result.WIN ? diffRow.wins + 1 : diffRow.wins, 
                result == MatchStat.Result.LOSS ? diffRow.losses + 1 : diffRow.losses,
                diffRow.wave + stat.getWave(), elapsedTime.add(diffRow.time), diffId
            ])
        }
        
        if (levelRow == null) {
            sql.execute("insert into levels values(?, ?, ?, ?, ?)", [
                levelId, stat.getLevelName(), 
                result == MatchStat.Result.WIN ? 1 : 0,
                result == MatchStat.Result.LOSS ? 1 : 0, elapsedTime
            ])
        } else {
            sql.execute("update levels set wins=?, losses=?, time=? where id=?", [
                result == MatchStat.Result.WIN ? levelRow.wins + 1 : levelRow.wins,
                result == MatchStat.Result.LOSS ? levelRow.losses + 1: levelRow.losses, 
                elapsedTime.add(levelRow.time),
                levelId
            ])
        }
    }
    
    public void writePlayerStat(Iterable<PlayerStat> stats) {
        stats.each {stat ->
            def category= stat.getCategory()
            if (category != "match") {
                def playerId= "${stat.getSteamID64()}-${category}".hashCode()
                def row= sql.firstRow("select * from player where id=$playerId")
                def statValues= [:]
                
                if (row != null) {
                    row.stats.tokenize(",").each {keyval ->
                        def split= keyval.tokenize("=")
                        statValues[split[0]]= split[1].toInteger()
                    }
                }
                stat.getStats().each {name, value ->
                    def aggrId= "${name}-${category}".hashCode()
                    def aggrRow= sql.firstRow("select * from aggregate where id=$aggrId")
                    
                    if (statValues[name] == null) {
                        statValues[name]= 0
                    }
                    statValues[name]+= value
                    
                    if (aggrRow == null) {
                        sql.execute("insert into aggregate values (?, ?, ?, ?)", [
                            aggrId, name, value, category
                        ])
                    } else {
                        sql.execute("update aggregate set value=? where id=?", [
                            aggrRow.value + value, aggrId
                        ])
                    }
                }
                
                def updatedValues= []
                statValues.each {name, value ->
                    updatedValues << "${name}=${value}"
                }
                
                if (row == null) {
                    sql.execute("insert into player values(?, ?, ?, ?)", [
                        playerId, stat.getSteamID64(), updatedValues.join(","), category
                    ])
                } else {
                    sql.execute("update player set stats=? where id=?", [
                        updatedValues.join(","), playerId
                    ])
                }
            } else {
                def id= stat.getSteamID64().hashCode()
                def row= sql.firstRow("select * from records where id=${id}")
                def result= stat.getResult()
                
                if (row == null) {
                    sql.execute("insert into records values(?, ?, ?, ?, ?)", [
                        id, stat.getSteamID64(),
                        result == WIN ? 1 : 0,
                        result == LOSS ? 1 : 0, 
                        result == DISCONNECT ? 1 : 0
                    ])
                } else {
                    sql.execute("update records set wins=?, losses=?, disconnects=? where id=?", [
                        result == WIN ? row.wins + 1 : row.wins, 
                        result == LOSS ? row.losses + 1 : row.losses, 
                        result == DISCONNECT ? row.disconnects + 1 : row.disconnects,
                        id
                    ])
                }
            }
        }
    }
}

