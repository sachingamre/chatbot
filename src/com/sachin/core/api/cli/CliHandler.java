/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.core.api.cli;

import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import com.sachin.core.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author saching
 */
public class CliHandler implements IDataSource {

    public String pullData(Command command, String[] args, int page, int totalRecords) {
        String cliCommand = command.source;
        if(command.autoReplaceArguments) {
            int charOccurenceCount = cliCommand.split("\\?", -1).length-1;
            for(int i = 0; i < args.length && i < charOccurenceCount; i++) {
                cliCommand = cliCommand.replaceFirst("\\?", args[i]);
            }
        }
         HashMap result = Utils.system(cliCommand);
        if(!((ArrayList) result.get("error")).isEmpty()) {
            return "Failure";
        }
        return "Success";
    }

    public String pullAds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
