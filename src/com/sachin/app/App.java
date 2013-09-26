/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.app;

import com.hazelcast.core.Hazelcast;
import com.sachin.core.api.db.DbHandler;
import com.sachin.core.api.http.HttpHandler;
import com.sachin.core.api.solr.SolrHandler;
import com.sachin.core.bot.Chatter;
import com.sachin.core.bot.CustomFileHandler;
import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import com.sachin.core.loaders.CommandLoader;
import com.sachin.core.loaders.ConfigLoader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author sachin
 */
public class App {

    public static boolean ENABLE_USER_AUTH = false;

    public static boolean ENABLE_USER_ACL = false;

    // We are using log4j for logging. So getting instance of log4j logger
    public static Logger logger = Logger.getLogger("com");

    // Map stores Precooked (instantiated) datasources for each command
    // The Map stores the class instances against each command pattern
    public static Map<String, Object> COMMANDS = Hazelcast.getMap("commands");

    // Stores users command history. As of now store last command fired by the user
    //history - it keeps user last sent command
    public static Map<String, Object> COMMAND_HISTORY = Hazelcast.getMap("commands_history");

    // Pager for each user
    public static Map<String, Integer> COMMAND_PAGER = Hazelcast.getMap("commands_pager");

    // Interactive command session for each user
    // Hazel cast is used to make it distributed app . A user can get interactive question from any of the machine.
    public static Map<String, HashMap> COMMAND_INTERACTIVE_SESSION = Hazelcast.getMap("commands_interactive_session");

    // Resource bundle to store login details for the bot to login
    public static ResourceBundle config = ResourceBundle.getBundle("com.sachin.config.credentials");

    // Number of records to be show on each search
    public static int PAGE_RECORDS = 1;

    // Source type supported
    public static enum AllowedSourceTypes {
        CLASS, HTTP, WS, SQL, SOLR, STRING, INTERACTIVE, DOWNLOAD
    };
    // Instance of the gmail BOT
    public static Chatter chatter = null;

