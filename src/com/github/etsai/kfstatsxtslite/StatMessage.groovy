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
    
    private def msgType
    private def seqNo
    private def close
    private def stats
    
    public static StatMessage parse(String msg) {
        def parts= msg.tokenize("|")
        def packetInfo= parts[0].tokenize(",")
        def message
        
        switch (packetInfo[0]) {
            case PlayerStat.PROTOCOL:
                if (packetInfo[1].toInteger() != PlayerStat.VERSION)
                    throw new RuntimeException("Player protocol is incorrect version.  Received ${packetInfo[1]}, expecting ${PlayerStat.VERSION}")
                message= new PlayerStat(parts)
                message.msgType= Type.PLAYER
                break;
            case MatchStat.PROTOCOL:
                if (packetInfo[1].toInteger() != MatchStat.VERSION)
                    throw new RuntimeException("Match protocol is incorrect version.  Received ${packetInfo[1]}, expecting ${MatchStat.VERSION}")
                message= new MatchStat(parts)
                message.msgType= Type.MATCH
                break;
            default:
                throw new RuntimeException("Unrecognized message type: ${packetInfo[0]}")
                    
        }
        message.seqNo= parts[2].toInteger()
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
        return Collections.unmodifiableMap(stats)
    }
}

