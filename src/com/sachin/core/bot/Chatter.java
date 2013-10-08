/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.bot;

import com.sachin.app.App;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

/**
 * Main BOT. The class handles all BOT functionality. It logins to gmail.
 * Connects to roster friends and also keep answering to incoming request.
 *
 * @author sachin
 */
public class Chatter {
    public static XMPPConnection connection ;
    public static int totalFriends = 0 ;
    public static int onlineFriends = 0 ;
    public static Roster roster ;
    final static String domain = "gmail.com" ;

    /**
     * Constructor
     */
    public Chatter() {
        try {

            // Get dbconnection
            //dbKey = DbManager.init("localhost", "chatdb", "chatdb", "chatdb", "mysql") ;

            SASLAuthentication.registerSASLMechanism(GoogleSASLAuth.NAME, GoogleSASLAuth.class);
            SASLAuthentication.supportSASLMechanism(GoogleSASLAuth.NAME, 0);
            ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
            // ConnectionConfiguration config = new ConnectionConfiguration("www.researcherdream.com", 5222, "researcherdream.com");
            // ConnectionConfiguration config = new ConnectionConfiguration("ejabberd.zapak.com", 5222, "ejabberd.zapak.com");

            config.setCompressionEnabled(true);
            config.setSASLAuthenticationEnabled(true);
            config.setSendPresence(true);
            config.setReconnectionAllowed(true);
            config.setDebuggerEnabled(true);

            connection = new XMPPConnection(config);
            connection.connect();
        }
        catch (XMPPException ex) {
            Logger.getLogger(Chatter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Login to gtalk server
     *
     * @param loginname
     * @param password
     */
    public void login(String loginname, String password) {
        try {
            connection.login(loginname, password, domain);
        }
        catch (XMPPException ex) {
            Logger.getLogger(Chatter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set bot status
     *
     * @param message
     */
    public void setStatus(String message) {
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(message);
        connection.sendPacket(presence);
    }

    /**
     * Manager friends and friends request
     * Sets counter for total friends and online friends
     *
     */
    public void manageFriends() {
        roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all) ;
        totalFriends = entries.size() ;

        roster.addRosterListener(new RosterListener() {
            public void entriesAdded(Collection<String> addresses) {
                for(String friend:addresses) {
                    // Log new entry
                    App.logger.info("new^" + friend) ;
                    totalFriends++ ;
                }
            }
            public void entriesDeleted(Collection<String> addresses) {}
            public void entriesUpdated(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                if(presence.isAvailable()) {
                    onlineFriends++ ;
                }
            }
        });
    }

    private void _updateFriendsCountToDb() {

    }

    /*
    public void connectToFriends() {
        roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all) ;
        totalFriends = entries.size() ;

        roster.addRosterListener(new RosterListener() {
            public void entriesAdded(Collection<String> addresses) {
                for(String friend:addresses) {
                    // System.out.println(friend) ;
                    App.logger.info("new^" + friend) ;
                    // Chat chat = connection.getChatManager().createChat(friend, new ChatHandler(friend)) ;
                }
            }
            public void entriesDeleted(Collection<String> addresses) {}
            public void entriesUpdated(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                if(presence.isAvailable()) {
                    onlineFriends++ ;
                }
                // System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
                // Chat chat = connection.getChatManager().createChat(presence.getFrom(), new ChatHandler(presence.getFrom())) ;
            }
        });

        System.out.println("\n\n Total Buddy(ies): " + totalFriends) ;

        //for(RosterEntry r:entries)
        //{
        //    try {
         //       System.out.println("Connecting to " + r.getUser());
                // Chat chat = connection.getChatManager().createChat(r.getUser(), new ChatHandler(r.getUser())) ;
                //Chat chat = connection.getChatManager().createChat(r.getUser(), (MessageListener) new ChatHandlerPacketListener()) ;
        //    }
        //    catch(Exception ex) {
        //        Logger.getLogger(Chatter.class.getName()).log(Level.SEVERE, null, ex);
         //   }
       // }

    }

    public void manageRequests() {
        roster.addRosterListener(new RosterListener() {
            public void entriesAdded(Collection<String> addresses) {
                for(String friend:addresses) {
                    System.out.println(friend);
                    Chat chat = connection.getChatManager().createChat(friend, new ChatHandler(friend)) ;
                }
            }
            public void entriesDeleted(Collection<String> addresses) {}
            public void entriesUpdated(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                if(presence.isAvailable()) {
                    onlineFriends++ ;
                }
                System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
                Chat chat = connection.getChatManager().createChat(presence.getFrom(), new ChatHandler(presence.getFrom())) ;
            }
        });
    }
    */

    /**
     * We have implemented ChatListener because Message listener was not working properly
     * And ChatListener is suitable for Listening to incoming messages
     */
    public void addChatListener() {
        connection.getChatManager().addChatListener(new ChatManagerListener(){

            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                System.out.println("Created locally ---------------------------------------" + createdLocally);
                if (!createdLocally) {
                    chat.addMessageListener(new ChatHandler(chat.getParticipant()));
                }
            }
        });
    }

    /**
     * Logout from the server
     */
    public void logout() {
        connection.disconnect() ;
    }

    /**
     * Rotate by shifting a character right at a time.
     */
    private static String rotate(String input) {
        return input.substring(1) + input.charAt(0);
    }


    public void  scrollingStatus() throws InterruptedException {

        // The text for the status
        String status = "I \u2665 my work ";

        // Construct the presence object
        Presence presence = new Presence(Presence.Type.available, status, 24, Presence.Mode.available);

        // Send 500 packets
        for (int i = 0; i < 500; i++) {
            // Rotate left by a character
            status = rotate(status);

            // Set the status into presence object
            presence.setStatus(status);

            // Send it
            connection.sendPacket(presence);
        }
    }
}
