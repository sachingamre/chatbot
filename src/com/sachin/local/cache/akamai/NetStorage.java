/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.local.cache.akamai;

import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import com.sachin.core.utils.Utils;
import java.io.File;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author saching
 */
public class NetStorage implements IDataSource {

    public static String AKAMAI_USER = "sshacs";
    public static String AKAMAI_SERVER_NAME = "gproperties.upload.akamai.com";
    public static String AKAMAI_BASE_PATH = "/174175/glamcache/glamstatic/";
    public static String AKAMAI_LINK_BASE_PATH = "/174175/glamcache/";

    public static String STATIC_CACHE_BASE_PATH = "/home/glmstudio/conf/static/";

    public static String RSYNC_BIN = "/usr/bin/rsync";
    public static String RSYNC_FLAG = " -azlr --stats ";

    private enum SupportedCommand {
        nssync, nspurge, akpurge
    }



    /**
     * Reads the domain/URL passed in the argument and sync the
     * static content to net storage.
     *
     * @param domains
     * @param page
     * @param totalRecords
     * @return
     */
    public String pullData(Command command, String[] domains, int page, int totalRecords) {
        domains = Utils.clean(domains);
        Utils.printArray(domains);
        System.out.println(command);
        System.out.println("Args length " + domains.length);
        if(domains.length == 0) { return "no args"; }

        SupportedCommand scmd = SupportedCommand.valueOf(command.pattern);

        switch(scmd) {
            case nssync:    this.nsSync(Utils.replace("([^\\s]+)\\s*", "$1", domains[0]));
                            break;
        }

        return StringUtils.join(domains);

    }

    private String nsSync(String url) {
        File domainDir = new File(STATIC_CACHE_BASE_PATH + "/" + url);
        if(domainDir.isDirectory()) {
            String rsyncCommand = RSYNC_BIN + " " + RSYNC_FLAG + " " + STATIC_CACHE_BASE_PATH + "/" +
                              url + "/ " + AKAMAI_USER + "@" + AKAMAI_SERVER_NAME + ":" + AKAMAI_BASE_PATH + "/" + url + "/";
            System.out.println(rsyncCommand);
        }
        else {
            System.out.println("No static cache directory present " + STATIC_CACHE_BASE_PATH + "/" + url);
        }
        //HashMap systemOut = Utils.system(rsyncCommand);
        //System.out.println(systemOut.get("output"));
        //System.out.println(systemOut.get("error"));
        return "";
    }


    public String pullAds() {
        return "";
    }

}
