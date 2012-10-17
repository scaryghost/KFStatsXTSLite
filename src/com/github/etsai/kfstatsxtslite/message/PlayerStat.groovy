/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite.message

import com.github.etsai.kfstatsxtslite.StatMessage

/**
 * Represents a player message
 * @author etsai
 */
public class PlayerStat extends StatMessage {
    static class MatchInfo {
        public enum Result {
            WIN, LOSS, DISCONNECT
        }

        public def level
        public def difficulty
        public def length
        public def wave
        public def result
    }
    
    public static String PROTOCOL= "kfstatsx-player"
    public static Integer VERSION= 1
    public static Long linuxOffset= 76561197960265728
    
    private final def steamID64
    private final def category
    private final def matchInfo
    
    public PlayerStat(def parts) {
        super((parts[3] == "match") ? "" : parts[4])
        
        steamID64= parts[1]
        if (steamID64.length() < 17) {
            steamID64= (steamID64.toLong() + linuxOffset).toString()
        }
        category= parts[3]
        
        if (category == "match") {
            matchInfo= new MatchInfo(level: parts[4].toLowerCase(), difficulty: parts[5], 
                length: parts[6], wave: parts[8].toInteger())

            switch(parts[7].toInteger()) {
                case 0:
                    matchInfo.result= MatchInfo.Result.DISCONNECT
                    break
                case 1:
                    matchInfo.result= MatchInfo.Result.LOSS
                    break
                case 2:
                    matchInfo.result= MatchInfo.Result.WIN
                    break
                default:
                    throw new RuntimeException("Unrecognized result: ${parts[7]}")
            }
        }
    }
    
    public String getSteamID64() {
        return steamID64
    }
    
    public String getCategory() {
        return category
    }
    
    public MatchInfo getMatchInfo() {
        return matchInfo
    }
}

