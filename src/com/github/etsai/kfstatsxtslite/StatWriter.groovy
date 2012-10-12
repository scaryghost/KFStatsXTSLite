/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite

import groovy.sql.Sql
import com.github.etsai.kfstatsxtslite.message.*

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
        
        sql.execute(difficultySql, [diffId, diffId, diffRow.name, diffId, diffRow.length, 
            stat.getResult() == MatchStat.Result.WIN ? diffRow.wins + 1 : diffRow.wins,
            stat.getResult() == MatchStat.Result.LOSS ? diffRow.losses + 1 : diffRow.losses,
            diffRow.wave + stat.getWave(), new Time(stat.getElapsedTime()).add(diffRow.time)])
        sql.execute(levelSql, [levelId, levelId, levelRow.name, 
            stat.getResult() == MatchStat.Result.WIN ? levelRow.wins + 1 : levelRow.wins,
            stat.getResult() == MatchStat.Result.LOSS ? levelRow.losses + 1: levelRow.losses, 
            new Time(stat.getElapsedTime()).add(levelRow.time)])
    }
    
    public void writePlayerStat(List<PlayerStat> stats) {
        throw new UnsupportedOperationException("Not yet implemented")
    }
}

