/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.api.db;

import com.sachin.app.App;
import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import com.sachin.core.utils.Utils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sachin
 */
public class DbHandler implements IDataSource {

    private String _sql = null ;
    private String _dbKey = null ;

    public DbHandler(String sql) {

        // Set db credentials
        _sql = sql + " LIMIT ?,?" ;
        _dbKey = DbManager.init(App.config.getString("dbserver"), App.config.getString("dbname"), App.config.getString("dbuser"), App.config.getString("dbpass"), App.config.getString("dbdriver")) ;
    }

    public String pullData(Command command, String[] args, int page, int totalRecords) {
        String result = "";
        Utils.printArray(args);

        System.out.println("Page " + page) ;
        System.out.println("Total Records " + totalRecords) ;

        List argsList = new ArrayList() ;
        for(int i=0; i < args.length; i++) {
            if(args[i] != null && !args[i].equals("")) {
                System.out.println("Adding a record") ;
                HashMap hmap = new HashMap() ;
                hmap.put("type", "string") ;
                hmap.put("value", args[i]) ;
                argsList.add(hmap);
            }
        }

        // add pagination values also
        HashMap hmap = new HashMap() ;
        hmap.put("type", "int") ;
        hmap.put("value", page) ;
        argsList.add(hmap) ;

        hmap = new HashMap() ;
        hmap.put("type", "int") ;
        hmap.put("value", totalRecords) ;
        argsList.add(hmap) ;

        Utils.printList(argsList, "Arg list -- ") ;

        System.out.println("Sql is " + _sql) ;
        try {
            result = DbManager.dumpResultSet(DbManager.getInstance(_dbKey).select(_sql, argsList));
        }
        catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String pullAds() {
        return "" ;
    }
}