    /**
     * Init method reads XML file for commands. Parses it and creates respective data source
     * object and stores it in the Hazelcast Map.
     * Logins to the gtalk BOT with the set login name and password. Assign ChatHandler object to each
     * roaster value
     *
     */
    public static void init() {
        try {

            // Read config XML file and load it.
            ConfigLoader configLoader = new ConfigLoader("commands.xml");

            // Get Bot credentials
            HashMap<String, String> credentials = configLoader.getCredentials();
            System.out.println("Loginname " + credentials.get("loginname"));
            System.out.println("Password " + credentials.get("password"));

            // Load Commands list
            List commandList = configLoader.getCommands();

            // Load Users
            List userList = configLoader.getUsers();

            // Check if user auth is enabled.
            ENABLE_USER_AUTH = configLoader.isAuthEnabled();

            // Check if user acl is enabled.
            ENABLE_USER_ACL = configLoader.isAclEnabled();

            // Read number of records to be shown on each page.
            PAGE_RECORDS = Integer.parseInt(App.config.getString("pagerecords"));
            System.exit(0);
            //Starting chatter
            // Login to the gmail through BOT
            chatter = new Chatter();
            chatter.login(credentials.get("loginname"), credentials.get("password"));
            chatter.setStatus("Available");
            chatter.manageFriends();
            chatter.addChatListener();

            //loading commands

            //CommandLoader commandLoader = new CommandLoader("commands.xml");
            //CommandLoader commandLoader = new CommandLoader(App.config.getString("commandslist"));


            // loop through the list and create data source object instances for each source class
            Iterator it = commandList.iterator();
            AllowedSourceTypes stype;
            HashMap <String,Object>tempMap;

            // Loop till there is no command left to process
            while (it.hasNext()) {
                try {
                    Command cmd = (Command) it.next();
                    stype = AllowedSourceTypes.valueOf(cmd.sourceType.toUpperCase());
                    switch (stype) {
                        /**
                         * CLASS source type assumes that the data will be loaded from a java class
                         * Which will be present in com.sachin.local. Class in the com.sachin.local
                         * should implement the IDataSource inteface
                         */
                        case CLASS:

                            // Load the instance of the class mentioned in the datasource
                            System.out.println("pattern " + cmd.pattern);
                            System.out.println("source " + cmd.source);
                            Class c = ClassLoader.getSystemClassLoader().loadClass(cmd.source);
                            //Class c = CustomClassLoader.getInstance().loadClass(cmd.source);
                            IDataSource ids = (IDataSource) c.newInstance();
                            tempMap = new HashMap();
                            tempMap.put("type", cmd.sourceType);
                            tempMap.put("instance", ids);
                            tempMap.put("usage", cmd.usage);
                            COMMANDS.put(cmd.pattern, tempMap);

                            break;


                        /**
                         * HTTP source type assumes that the data will be loaded from the http URL.
                         */
                        case HTTP:
                            // load the data from HTTP URL
                            System.out.println("pattern " + cmd.pattern);
                            System.out.println("source " + cmd.source);
                            HttpHandler httpHandler = new HttpHandler(cmd.source);
                            tempMap = new HashMap();
                            tempMap.put("type", cmd.sourceType);
                            tempMap.put("instance", httpHandler);
                            tempMap.put("usage", cmd.usage);
                            COMMANDS.put(cmd.pattern, tempMap);
                            break;

                        /**
                         * WS source type assumes that the data will be loaded from a web server.
                         * The web server should hold a method named pullData.
                         */
                        case WS:
                            // create WEB SERVICE client and fetch the data
                            break;

                        /**
                         * SQL source type assumes that the data will be loaded from a database server.
                         */
                        case SQL:
                            // Query the database to fetch the data
                            System.out.println("pattern " + cmd.pattern);
                            System.out.println("source " + cmd.source);
                            DbHandler dbHandler = new DbHandler(cmd.source);
                            tempMap = new HashMap();
                            tempMap.put("type", cmd.sourceType);
                            tempMap.put("instance", dbHandler);
                            tempMap.put("usage", cmd.usage);
                            COMMANDS.put(cmd.pattern, tempMap);
                            break;

                        /**
                         * SOLR source type assumes that the data will be loaded from a solr server.
                         */
                        case SOLR:
                            // pull data from solr
                            // load the data from HTTP URL
                            System.out.println("pattern " + cmd.pattern);
                            System.out.println("source " + cmd.source);
                            SolrHandler solrHandler = new SolrHandler(cmd.source, cmd.queryFields, cmd.displayFields, cmd.defaultOperater);
                            System.out.println(solrHandler);
                            tempMap = new HashMap();
                            tempMap.put("type", cmd.sourceType);
                            tempMap.put("instance", solrHandler);
                            tempMap.put("usage", cmd.usage);
                            COMMANDS.put(cmd.pattern, tempMap);
                            break;

                        /**
                         * Interactive source type assumes that you need to ask some questions to the user
                         * and get some feedback from the user too.
                         *
                         */
                        case INTERACTIVE:
                            // pull data from solr
                            // load the data from HTTP URL
                            System.out.println("pattern " + cmd.pattern);
                            System.out.println("source " + cmd.source);

                            //Class d = CustomClassLoader.getInstance().loadClass(cmd.source);
                            //loading of class
                            Class d = ClassLoader.getSystemClassLoader().loadClass(cmd.source);

                            //passing arguments to constructor
                            Class[] ctorArgs1 = new Class[1];
                            ctorArgs1[0] = Command.class;
                            Constructor cc = d.getConstructor(ctorArgs1);

                            IDataSource mqInteractive = (IDataSource) cc.newInstance(cmd); // d.newInstance() ;
                            // IDataSource mqInteractive = (IDataSource) d.newInstance() ;
                            //Interactivity interactivity = new Interactivity(cmd);
                            // System.out.println(interactivity) ;
                            tempMap = new HashMap();
                            tempMap.put("type", cmd.sourceType);
                            tempMap.put("instance", mqInteractive);
                            tempMap.put("usage", cmd.usage);
                            COMMANDS.put(cmd.pattern, tempMap);
                            break;

                        /**
                         * STRING source type assumes that the data will be loaded from a normal string.
                         */
                        case STRING:
                            // pull data from String
                            tempMap = new HashMap();
                            tempMap.put("type", cmd.sourceType);
                            tempMap.put("instance", cmd.source);
                            tempMap.put("usage", cmd.usage);
                            COMMANDS.put(cmd.pattern, tempMap);
                            break;

                        case DOWNLOAD:
                            System.out.println("pattern " + cmd.pattern);
                            CustomFileHandler fileObj =new CustomFileHandler();
                            System.out.println("source " + cmd.source);

                            tempMap = new HashMap();
                            tempMap.put("type", cmd.sourceType);
                            System.out.println("putting in instance");
                            tempMap.put("instance", fileObj);
                            tempMap.put("usage", cmd.usage);
                            //tempMap.put("instance", cmd.source);
                            System.out.println("bfore entering into command");
                           // System.out.println(fileObj.) ;

                            try{
                               COMMANDS.put(cmd.pattern, tempMap);
                            }
                            catch(Exception ex) {
                                System.out.print("error is :"+ex.getMessage());
                            }

                            System.out.println("after entering into command");
                            break;

                    }
                }
                catch (InstantiationException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.FATAL, null, ex);
                }
                catch (IllegalAccessException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.FATAL, null, ex);
                }
                catch (Exception ex) {
                    Logger.getLogger(App.class.getName()).log(Level.FATAL, null, ex);
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.FATAL, null, ex);
        }
    }

    /**
     * Closes the application
     */
    public static void close() {
        chatter.logout();
        System.exit(0);
    }
}
