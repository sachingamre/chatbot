/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sachin.gamre
 */
public class SacXMLObject {

    private String name = null ;
    private String value = null ;
    private List<SacXMLObject> children = new ArrayList<SacXMLObject>() ;
    private HashMap<String, String> attributes = new HashMap<String, String>() ;

    public void addAttribute(String attr, String value) {
        attributes.put(attr, value) ;
    }

    public void setValue(String val) {
        value = val ;
    }

    public String getValue() {
        return value ;
    }

    public void setName(String nodeName) {
        name = nodeName ;
    }

    public String getName() {
        return name ;
    }

    public HashMap<String, String> getAttributes() {
        return attributes ;
    }

    public void addChild(SacXMLObject child) {
        if(child != null) {
            children.add(child) ;
        }
    }

    public List<SacXMLObject> getChildren() {
        return children ;
    }

}
