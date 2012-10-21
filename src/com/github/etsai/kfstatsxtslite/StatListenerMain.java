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
import java.util.Calendar;
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
    private static StatWriter writer;
    private static long contentTimeout= 60000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ClomParser clom= new ClomParser(args);
        
        try {
            if (clom.getLogging()) {
                try {
                    logWriter= TeeLogger.getFileWriter("kfstatsxtslite");
                    System.setOut(new PrintStream(new TeeLogger(logWriter, System.out), true));
                    System.setErr(new PrintStream(new TeeLogger(logWriter, System.err), true));
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                    System.err.println("Cannot create log file to store output");
                }
            }
        
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("Server shutting down: " + Calendar.getInstance().getTime());
                }
            });

            writer= new StatWriter(Sql.newInstance(clom.getDbURL(), clom.getDbUser(), clom.getDbPassword()));
            System.out.println("Server started: " + Calendar.getInstance().getTime());
            System.out.println("Logging: " + clom.getLogging());

            startServer(new DatagramSocket(clom.getPort()), clom.getServerPassword(), clom.getVerbose());
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            System.err.println("Error connecting to the MySql database");
            System.exit(2);
        } catch (SocketException ex) {
            System.err.println(ex.getMessage());
            System.err.println("Error starting server on port: " + clom.getPort());
            System.exit(3);
        }

    }

    public static void startServer(DatagramSocket socket, String password, boolean verbose) throws SocketException {
        byte[] buffer= new byte[65536];
        DatagramPacket packet= new DatagramPacket(buffer, buffer.length);
        Timer timer= new Timer();

        System.out.println("Verbose mode: " + verbose);
        System.out.println("Listening on port: " + socket.getLocalPort());
        while(true) {
            try {
                socket.receive(packet);
                StatMessage msg= StatMessage.parse(new String(packet.getData(), 0, packet.getLength()), password);
                
                if (verbose) {
                    System.out.println("Received stat message: " + msg);
                }
                if (msg instanceof MatchStat) {
                    writer.writeMatchStat((MatchStat)msg);
                } else if (msg instanceof PlayerStat) {
                    PlayerStat playerMsg= (PlayerStat)msg;
                    String steamID64= playerMsg.getSteamID64();
                    PlayerContent content;

                    if (steamID64 == null) {
                        writer.writeBlankPlayerStat(playerMsg);
                    } else {
                        synchronized(receivedContent) {
                            if (!receivedContent.containsKey(steamID64)) {
                                receivedContent.put(steamID64, new PlayerContent());
                                timer.schedule(new ContentRemover(steamID64), contentTimeout);
                            }
                            content= receivedContent.get(steamID64);
                        }
                        content.addPlayerStat(playerMsg);
                        if (content.isComplete()) {
                            synchronized(receivedContent) {
                                receivedContent.remove(steamID64);
                            }
                            System.out.println("Saving stats for: " + steamID64);
                            writer.writePlayerStat(content.getStats());
                        }
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
                    System.out.println(String.format("Removing player content for steamID64: %s, not completed within %dms", 
                        steamID64, contentTimeout));
                    receivedContent.remove(steamID64);
                }
            }
        }
    }
}
