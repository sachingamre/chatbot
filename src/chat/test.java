/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chat;

import com.sachin.core.abstracts.Interactivity;
import com.sachin.core.ds.Command;


/**
 *
 * @author sachin.gamre
 */
public class test extends Interactivity {

    public test(Command command) {
        super(command);
    }

    @Override
    public String signature() {
        return "signature";
    }

    @Override
    public String pullData(Command command, String[] args, int page, int totalRecords) {
        String data = super.pullData(command, args, page, totalRecords);
        return data;
    }
}
