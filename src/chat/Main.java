/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chat;

import com.sachin.app.App;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sachin.gamre
 */
public class Main {

    /**
     * App startup method
     * The method calls init method to do bootstrap the application
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Initialize the application environment
            // Read configuration and start the chatter box
            App.init();

            // We have implemented this because we want to exit the application by typing exit
            // Read exit from command prompt to halt the application.
            String msg = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while ((msg = br.readLine()).length() > 0) {
                if (msg.equalsIgnoreCase("exit")) {
                    App.close();
                } else if (msg.equalsIgnoreCase("reload")) {
                    App.reloadConfig();
                }
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
