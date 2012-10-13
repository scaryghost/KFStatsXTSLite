/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite

import com.github.etsai.kfstatsxtslite.message.PlayerStat

/**
 * Wrapper class for checking completeness of player stats
 * @author etsai
 */
public class PlayerContent {
    private def receivedStats= []
    
    public void addPlayerStat(PlayerStat stat) {
        receivedStats[stat.getSeqNo()]= stat
    }
    
    public boolean isComplete() {
        return receivedStats.last().isClose() && 
            receivedStats.inject(true) {acc, val -> acc && (val != null) }
    }
    
    public Iterable<PlayerStat> getStats() {
        return Collections.unmodifiableList(receivedStats)
    }
}

