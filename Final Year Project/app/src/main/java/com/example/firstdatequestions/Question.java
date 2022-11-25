package com.example.firstdatequestions;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

    private String name;
    private String question;
    private String answer;
    private String option1;
    private String option2;
    private String option3;
    private String type;
    private int number;

    private List<Question> questionnaire;

    public Question(String name, String type, String question, String answer) {
        this.name = name;
        this.type = type;
        this.question = question;
        this.answer = answer;
    }

    public Question(){

    }

    public Question(List<Question> questionnaire){
        this.questionnaire = questionnaire;
    }

    public Question(String name, String type, String question, String answer, String option1, String option2, String option3) {
        this.name = name;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
    }

    public Question(String name, String type, String question, String answer, String option1, String option2) {
        this.name = name;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.option1 = option1;
        this.option2 = option2;
    }


    public Question(String name, String type, String question, String option1, String option2) {
        this.name = name;
        this.type = type;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
    }

    public Question(String name, String type, String question) {
        this.name = name;
        this.type = type;
        this.question = question;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public List<Question> getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(List<Question> questionnaire) {
        this.questionnaire = questionnaire;
    }


}
