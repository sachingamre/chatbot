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
public class ShowDetails implements IDataSource {

    public String pullData(Command command, String[] args, int page, int totalRecords) {

        // we expect user arguments
        String sql = "SELECT * FROM movie_shows WHERE mvname = ?" ;


        return "" ;
    }

    public String pullAds() {
        return "" ;
    }

}
