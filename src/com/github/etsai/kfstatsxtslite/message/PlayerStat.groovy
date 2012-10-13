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
    public enum Result {
        WIN, LOSS, DISCONNECT
    }
    
    public static String PROTOCOL= "kfstatsx-player"
    public static Integer VERSION= 1
    
    private final def steamID64
    private final def category
    private final def result
    
    public PlayerStat(def parts) {
        super((parts[3] == "match") ? "" : parts[4])
        
        steamID64= parts[1]
        category= parts[3]
        
        if (category == "match") {
            switch(parts[7].toInteger()) {
                case 0:
                    result= Result.DISCONNECT
                    break
                case 1:
                    result= Result.LOSS
                    break
                case 2:
                    result= Result.WIN
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
    
    public Result getResult() {
        return result
    }
}

