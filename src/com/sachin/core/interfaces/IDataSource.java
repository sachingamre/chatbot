/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.interfaces;

import com.sachin.core.ds.Command;
import java.io.Serializable;

/**
 *
 * @author sachin
 */
public interface IDataSource extends Serializable {

    /**
     *
     * The function pulls data from the data source and returns it back to the chat handler.
     *
     * @param String[] args arguments passed to BOT command
     * @param int page page number
     * @param int totalRecords total number of records to show
     *
     * @return String
     */
    public String pullData(Command command, String args[], int page, int totalRecords) ;

    /**
     * Pulls ad from adserver and returns it back to chat handler.
     *
     * @return
     */
    public String pullAds() ;
}
