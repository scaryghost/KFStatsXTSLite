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
    private static final def deathSql= 
    '''replace into deaths (id, name, count) values
    (?, coalesce(( select name from deaths where id=?),?),?);'''
    private static final def recordSql= 
    '''replace into records (id, steamid, wins, losses, disconnects) values 
    (?, coalesce(( select steamid from records where id=?),?), ?, ?, ?);'''
    private static final def aggregateSql= 
    '''replace into aggregate (id, stat, value, category) values 
    (?, coalesce(( select stat from aggregate where id=?),?), ?, 
    coalesce(( select category from aggregate where id=?),?));'''
    private static final def difficultySql= 
    '''replace into difficulties (id, name, length, wins, losses, wave, time) values
    (?, coalesce(( select name from difficulties where id=?),?),
    coalesce(( select length from difficulties where id=?),?), ?, ?, ?, ?);'''
    private static final def levelSql=
    '''replace into levels (id, name, wins, losses, time) values 
    (?, coalesce(( select name from levels where id=?),?), ?, ?, ?);'''
    private static final def playerSql= 
    '''replace into player (id, steamid, stats, category) values
    (?, coalesce(( select steamid from player where id=?),?), ?, 
    coalesce(( select category from player where id=?),?));'''
    
    private final def sql
    
    public StatWriter(Sql sql) {
        this.sql= sql
    }
    
    public void writeMatchStat(MatchStat stat) {
        def deaths= stat.getStats();
        deaths.each {name, count ->
            def id= name.hashCode()
            def row= sql.firstRow('select * from deaths where id=${id}')
            
            sql.execute(deathsSql, [id, id, name, count + row.count])
        }
        
        def diffId= "${stat.getDifficulty()}-${stat.getLength()}}".hashCode()
        def diffRow= sql.firstRow('select * from difficulties where id=${id}')
        def levelId= stat.getLevelName().hashCode()
        def levelRow= sql.firstRow('select * from levels where id=${id}')
        def result= stat.getResult()
        
        sql.execute(difficultySql, [diffId, diffId, diffRow.name, diffId, diffRow.length, 
            result == MatchStat.Result.WIN ? diffRow.wins + 1 : diffRow.wins,
            result == MatchStat.Result.LOSS ? diffRow.losses + 1 : diffRow.losses,
            diffRow.wave + stat.getWave(), new Time(stat.getElapsedTime()).add(diffRow.time)])
        sql.execute(levelSql, [levelId, levelId, levelRow.name, 
            result == MatchStat.Result.WIN ? levelRow.wins + 1 : levelRow.wins,
            result == MatchStat.Result.LOSS ? levelRow.losses + 1: levelRow.losses, 
            new Time(stat.getElapsedTime()).add(levelRow.time)])
    }
    
    public void writePlayerStat(Iterable<PlayerStat> stats) {
        stats.each {stat ->
            def category= stat.getCategory()
            if (category != "match") {
                def id= "${stat.getSteamID64()}-${category}".hashCode()
                def row= sql.firstRow('select * from player where id=${id}')
                def statValues= [:]
                
                row.category.tokenize(",").each {keyval ->
                    def split= keyval.tokenize("=")
                    statValues[split[0]]= split[1].toInteger()
                }
                stat.getStats().each {name, value ->
                    def aggrId= "${name}-${category}".hashCode()
                    def aggrRow= sql.firstRow('select * from aggregate id=${id}')
                    
                    if (statValues[name] == null) {
                        statValues[name]= 0
                    }
                    statValues[name]+= value
                    sql.execute(aggregateSql, [aggrId, aggrId, aggrRow.stat, 
                        aggrRow.value + value, aggrId, aggrRow.category])
                }
                
                def updatedValues= []
                statValues.each {name, value ->
                    updatedValues << "${name}=${value}"
                }
                sql.execute(playerSql, [id, id, stat.getSteamID64(), updatedValues.join(","),
                    id, category])
            } else {
                def id= stat.getSteamID64().hashCode()
                def row= sql.firstRow('select * from records where id=${id}')
                def result= stat.getResult()
                
                sql.execute(recordSql, [id, id, stat.getSteamID64(), 
                    result == WIN ? row.wins + 1 : row.wins, 
                    result == LOSS ? row.losses + 1 : row.losses, 
                    result == DISCONNECT ? row.disconnects + 1 : row.disconnects])
            }
        }
        throw new UnsupportedOperationException("Not yet implemented")
    }
}

