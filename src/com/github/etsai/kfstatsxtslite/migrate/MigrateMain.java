/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.etsai.kfstatsxtslite.migrate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main entry point for the migration from SQLite to MYSQL
 * @author etsai
 */
public class MigrateMain {
    private static Pattern timePat= Pattern.compile("(\\d+) days (\\d{2}):(\\d{2}):(\\d{2})");
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection src= DriverManager.getConnection("jdcb:sqlite:kfstatsx.sqlite");
        Connection dst= DriverManager.getConnection("jdbc:mysql://192.168.1.121:3306/kfstatsx", 
                "kfstatsx", "Ch0coc4t");
        
        move(src.createStatement(), dst.createStatement());
    }
    
    static long timeStrToSeconds(String timeStr) {
        Matcher matcher= timePat.matcher(timeStr);
        return Integer.valueOf(matcher.group(4)) + Integer.valueOf(matcher.group(3)) * 60 + 
                    Integer.valueOf(matcher.group(2)) * 3600 + Integer.valueOf(matcher.group(1)) * 86400;
    }
    static void move(Statement srcSt, Statement dstSt) throws SQLException {
        ResultSet rs;
        
        rs= srcSt.executeQuery("select * from deaths");
        while(rs.next()) {
            dstSt.executeUpdate(String.format("insert into deaths values(NULL, '%s', %d);", 
                    rs.getString("name"), rs.getInt("count")));
        }
        rs.close();
        
        rs= srcSt.executeQuery("select * from records");
        while(rs.next()) {
            dstSt.executeUpdate(String.format("insert into records values (NULL, '%s', %d, %d, %d);", 
                    rs.getString("steamid"), rs.getInt("wins"), rs.getInt("losses"), rs.getInt("disconnects")));
        }
        rs.close();
        
        rs= srcSt.executeQuery("select * from aggregate");
        while(rs.next()) {
            dstSt.executeUpdate(String.format("insert into aggregate values (NULL, '%s', %d, '%s');", 
                    rs.getString("stat"), rs.getInt("value"), rs.getString("category")));
        }
        rs.close();
        
        rs= srcSt.executeQuery("select * from difficulties");
        while(rs.next()) {          
            dstSt.executeUpdate(String.format("insert into difficulties values (NULL, '%s', '%s', %d, %d, %d, %d);", 
                    rs.getString("name"), rs.getString("length"), rs.getInt("wins"), rs.getInt("losses"), 
                    rs.getInt("wave"), timeStrToSeconds(rs.getString("time"))));
        }
        rs.close();
        
        rs= srcSt.executeQuery("select * from levels");
        while(rs.next()) {
            dstSt.executeUpdate(String.format("insert into levels values (NULL, '%s', %d, %d, %d);", 
                    rs.getString("name"), rs.getInt("wins"), rs.getInt("losses"), timeStrToSeconds(rs.getString("time"))));
        }
        rs.close();
        
        rs= srcSt.executeQuery("select * from player");
        while(rs.next()) {
            for(String statValue: rs.getString("stats").split(",")) {
                String[] split= statValue.split("=");
                dstSt.executeUpdate(String.format("insert into player values (NULL, '%s', '%s', %d, '%s');", 
                        rs.getString("steamid"), split[0], Integer.valueOf(split[1]), rs.getString("category")));
            }
        }
        rs.close();
    }
}
