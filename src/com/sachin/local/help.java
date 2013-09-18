/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.local;

import com.sachin.app.App;
import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import java.util.HashMap;

/**
 *
 * @author suman.holani
 *
 *
 * 
 * Surround your text with the modifiers to style the string. 
 *      1. Asterisks will make it bold, 
 *      2. underscores will make it italicized, 
 *      3. dashes will make it strikethrough.
 *
 */
public class help implements IDataSource {

    static String helpString = "";

    //String output = idx.pullData(messageParts, page, App.PAGE_RECORDS) ;
    public String pullData(String command, String[] args, int page, int totalRecords) {
        //look for subpattern if it contains command name then give its description
        String helpstr = "";

        System.out.println(" Got arguments " + args.toString() + "   " + args.length);
        //System.exit(1);

        if (!(args[0].trim().equals("")) && (args.length >= 1)  ) {
            //if arguments are passed ie command name is coming in help
            //System.out.println("argument Value = " + args[0]);
            String pattern = args[0].toString();
            //System.out.println("command is =>" + pattern);
            //Command obj = () App.COMMANDS.get(pattern);
            // App.COMMANDS.
            HashMap<String, Object> cmd = (HashMap<String, Object>) App.COMMANDS.get(pattern);
            //System.out.println("Value = " + cmd.get("usage").toString());
            helpstr = cmd.get("usage").toString().concat("\n");
            //helpstr=obj.usage.toString();

        } else if (helpString.length() < 1) {
            //System.out.println("Helpstring.length()  >" + helpString.length());
            //Iterate thru command map and populate help string
            for (Object value : App.COMMANDS.values()) {
                HashMap<String, Object> cmd = (HashMap<String, Object>) value;
               // System.out.println("Value = " + cmd.get("usage"));
                helpstr = helpstr.concat(cmd.get("usage").toString()).concat("\n");
                helpString = helpstr;
            }
        } else {
            helpstr = helpString;
        }
        return helpstr;


    }

    public String pullAds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
