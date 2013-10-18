/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.core.classes;

import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;

/**
 *
 * @author saching
 */
public class UserAuth implements IDataSource  {

    public String pullData(Command command, String[] args, int page, int totalRecords) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String pullAds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
