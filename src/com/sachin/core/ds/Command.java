/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.ds;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sachin
 */
public class Command {
    public int id = 0;
    public String pattern = null ;
    public String description = null ;
    public String usage = null ;
    public String sourceType = null ;
    public String source = null ;
    public String queryFields = null ;
    public String displayFields = null ;
    public String defaultOperater = "AND" ;
    public List questions = new ArrayList() ;
    public String interactive = null ;
    public String doneMessage = null ;


    public Command(int instId, String instName, String instDescription,String instUsage, String instDataSourceType, String instDataSource, String qFields, String dFields, String defOp, List ques, String inter, String doneMsg) {
        id = instId ;
        pattern = instName ;
        description = instDescription ;
        usage=instUsage;
        sourceType = instDataSourceType ;
        source = instDataSource ;
        queryFields = qFields ;
        displayFields = dFields ;
        defaultOperater = defOp ;
        questions = ques ;
        interactive = inter ;
        doneMessage = doneMsg ;
    }
}
