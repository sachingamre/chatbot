/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.core.ds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saching
 */
public class User implements Serializable {
    public String loginName = null;
    public String password = null;
    public List commands = new ArrayList();
    public boolean allowAll = true;
    public String help = null;

    public User(String instLoginName, String instPassword, List instCommands) {
        loginName = instLoginName;
        password = instPassword;
        commands = instCommands;
        System.out.println("Username " + loginName + " commands count " + commands.size());
        if(commands.size() > 0) {
            allowAll = false;
            help = "This is an online bot. Please type following commands to interact with it.\n";
            for(int i = 0; i < commands.size(); i++) {
                help += commands.get(i) + "\n";
            }
        }
    }
}
