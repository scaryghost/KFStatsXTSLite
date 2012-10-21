/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite

import com.github.etsai.kfstatsxtslite.message.PlayerStat
import com.github.etsai.kfstatsxtslite.message.MatchStat

/**
 * Interprets the messages received from the mutator
 * @author etsai
 */
public abstract class StatMessage {
    public enum Type {
        MATCH, PLAYER
    }
    
    protected def msgType
    protected def seqNo
    protected def close
    protected def stats
    
    public static StatMessage parse(String msg, String password) {
        def parts= msg.split("\\|")
        def packetInfo= parts[0].split(",")
        def message
        
        if (packetInfo[2] != password) {
            throw new RuntimeException("Invalid password given, ignoring packet: ${msg}")
        }
        switch (packetInfo[0]) {
            case PlayerStat.PROTOCOL:
                if (packetInfo[1].toInteger() != PlayerStat.VERSION)
                    throw new RuntimeException("Player protocol is incorrect version.  Received ${packetInfo[1]}, expecting ${PlayerStat.VERSION}")
                message= new PlayerStat(parts)
                message.msgType= Type.PLAYER
                message.seqNo= parts[2].toInteger()
                break;
            case MatchStat.PROTOCOL:
                if (packetInfo[1].toInteger() != MatchStat.VERSION)
                    throw new RuntimeException("Match protocol is incorrect version.  Received ${packetInfo[1]}, expecting ${MatchStat.VERSION}")
                message= new MatchStat(parts)
                message.msgType= Type.MATCH
                message.seqNo= 0
                break;
            default:
                throw new RuntimeException("Unrecognized message type: ${packetInfo[0]}")
                    
        }
        message.close= parts.last() == "_close"
        return message
    }
    
    public StatMessage(def statStr) {
        stats= [:]
        statStr.tokenize(",").each {keyval ->
            def keyvalSplit= keyval.tokenize("=")
            stats[keyvalSplit[0]]= keyvalSplit[1].toInteger()
        }
    }
    
    public Type getType() {
        return msgType
    }
    
    public int getSeqNo() {
        return seqNo
    }
    public boolean isClose() {
        return close
    }
    
    public Map<String, Integer> getStats() {
        return stats
    }
}

