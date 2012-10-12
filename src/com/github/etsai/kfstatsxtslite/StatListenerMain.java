/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.etsai.kfstatsxtslite;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point for the UDP listener
 * @author etsai
 */
public class StatListenerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException {
        ClomParser clom= new ClomParser();
        DatagramSocket socket;
        DatagramPacket packet;
        ExecutorService pool= Executors.newCachedThreadPool();
        
        clom.parse(args);
        
        byte[] buffer= new byte[65536];
        socket= new DatagramSocket(clom.getPort());
        packet= new DatagramPacket(buffer, buffer.length);
        
        System.out.println("Listening on port: "+clom.getPort());
        while(true) {
            try {
                socket.receive(packet);
                StatMessage msg= StatMessage.parse(new String(packet.getData()));
                
            } catch (IOException ex) {
                Logger.getLogger(StatListenerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
