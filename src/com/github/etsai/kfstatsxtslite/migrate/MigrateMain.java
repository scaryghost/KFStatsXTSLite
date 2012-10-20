/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.etsai.kfstatsxtslite.migrate;

import com.github.etsai.utils.Time;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Main entry point for the migration from SQLite to MYSQL
 * @author etsai
 */
public class MigrateMain {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if (args.length != 4) {
            System.err.println("Usage: migrate [sqlite url] [mysql url] [mysql user name] [mysql password]");
            System.exit(1);
        }
        
        Class.forName("org.sqlite.JDBC");
        Connection src= DriverManager.getConnection(args[0]);
        Connection dst= DriverManager.getConnection(args[1], args[2], args[3]);
        
        move(src.createStatement(), dst.createStatement());
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
            Time time= new Time(rs.getString("time"));
            dstSt.executeUpdate(String.format("insert into difficulties values (NULL, '%s', '%s', %d, %d, %d, %d);", 
                    rs.getString("name"), rs.getString("length"), rs.getInt("wins"), rs.getInt("losses"), 
                    rs.getInt("wave"), time.toSeconds()));
        }
        rs.close();
        
        rs= srcSt.executeQuery("select * from levels");
        while(rs.next()) {
            Time time= new Time(rs.getString("time"));
            dstSt.executeUpdate(String.format("insert into levels values (NULL, '%s', %d, %d, %d);", 
                    rs.getString("name"), rs.getInt("wins"), rs.getInt("losses"), time.toSeconds()));
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