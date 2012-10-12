/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite.message

import com.github.etsai.kfstatsxtslite.StatMessage

/**
 * Represents a match message
 * @author etsai
 */
public class MatchStat extends StatMessage {
    public static String PROTOCOL= "kstatsx-match"
    public static Integer VERSION= 1
    
    public enum Result {
        WIN, LOSS
    }
    
    private final def mapName
    private final def difficulty
    private final def length
    private final def elapsedTime
    private final def result
    private final def wave
    
    public MatchStat(def parts) {
        super(parts[7])
        
        mapName= parts[1].toLowerCase()
        difficulty= parts[2]
        length= parts[3]
        elapsedTime= parts[4].toInteger()
        wave= parts[6].toInteger()
        
        switch (parts[5]) {
            case "1":
                result= LOSS
                break
            case "2":
                result= WIN
                break
            default:
                throw new RuntimeException("Unrecognized result value: ${parts[5]}")
        }
    }
    
    public String getMapName() {
        return mapName
    }
    
    public String getDifficulty() {
        return difficulty
    }
    
    public String getLength() {
        return length
    }
    
    public int getElapsedTime() {
        return elapsedTime
    }
    
    public Result getResult() {
        return result
    }
    
    public int getWave() {
        return wave
    }
}

