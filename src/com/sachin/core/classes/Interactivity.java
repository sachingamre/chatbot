/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.classes;

import com.sachin.app.App;
import com.sachin.core.ds.Command;
import com.sachin.core.interfaces.IDataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author sachin
 */
public class Interactivity implements IDataSource {

    private String _user = null ;
    private String _cPattern = null ;
    private HashMap<String, HashMap> _commandQuestions = new HashMap<String, HashMap>();
    private String doneMessage = null ;

    public Interactivity(Command command) {
        if(!_commandQuestions.containsKey(command.pattern)) {
            List questions = command.questions ;
            int totalQuestions = questions.size() ;
            doneMessage = command.doneMessage ;

            HashMap tmpCmd = new HashMap() ;
            tmpCmd.put("questions", questions) ;
            tmpCmd.put("question_counter", totalQuestions) ;

            _commandQuestions.put(command.pattern, tmpCmd) ;
        }
    }

    /**
     * The function interacts with the user. The very first time when the user
     * sends this command, the arguments to the commands are stored in a list
     * and interactive flag is set to true for that user. Then one by one the
     * questions are asked to the user and the respective answer is stored in the list.
     * Once all the questions are asked. The process will set the interactivity flag to false
     * accumulate all the answers and perform the action set in the command XML.
     * The action could be email/log/process.
     *
     * @param args
     * @param page
     * @param totalRecords
     * @return String
     */
    public String pullData(Command cmd, String[] args, int page, int totalRecords) {
        String data = null ;

        if(_user != null) {
            // check if user session is started for this interactive command
            // String key = _user + "_" + _cPattern ;
            String key = _user ;
            List tmpQ = (List) ((HashMap) _commandQuestions.get(_cPattern)).get("questions")  ;
            int totalQuestions = Integer.parseInt(((HashMap) _commandQuestions.get(_cPattern)).get("question_counter").toString()) ;



            if(!App.COMMAND_INTERACTIVE_SESSION.containsKey(key)) {
                // start interactive session
                HashMap iSession = new HashMap() ;
                iSession.put("answers", new ArrayList()) ;
                iSession.put("qCounter", 1) ;
                iSession.put("command", _cPattern) ;
                iSession.put("arguments", args) ;
                App.COMMAND_INTERACTIVE_SESSION.put(key, iSession) ;
                data = tmpQ.get(0).toString() ;
            }
            else {
                HashMap userISession = (HashMap) App.COMMAND_INTERACTIVE_SESSION.get(key) ;

                int qCounter = Integer.parseInt(userISession.get("qCounter").toString()) ;
                ArrayList answers = (ArrayList) userISession.get("answers") ;
                String[] argumentList = (String[]) userISession.get("arguments") ;

                // add answers
                if(args.length > 0) {
                    answers.add(StringUtils.join(args, ",")) ;
                }

                // check if it is the last answer
                if(qCounter == totalQuestions) {
                    // this is the last answer. User has answered all the questions correctly
                    // log all the answers
                    String argList = userISession.get("arguments").toString() ;
                    String ansList = answers.toString() ;
                    System.out.println("Interactive session over ") ;
                    System.out.println("Initial arguments " + argList) ;
                    System.out.println("Answers " + ansList) ;
                    //App.logger.info("args:" + argList);
                    //App.logger.info("args:" + argList);
                    //App.logger.info("args:" + argList);
                    App.COMMAND_INTERACTIVE_SESSION.remove(key) ;
                    data = doneMessage ;
                }
                else {
                    data = tmpQ.get(qCounter).toString() ;
                    qCounter++ ;
                    HashMap iSession = new HashMap() ;
                    iSession.put("answers", answers) ;
                    iSession.put("qCounter", qCounter) ;
                    iSession.put("command", _cPattern) ;
                    iSession.put("arguments", argumentList) ;
                    App.COMMAND_INTERACTIVE_SESSION.put(key, iSession) ;
                }
            }
        }
        return data ;
    }

    public String pullAds() {
        return "ads" ;
    }

    public Interactivity forUser(String userName) {
        _user = userName ;
        return this ;
    }

    public Interactivity forCPattern(String pattern) {
        _cPattern = pattern ;
        return this ;
    }

}
