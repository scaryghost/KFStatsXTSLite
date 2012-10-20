/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.etsai.kfstatsxtslite;

import com.github.etsai.kfstatsxtslite.message.*;
import com.github.etsai.utils.logging.TeeLogger;
import groovy.sql.Sql;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main entry point for the UDP listener
 * @author etsai
 */
public class StatListenerMain {
    private static final Map<String, PlayerContent> receivedContent= new HashMap<>();
    private static FileWriter logWriter;
    private static long contentTimeout= 60000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException, SQLException {
        ClomParser clom= new ClomParser();
        
        clom.parse(args);
        if (clom.getLogging()) {
            try {
                logWriter= TeeLogger.getFileWriter("kfstatsxtslite");
                System.setOut(new PrintStream(new TeeLogger(logWriter, System.out), true));
                System.setErr(new PrintStream(new TeeLogger(logWriter, System.err), true));
                System.out.println("Logging enabled");
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                System.err.println("Cannot create log file to store output");
            }
        } else {
            System.out.println("Logging disabled");
        }
        
        StatWriter writer= new StatWriter(Sql.newInstance(clom.getDbURL(), clom.getDbUser(), clom.getDbPassword()));
        byte[] buffer= new byte[65536];
        DatagramSocket socket= new DatagramSocket(clom.getPort());
        DatagramPacket packet= new DatagramPacket(buffer, buffer.length);
        Timer timer= new Timer();
        
        System.out.println("Listening on port: "+clom.getPort());
        while(true) {
            try {
                socket.receive(packet);
                StatMessage msg= StatMessage.parse(new String(packet.getData(), 0, packet.getLength()));
                
                if (msg instanceof MatchStat) {
                    writer.writeMatchStat((MatchStat)msg);
                } else if (msg instanceof PlayerStat) {
                    PlayerStat playerMsg= (PlayerStat)msg;
                    String steamID64= playerMsg.getSteamID64();
                    PlayerContent content;

                    if (!receivedContent.containsKey(steamID64)) {
                        receivedContent.put(steamID64, new PlayerContent());
                    }
                    content= receivedContent.get(steamID64);
                    content.addPlayerStat(playerMsg);
                    if (content.isComplete()) {
                        System.out.println("Saving stats for: " + steamID64);
                        writer.writePlayerStat(content.getStats());
                        receivedContent.remove(steamID64);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    static class ContentRemover extends TimerTask {
        private String steamID64;

        public ContentRemover(String steamID64) {
            this.steamID64= steamID64;
        }

        @Override
        public void run() {
            synchronized(receivedContent) {
                if (receivedContent.containsKey(steamID64)) {
                    System.out.println(String.format("Player content for steamID64: %s not completed within %dms.  Removing", 
                        steamID64, contentTimeout));
                    receivedContent.remove(steamID64);
                }
            }
        }
    }
}
