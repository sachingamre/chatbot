/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachin.local;

import com.sachin.core.abstracts.Interactivity;
import com.sachin.core.ds.Command;
import java.util.List;

/**
 *
 * @author suman.holani
 */
public class Quiz extends Interactivity {

    //diff quiz  will hav diff
    // public static int level=1; //number fo questions
    // public static int max_question=10;
    private static Quiz mqInstance = null;

    //constructor of movie quiz called by runtime polymorphism on the basis of command source found
    public Quiz(Command command) {
        super(command);
        System.out.println("After MovieQuiz constructor");
    }

    public static Quiz getInstance(Command command) {
        if (mqInstance == null) {
            mqInstance = new Quiz(command);
        }
        return mqInstance;
    }

    public int getLevel() {
        return 1;
    }

    public void setNumber(int questionCount) {
    }

    public int getNumber(int level) {
        return 1;
    }

    public void pushAnswers(String resultstore) {
        //rest api call push data
    }

    protected List getQuestions(int level, int number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String signature() {
        return "";
    }

    public String pullData(String command, String[] args, int page, int totalRecords) {
        return "";
    }
}
