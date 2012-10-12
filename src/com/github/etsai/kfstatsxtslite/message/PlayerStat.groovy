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
    public static String PROTOCOL= "kfstatsx-player"
    public static Integer VERSION= 1
    
    private final def steamID64
    private final def group
    
    public PlayerStat(def parts) {
        super(parts[4])
        
        steamID64 parts[1]
        group= partrs[3]
        
    }
    
    public String getSteamID64() {
        return steamID64
    }
    
    public String getGroup() {
        return group
    }
}

