/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.etsai.kfstatsxtslite;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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
        
        clom.parse(args);
        
        byte[] buffer= new byte[65536];
        socket= new DatagramSocket(clom.getPort());
        packet= new DatagramPacket(buffer, buffer.length);
        
        System.out.println("Listening on port: "+clom.getPort());
        while(true) {
            try {
                socket.receive(packet);
                String data= new String(packet.getData());
                
                System.out.println(data);
            } catch (IOException ex) {
                Logger.getLogger(StatListenerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    }
}
