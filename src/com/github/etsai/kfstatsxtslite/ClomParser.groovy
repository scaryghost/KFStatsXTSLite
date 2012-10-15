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
        cli.dburl(args:1, argName:'url', required:true, 'url to the remote database')
        cli.dbuser(args:1, argName:'name', 'user name to log into the database')
        cli.dbpassword(args:1, argName:'pwd', 'login password for the database')
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
     * Get the url to the remote database.  This is a required command
     * line option
     * @return Database url
     */
    public String getDbURL() {
        return options.dburl
    }
    /**
     * Get the user name for the database login.  User name is specified by 
     * the -dbuser option.  If no user name is specified, null is returned
     * @return Database user name login or null if none is given
     */
    public String getDbUser() {
        if (options.dbuser)
            return options.dbuser
        return null
    }
    /**
     * Get the password for the dadtabase login.  Password is specified by 
     * the -dbpassword option.  If no password is specified, null is returned
     * @return Database password login or null if none is given
     */
    public String getDbPassword() {
        if (options.dbpassword)
            return options.dbpassword
        return null
    }
}

