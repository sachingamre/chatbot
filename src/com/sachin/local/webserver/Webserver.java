/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.local.webserver;

import com.sachin.core.interfaces.IDataSource;
import com.sachin.core.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author saching
 */
public class Webserver implements IDataSource {

    protected static String SSH_BIN = "/usr/bin/ssh ";
    protected static String SSH_USER = "glmstudio";
    protected static String APACHE_BIN = "/usr/bin/sudo /home/glmstudio/dev/apache/bin/httpd";

    protected enum SupportedCommand {
        restartweb, stopweb, startweb
    }

    public String pullData(String command, String[] servers, int page, int totalRecords) {
        String message = "";
        System.out.println(command);
        SupportedCommand scmd = SupportedCommand.valueOf(command);

        if(servers.length == 0) { return "no args"; }

        switch(scmd) {
            case restartweb:   // Run apache restart server through SSH
                                boolean restarted = this.restartWebServer(Utils.replace("([^\\s]+)\\s*", "$1", servers[0]));
                                if(restarted) {
                                    message = "Apache on " + servers[0] + " restarted successfully";
                                } else {
                                    message = "Apache on " + servers[0] + " failed to restart";
                                }
                                break;

            case stopweb:   // Stop apache server through SSH
                                boolean stopped = this.stopWebServer(Utils.replace("([^\\s]+)\\s*", "$1", servers[0]));
                                if(stopped) {
                                    message = "Apache on " + servers[0] + " restarted successfully";
                                } else {
                                    message = "Apache on " + servers[0] + " failed to restart";
                                }
                                break;

            case startweb:  // Start apache server through SSH
                                boolean started = this.startWebServer(Utils.replace("([^\\s]+)\\s*", "$1", servers[0]));
                                if(started) {
                                    message = "Apache on " + servers[0] + " restarted successfully";
                                } else {
                                    message = "Apache on " + servers[0] + " failed to restart";
                                }
                                break;
        }
        return message;
    }

    protected boolean restartWebServer(String server) {
        String restartCommand = SSH_BIN + " " + SSH_USER + "@" + server + " " + APACHE_BIN + " -k restart";
        System.out.println(restartCommand);
        HashMap result = Utils.system(restartCommand);
        if(!((ArrayList) result.get("error")).isEmpty()) {
            return false;
        }
        return true;
    }

    protected boolean startWebServer(String server) {
        String startCommand = SSH_BIN + " " + SSH_USER + "@" + server + " " + APACHE_BIN + " -k start";
        System.out.println(startCommand);
        HashMap result = Utils.system(startCommand);
        if(!((ArrayList) result.get("error")).isEmpty()) {
            return false;
        }
        return true;
    }

    protected boolean stopWebServer(String server) {
        String stopCommand = SSH_BIN + " " + SSH_USER + "@" + server + " " + APACHE_BIN + " -k stop";
        System.out.println(stopCommand);
        HashMap result = Utils.system(stopCommand);
        if(!((ArrayList) result.get("error")).isEmpty()) {
            return false;
        }
        return true;
    }

    public String pullAds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
