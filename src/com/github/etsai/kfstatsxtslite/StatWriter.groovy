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
            
            sql.execute("insert or ignore into deaths values ($id, $name, 0);")
            sql.execute("update deaths set count= count + $count where id=$id")
        }
        
        def diffId= "${stat.getDifficulty()}-${stat.getLength()}".hashCode()
        def diffRow= sql.firstRow("select time from difficulties where id=$diffId")
        def levelId= stat.getLevelName().hashCode()
        def levelRow= sql.firstRow("select time from levels where id=$levelId")
        def result= stat.getResult()
        def elapsedTime= new Time(stat.getElapsedTime())
        
        if (diffRow == null) {
            sql.execute("insert into difficulties values(?, ?, ?, ?, ?, ?, ?)", [
                diffId, stat.getDifficulty(), stat.getLength(),
                result == MatchStat.Result.WIN ? 1 : 0,
                result == MatchStat.Result.LOSS ? 1 : 0, stat.getWave(), elapsedTime])
        } else {
            sql.execute("update difficulties set wins=wins + ?, losses=losses + ?, wave=wave + ?, time=? where id=?", [
                result == MatchStat.Result.WIN ? 1 : 0, 
                result == MatchStat.Result.LOSS ? 1 : 0,
                stat.getWave(), elapsedTime.add(diffRow.time), diffId
            ])
        }
        
        if (levelRow == null) {
            sql.execute("insert into levels values(?, ?, ?, ?, ?)", [
                levelId, stat.getLevelName(), 
                result == MatchStat.Result.WIN ? 1 : 0,
                result == MatchStat.Result.LOSS ? 1 : 0, elapsedTime
            ])
        } else {
            sql.execute("update levels set wins=wins + ?, losses=losses + ?, time=? where id=?", [
                result == MatchStat.Result.WIN ? 1 : 0,
                result == MatchStat.Result.LOSS ? 1 : 0, 
                elapsedTime.add(levelRow.time), levelId
            ])
        }
    }
    
    public void writePlayerStat(Iterable<PlayerStat> stats) {
        def start= System.nanoTime()
        stats.each {stat ->
            def category= stat.getCategory()
            if (category != "match") {
                def playerId= "${stat.getSteamID64()}-${category}".hashCode()
                def row= sql.firstRow("select stats from player where id=$playerId")
                def statValues= [:]
                
                if (row != null) {
                    row.stats.tokenize(",").each {keyval ->
                        def split= keyval.tokenize("=")
                        statValues[split[0]]= split[1].toInteger()
                    }
                }
                stat.getStats().each {name, value ->
                    def aggrId= "${name}-${category}".hashCode()
                    
                    if (statValues[name] == null) {
                        statValues[name]= 0
                    }
                    statValues[name]+= value
                    
                    sql.execute("insert or ignore into aggregate values ($aggrId, $name, 0, $category);")
                    sql.execute("update aggregate set value= value + $value where id=$aggrId")
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
                def result= stat.getResult()
                
                sql.execute("insert or ignore into records values ($id, ${stat.getSteamID64()}, 0, 0, 0);")
                sql.execute("update records set wins= wins + ?, losses= losses + ?, disconnects= disconnects + ? where id=$id", [
                    result == WIN ? 1 : 0,
                    result == LOSS ? 1 : 0, 
                    result == DISCONNECT ? 1 : 0
                ])
            }
        }
        println System.nanoTime() - start
    }
}

