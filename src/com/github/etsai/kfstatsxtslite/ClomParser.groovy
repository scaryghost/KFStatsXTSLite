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
    
    /**
     * Constructs object given the list of command lin arguments
     * @param   args    Command line arguments
     */
    public ClomParser(String[] args) {
        def cli= new CliBuilder(usage: 'java -jar KFStatsXTSLite.jar [options]')
        cli.dburl(args:1, argName:'url', required:true, 'url to the remote database')
        cli.dbuser(args:1, argName:'name', 'user name to log into the database')
        cli.dbpwd(args:1, argName:'password', 'login password for the database')
        cli.port(args:1, argName:'no', 'udp port to listen for stat packets')
        cli.h(longOpt:'help', 'displays this help message')
        cli._(longOpt:'version', 'prints the version and exits')
        cli.pwd(args:1, argName:'password', required:true, 'password that udp packets must have to be accepted by the server')
        cli.log('enable logging')
        cli.v('Verbose mode')

        if (args.contains("--version")) {
            println "KFStatsXTSLite - Version: ${Version.gitTag}"
            System.exit(0)
        } else if (args.contains("-h") || args.contains("--help")) {
            cli.usage()
            System.exit(0)
        }
        
        options= cli.parse(args)
        if (options == null) {
            System.exit(1)
        }
    }

    /**
     * Get the udp port to use.  If -port was not used, return default value 
     * of 6000.
     * @return  UDP port or 6000 if none was specified
     */
    public Integer getPort() {
        if (options.port) {
            return options.port.toInteger();
        }
        return 6000
    }
    /**
     * Get the server password, specified by the -pwd option.  This is a required option
     * @return Server passwprd
     */
    public String getServerPassword() {
        return options.pwd
    }
    /**
     * Get the logging state, specified by the -log option.
     * @return True if logging is enabled
     */
    public boolean getLogging() {
        return options.log
    }
    /**
     * Get verbose mode, specificed by -v options
     * @return True if verbose mode is set
     */
    public boolean getVerbose() {
        return options.v
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
        if (options.dbpwd)
            return options.dbpwd
        return null
    }
}

