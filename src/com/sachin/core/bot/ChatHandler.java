/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.bot;

import com.sachin.app.App;
import com.sachin.core.abstracts.Interactivity;
import com.sachin.core.api.cli.CliHandler;
import com.sachin.core.api.db.DbHandler;
import com.sachin.core.api.http.HttpHandler;
import com.sachin.core.api.solr.SolrHandler;
import com.sachin.core.ds.Command;
import com.sachin.core.ds.User;
import com.sachin.core.interfaces.IDataSource;
import com.sachin.core.utils.Utils;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * @author sachin
 */
public class ChatHandler implements MessageListener {

    private User _user = null ;
    private boolean handShakeInProcess = false;
    public static enum RESERVED_COMMANDS {NEXT, PREV} ;

    public ChatHandler(String username) {
        System.out.println("===============================User name " + username);
        _user = App.USERS.get(username) ;
    }

    /**
     * Function to handle incoming chat messages
     * Parses the chat message, extracts the action/command to perform
     * calls corresponding action performer. Passes the arguments passed to command
     * to the action performer.
     *
     * @param chat
     * @param message
     */
    public void processMessage(Chat chat, Message message) {
        System.out.println(message + " " + message.getBody());
        System.out.println(chat.getParticipant() + " " + message.getType() + " " + message.getBody()) ;

        try {
            // Proceed only if the message type is Chat
            if (message != null && message.getBody() != null && message.getType() == Message.Type.chat) {
                String messageBody = message.getBody().trim();
                int page = 0 ;                              // default pager value

                // Check if message is from any of the reserved keywords ie if command pager is used
                HashMap keyWordPresent = _checkKeyword(chat.getParticipant(), messageBody);
                if(keyWordPresent.get("match").equals("yes")) {
                    page = Integer.parseInt(keyWordPresent.get("page").toString()) ;
                    messageBody = (String) keyWordPresent.get("command") ;
                }

                // save the command in history
                System.out.println("Message body is " + messageBody) ;
                System.out.println("Page is " + page) ;

                if(_isAuthorized(messageBody)) {
                    if(messageBody != null && !messageBody.equals("") && _isAllowed(messageBody)) {
                        String msgCmd = null ;
                        String msgArgs = null ;

                        // check if interactive session is on for the _user //then command typed must be answer to question
                        if(App.COMMAND_INTERACTIVE_SESSION.containsKey(_user.loginName)) {
                            msgCmd = ((HashMap) App.COMMAND_INTERACTIVE_SESSION.get(_user.loginName)).get("command").toString() ;
                            msgArgs = messageBody ;
                        }
                        else {
                            // Extract the command and its arguments from chat message
                            msgCmd = Utils.replace("^([^\\s]+)(.*)", "$1", messageBody) ;

                            // The regular expression will match
                            // any damn non space character first, then it will match space only
                            // and then the characters/arguments given to the command
                            // eg: find genre,action
                            msgArgs = Utils.replace("^([^\\s]+)([^\\S]+)(.+)", "$3", messageBody) ; //suman
                        }
                        String[] messageParts = msgArgs.split(",") ;

                        // Proceed if message body is not empty some command is thr
                        if(messageParts.length > 0) {

                            // Refer the commands set for action match
                            Iterator it = App.COMMANDS.keySet().iterator() ;
                            HashMap tempMap = null ;
                            boolean matchFound = false ;

                            // Loop through App commands and see the given chat messages
                            // matches to which command
                            while(it.hasNext()) {
                                String key = (String) it.next() ;
                                App.AllowedSourceTypes stype ;

                                //command given by the user is found . now fetch the command profiles for execution
                                if(Utils.match(key, msgCmd)) {
                                    System.out.println("command " + msgCmd + " matched to key " + key) ;
                                    try {
                                        matchFound = true ;
                                        tempMap = (HashMap) App.COMMANDS.get(key);
                                        // messageParts = Arrays.copyOfRange(messageParts, 1, messageParts.length) ;

                                        // Since we are storing the data source type and data source in the hash map
                                        // in App.COMMANDs. We will check the data source type for the mapped key
                                        // and then pull off the data source object to pull data
                                        //stype = Enum.valueOf(App.AllowedSourceTypes,key);
                                        stype = App.AllowedSourceTypes.valueOf(((String) tempMap.get("type")).toUpperCase());
                                        switch (stype) {
                                            case CLASS:

                                                IDataSource idx = (IDataSource) tempMap.get("instance");
                                                String output = idx.pullData((Command) tempMap.get("commandInstance"), messageParts, page, App.PAGE_RECORDS);
                                                chat.sendMessage(output);
                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody);
                                                App.COMMAND_PAGER.put(_user.loginName, page);

                                                //chat.sendMessage(_getMessage(output, chat.getThreadID()));
                                                break;

                                            case HTTP:


                                                HttpHandler httpHandler = (HttpHandler) tempMap.get("instance") ;
                                                System.out.println("Fetching http content") ;
                                                String response = httpHandler.pullData((Command) tempMap.get("commandInstance"), messageParts, page, App.PAGE_RECORDS) ;
                                                chat.sendMessage(response);
                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody) ;
                                                App.COMMAND_PAGER.put(_user.loginName, page) ;
                                                //chat.sendMessage(_getMessage(response, chat.getThreadID()));
                                                break;

                                           case CLI:


                                                CliHandler cliHandler = (CliHandler) tempMap.get("instance") ;
                                                System.out.println("Fetching http content") ;
                                                String cmdOutput = cliHandler.pullData((Command) tempMap.get("commandInstance"), messageParts, page, App.PAGE_RECORDS) ;
                                                chat.sendMessage(cmdOutput);
                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody) ;
                                                App.COMMAND_PAGER.put(_user.loginName, page) ;
                                                //chat.sendMessage(_getMessage(response, chat.getThreadID()));
                                                break;

                                            case WS:
                                                // create WEB SERVICE client and fetch the data
                                                break;

                                            case SQL:
                                                // Query the database to fetch the data
                                                DbHandler dbHandler = (DbHandler) tempMap.get("instance") ;
                                                System.out.println("Fetching Sql content") ;
                                                String resultset = dbHandler.pullData((Command) tempMap.get("commandInstance"), messageParts, page, App.PAGE_RECORDS) ;
                                                chat.sendMessage(resultset);
                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody) ;
                                                App.COMMAND_PAGER.put(_user.loginName, page) ;
                                                //chat.sendMessage(_getMessage(resultset, chat.getThreadID()));
                                                break;

                                            case SOLR:
                                                // pull data from solr
                                                SolrHandler solrHandler = (SolrHandler) tempMap.get("instance") ;
                                                System.out.println("Fetching solr content") ;
                                                String solrData = solrHandler.pullData((Command) tempMap.get("commandInstance"), messageParts, page, App.PAGE_RECORDS) ;
                                                System.out.println("Sending data") ;
                                                chat.sendMessage(solrData);
                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody) ;
                                                App.COMMAND_PAGER.put(_user.loginName, page) ;
                                                //chat.sendMessage(_getMessage(solrData, chat.getThreadID()));
                                                break;

                                            case INTERACTIVE:
                                                Interactivity interactivity = (Interactivity) tempMap.get("instance") ;
                                                System.out.println("Doing interactive session") ;
                                                String iaData = interactivity.forCPattern(msgCmd).forUser(_user.loginName).pullData((Command) tempMap.get("commandInstance"), messageParts, page, App.PAGE_RECORDS) ;
                                                System.out.println("Sending data") ;
                                                chat.sendMessage(iaData);

                                                 String adsData = interactivity.forCPattern(msgCmd).forUser(_user.loginName).pullAds() ;
                                                System.out.println("-----------------Sending ads--------") ;
                                                chat.sendMessage(adsData);

                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody) ;
                                                App.COMMAND_PAGER.put(_user.loginName, page) ;
                                                break ;

                                            case STRING:
                                                chat.sendMessage((String) tempMap.get("instance"));
                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody) ;
                                                App.COMMAND_PAGER.put(_user.loginName, page) ;

                                                //chat.sendMessage(_getMessage((String) tempMap.get("instance"), chat.getThreadID()));
                                                break;

                                            case DOWNLOAD:
                                                //to check if evertime a new instance needs to b created to same
                                                //file handler needs to b used for diff simulataneous user
                                                CustomFileHandler fileObj =null;
                                                        fileObj=(CustomFileHandler) tempMap.get("instance") ;;

                                                String filename = "filename.txt" ;
                                                System.out.print("Download option is selecetd for file :"+filename);

                                                fileObj.fileTransfer(filename, chat.getParticipant());
                                                System.out.print("file transfer happened");
                                                chat.sendMessage(fileObj.pullData(messageParts, page, App.PAGE_RECORDS));  //fake arguments
                                                App.COMMAND_HISTORY.put(_user.loginName, messageBody) ;
                                                App.COMMAND_PAGER.put(_user.loginName, page) ;
                                                break ;

                                            default:
                                                chat.sendMessage("Type 'help' for more information.");
                                                // chat.sendMessage(_getMessage("Type 'help' for more information.", chat.getThreadID()));
                                                break ;
                                        }
                                        App.logger.info(chat.getParticipant()+"^"+message.getType()+"^"+message.getBody()) ;
                                        break;
                                    }
                                    catch (XMPPException ex) {
                                        App.logger.error(ex.getMessage());
                                        // Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    catch(Exception ex) {
                                        ex.printStackTrace();
                                        App.logger.error(ex.getMessage());
                                        // Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
                                        chat.sendMessage("Command usage is not correct");
                                    }
                                }
                                else{
                                    System.out.println("Matched failed for " + key + " " + messageBody) ;
                                }
                            }

                            // return default message, if no matching message is sent by client
                            if(!matchFound) {
                                try {
                                    App.logger.info(chat.getParticipant()+"^"+message.getType()+"^"+message.getBody()) ;
                                    chat.sendMessage("Type 'help' for more information.");
                                }
                                catch (XMPPException ex) {
                                    // Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    App.logger.error(ex.getMessage());
                                }
                            }
                        }
                        else {
                            App.logger.info(chat.getParticipant()+"^"+message.getType()+"^"+message.getBody()) ;
                            chat.sendMessage("Type 'help' for more information.");
                        }
                    }
                    else {
                        App.logger.info(chat.getParticipant()+"^"+message.getType()+"^"+message.getBody()) ;
                        // Show custom help message. message of allowed commands.
                        chat.sendMessage("Type 'help' for more information.");
                    }
                }
                else {
                    App.logger.info(chat.getParticipant()+"^"+message.getType()+"^"+message.getBody()) ;
                    chat.sendMessage("Invalid session: please enter your password");
                }
            }
            else if(message.getType() == Message.Type.normal) {
                App.logger.info(chat.getParticipant()+"^"+message.getType()+"^"+message.getBody()) ;
                // System.out.println(chat.getParticipant() + " normal chat says: " + message.getBody());
            }

        }
        catch(Exception ex) {
            App.logger.error(ex.getMessage());
            //Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Message _getMessage(String data, String threadId) {
        Message newMessage = new Message();
        newMessage.setBody(data);
        newMessage.setThread(threadId);
        return newMessage ;
    }

    private boolean _isAuthorized(String password) {
        System.out.println("User auth enabled " + App.ENABLE_USER_AUTH);
        if(App.ENABLE_USER_AUTH) {
            System.out.println("User object " + App.AUTHORIZED_USERS.get(_user.loginName));
            if(App.AUTHORIZED_USERS.get(_user.loginName) != null) {
                // Get current time in seconds
                int seconds = ((int) (new Date()).getTime()/1000);

                // get session expire time
                int sessionExpiryTime = App.AUTHORIZED_USERS.get(_user.loginName);

                if(sessionExpiryTime > seconds) {

                    // update session ExpiryTime
                    seconds = seconds + App.sessionTimeout;
                    App.AUTHORIZED_USERS.put(_user.loginName, seconds);
                    return true;
                }
            } else {
                if(handShakeInProcess) {
                    // Check if password given is valid
                    //User user = App.USERS.get(_user);
                    if(_user.password.equalsIgnoreCase(password)) {
                        // Add a session cookie and its expiry time
                        int seconds = ((int) (new Date()).getTime()/1000) + App.sessionTimeout;
                        App.AUTHORIZED_USERS.put(_user.loginName, seconds);
                        handShakeInProcess = false;
                        return true;
                    }
                } else {
                    handShakeInProcess = true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Method checks if user has access permission for given command.
     *
     * @param String command
     * @return boolean
     */
    private boolean _isAllowed(String command) {
        // Check if ACL is enabled
        if(App.ENABLE_USER_ACL) {

            // Small hack to check if interactive session is going on.
            if(App.COMMAND_INTERACTIVE_SESSION.containsKey(_user.loginName)) {
                return true;
            } else {
                User user = App.USERS.get(_user.loginName);
                System.out.println("Allow all " + user.allowAll);
                if(!user.allowAll) {
                    List commands = user.commands;
                    return commands.contains(command);
                }
            }
        }
        return true;
    }

    /**
     * The function check for reserved keywords in the command passed to the BOT
     * Returns HashMap
     *
     * @param String participant
     * @param String command
     * @return HashMap result
     */
    private HashMap _checkKeyword(String participant, String command) {
        HashMap result = new HashMap() ;

        try {
            //RESERVED_COMMANDS rcmd = RESERVED_COMMANDS.valueOf(command.toUpperCase()) ;
            String lastCommand = (String) App.COMMAND_HISTORY.get(participant) ;
            int page = 0 ;
            String match = "no" ;

            //switch(rcmd) {
                // Check if next is given
            if(command.toLowerCase().equals("next")) {
                match = "yes" ;
                if(App.COMMAND_PAGER.containsKey(participant)) {
                    page = Integer.parseInt(App.COMMAND_PAGER.get(participant).toString()) ;
                    page = page + App.PAGE_RECORDS ;
                }
            }
            else if(command.toLowerCase().equals("prev")) {
                match = "yes" ;
                if(App.COMMAND_PAGER.containsKey(participant)) {
                    page = Integer.parseInt(App.COMMAND_PAGER.get(participant).toString()) ;
                    page-- ;
                    if(page <= 0) {
                        page = 0 ;
                    }
                }
            }

            // Form resultant hash map
            result.put("match", match) ;
            result.put("command", lastCommand) ;
            result.put("page", page) ;
        }
        catch(IllegalArgumentException ex) {
            App.logger.error(ex.getMessage());
            //Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
            result.put("match", "no") ;
        }
        catch(Exception ex) {
            App.logger.error(ex.getMessage());
            //Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
            result.put("match", "no") ;
        }
        return result ;
    }
}
