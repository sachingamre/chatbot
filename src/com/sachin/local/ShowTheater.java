/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.local;

import com.sachin.app.App;
import com.sachin.core.api.db.DbManager;
import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
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
public class ShowTheater implements IDataSource {

    private String _dbKey = null ;

    public ShowTheater() {
        _dbKey = DbManager.init(App.config.getString("dbserver"), App.config.getString("dbname"), App.config.getString("dbuser"), App.config.getString("dbpass"), App.config.getString("dbdriver")) ;
    }

    public String pullData(Command command, String[] args, int page, int totalRecords) {

        String result = "";

        // There are max to max two arguments, hence we are not putting the columns in array
        // We are just implementing if loops for handling function arguments
        String sql = "SELECT * FROM movie_shows " ;
        if(args.length == 2 ) {
            sql += " WHERE theatername like ? and showdate like ?" ;
        }
        else if(args.length == 1) {
            sql += " WHERE theatername like ? " ;
        }
        sql += " LIMIT ?,?" ;
        System.out.println(sql) ;

        // build array list
        List argsList = new ArrayList() ;
        for(int i=0; i < args.length; i++) {
            if(args[i] != null && !args[i].equals("")) {
                System.out.println("Adding a record") ;
                HashMap hmap = new HashMap() ;
                hmap.put("type", "string") ;
                hmap.put("value", "%" + args[i] + "%") ;
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

        try {
            result = DbManager.dumpResultSet(DbManager.getInstance(_dbKey).select(sql, argsList));
        }
        catch (SQLException ex) {
            Logger.getLogger(ShowTheater.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;



    }

    public String pullAds() {
        return "" ;
    }

}
