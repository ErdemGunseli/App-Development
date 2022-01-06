package com.example.xmasdraft;


import java.util.ArrayList;

public class QuestionSet {

    /* Each Question Set will have a name, description, image id (so that it can have an image in the main
    * menu), an array of Questions, the index of the current question, and whether it is expanded
    * (in the main menu).
    */

    private int questionSetID;
    private String name, description;
    private int imageId;
    private Question[] questions;
    private int currentQuestionIndex;
    private boolean isExpanded;


    public QuestionSet(int questionSetID, String name, String description, int imageID, Question[] questions) {
        this.questionSetID = questionSetID;
        this.name = name;
        this.description = description;
        this.imageId = imageID;
        this.questions = questions;
        this.currentQuestionIndex = 0;
        this.isExpanded = false;
    }

    // Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // Image ID
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }


    // Questions
    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question[]  questions) {
        this.questions = questions;
    }


    // Is Expanded
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }


    // Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // Question Set ID
    public int getQuestionSetID() {
        return questionSetID;
    }

    public void setQuestionSetID(int questionSetID) {
        this.questionSetID = questionSetID;
    }


    // Current Question Index
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        // To prevent any errors, validate first:
        if (currentQuestionIndex < getQuestions().length) {
            this.currentQuestionIndex = currentQuestionIndex;
        }
    }

    // Length

    public int length(){
        return this.questions.length;
    }
}