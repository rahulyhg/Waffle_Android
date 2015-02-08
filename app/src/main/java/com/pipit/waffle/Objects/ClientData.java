package com.pipit.waffle.Objects;

import android.content.Context;
import android.util.Log;

import com.pipit.waffle.Network;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Eric on 12/14/2014.
 */
public class ClientData {
    //Singleton class to hold all data

    public static final int MAXIMUM_QUEUED_QUESTIONS = 5;

    private static ClientData clientdata = new ClientData();
    private static Queue<Question> questions;
    private static List<String> idsOfAnsweredQuestions;

    private ClientData(){
        //Initialize
        questions = new LinkedList<Question>();
        idsOfAnsweredQuestions = new ArrayList<String>();
        Log.d("ClientData", "ClientData() - initializing questions arrays");
    }

    public static ClientData getInstance(){
        if (clientdata == null){
            clientdata = new ClientData();
        }
        return clientdata;
    }

    public static void addQuestion(Question q){
        /*
            When a question gets added, it contains a url to the associated image, but not the image itself. We have to
            start the synchronous download of that image here.
         */
        //Todo: Check that question contains at least one option.

        questions.add(q);
        Log.d("ClientData", "addQuestion(q) added a question with text: '" + q.getQuestionBody() + "' and " + q.getChoices().get(0).getQuestionID() + " userID  - question array size is now" + Integer.toString(questions.size()));
        return;
    }

    public static void setAllQuestions(){
        return;
    }

    public static Question getNextUnansweredQuestion(Context mcontext){

        Log.d("ClientData", "getNextUnAnsweredQuestion: questions.size() = " +Integer.toString(questions.size()));
        Question q = new Question("", Self.getUser());
        Choice a = new Choice();
        a.setAnswerBody("developer dun goofed");
        q.addChoice(a);
        q.addChoice(a);

        if (questions.size()<1){
            Network.getAllQuestions(mcontext, numberQuestionsToPull());
        }

        Log.d("ClientData", "getNextUnAnsweredQuestion: questions.size() = " +Integer.toString(questions.size()));

//        Random rand = new Random();
        if (questions.size() > 0){
            //TODO: WHAT WILL WE DO WHEN WE STILL HAVE NO QUESTIONS READY!? WE ARE FUCKED? or will getAllQuestions block via Ion? We'll see
            //TODO: Implement some way of randomizing questions
//            int randomNum = rand.nextInt(questions.size());
//            q = questions.get(randomNum);
//            questions.remove(randomNum);

            q = questions.remove();
            idsOfAnsweredQuestions.add(q.getId());
            Log.d("ClientData", "Retrieved a question with body" + q.getQuestionBody());
        }


        return q;
    }

    public static int numberQuestionsToPull(){
        int spaceInQueue = MAXIMUM_QUEUED_QUESTIONS - questions.size();
        if (spaceInQueue<0){
            return 0;
        }
        else return spaceInQueue;
    }

    public static List<String> getIdsOfAnsweredQuestions() {
        return idsOfAnsweredQuestions;
    }

}
