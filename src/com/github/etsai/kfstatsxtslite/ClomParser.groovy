/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.etsai.kfstatsxtslite

/**
 * Parses command line options
 * @author etsai
 */
public class ClomParser {
    private def options;
    private def cli;
    
    /**
     * Default constructor
     */
    public ClomParser() {
        cli= new CliBuilder(usage: './startlistener [options]')
        cli.dbname(args:1, argName:'path', required:true, 'sqlitedb to use')
        cli.port(args:1, argName:'no', 'udp port to listen for stat packets')
        cli.h(longOpt:'help', 'displays this help message')
    }
    
    /**
     * Parse the command line arguments
     */
    public void parse(String[] args) {
        options= cli.parse(args)
        if (options == null) {
            System.exit(1)
        }
        if (options.h) {
            cli.usage()
            System.exit(0)
        }
    }
    /**
     * Get the udp port to use.  If -port was not used, return default value 
     * of 6000.
     * @return  UDP port or 6000 if none was specified
     */
    public Integer getPort() {
        if (options.port) {
            return options.port;
        }
        return 6000
    }
    /**
     * Get the database name given by -dbname.  This is a required command 
     * line option
     * @return Database name
     */
    public String getDBName() {
        return options.dbname
    }
}

