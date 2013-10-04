/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.local;

import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;

/**
 *
 * @author sachin
 */
public class ShowMovie implements IDataSource {

    public String pullData(Command command, String[] args, int page, int totalRecords) {

        // we expect user arguments
        String sql = "SELECT * FROM movie_shows " ;
        String concat = " WHERE " ;
        if(args.length > 0) {
            //for()
        }

        return "" ;
    }

    public String pullAds() {
        return "" ;
    }

}
