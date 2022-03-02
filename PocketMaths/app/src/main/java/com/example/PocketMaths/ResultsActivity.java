package com.example.PocketMaths;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.PocketMaths.QuestionSetActivity.QUESTION_SET_ID;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imgExit;

    private TextView txtQuestionSetName, txtResult, txtFirstAttempt, txtSecondAttempt, txtResultPercentage, txtFailed;

    private QuestionSet questionSet;

    private RecyclerView rvQuestions;

    private Button btnFinish;

    private PieChart pieResults;


    private ArrayList<String> failedTopics = new ArrayList<>();
    private  ArrayList<String> failedModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialising View Objects:
        initViews();

        // Setting data from the intent:
        setDataFromIntent();

        ResultsRecyclerAdapter resultsRecyclerAdapter = new ResultsRecyclerAdapter(this);
        resultsRecyclerAdapter.setQuestionSet(questionSet);

        // Setting adapter to recycler view:
        rvQuestions.setAdapter(resultsRecyclerAdapter);

        rvQuestions.setLayoutManager(new LinearLayoutManager(this));



    }

    private void setDataFromIntent() {
        // Getting Question Set from the Intent:
        Intent intent = getIntent();

        // If the intent is not null:
        if (intent != null) {

            // Get the extra data from the Intent:
            // If the value is set to null, it will default to -1:
            int questionSetID = intent.getIntExtra(QUESTION_SET_ID, -1);

            if (questionSetID != -1) {
                // Finding our Question Set by ID:
                questionSet = Utils.getInstance().getQuestionSetByID(questionSetID);

                if (questionSet != null) {

                    // Setting the data from the Question Set to our View item:
                    setData(questionSet);

                    // Saving the Question Set to the account's list of Question Sets Completed:
                    saveQuestionSet();
                }
            }

        }
    }


    private void saveQuestionSet(){
        // Adding the question set to the account's list of completed question sets:

        Calendar calendar = Calendar.getInstance();


        Utils.getInstance().getUserAccount().addQuestionSet(new QuestionSetResult(
                questionSet.getName(),
                questionSet.calculatePointsEarned(),
                questionSet.calculatePointsPossible(),
                questionSet.calculateResult(),
                questionSet.calculateNumberOfQuestionsSolved()[0],
                questionSet.calculateNumberOfQuestionsSolved()[1],
                questionSet.getQuestions().length,
                DateFormat.getDateInstance().format(Calendar.getInstance().getTime())
        ));
    }


    private void initViews() {
        rvQuestions = findViewById(R.id.rvQuestions);
        imgExit = findViewById(R.id.imgExit);
        txtQuestionSetName = findViewById(R.id.txtQuestionSetName);
        txtResult = findViewById(R.id.txtResult);

        txtFirstAttempt = findViewById(R.id.txtFirstAttempt);
        txtSecondAttempt = findViewById(R.id.txtSecondAttempt);

        txtResultPercentage = findViewById(R.id.txtResultPercentage);
        txtFailed = findViewById(R.id.txtFailed);

        btnFinish = findViewById(R.id.btnFinish);

        pieResults = findViewById(R.id.pieResults);

        imgExit.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

    }

    private void setData(QuestionSet questionSet) {

        txtQuestionSetName.setText(questionSet.getName());

        // X Points out of X
        txtResult.setText(String.format(getString(R.string.x_points_out_of_x), questionSet.calculatePointsEarned(), questionSet.calculatePointsPossible()));

        // Solved X out of X with one attempt.
        txtFirstAttempt.setText(String.format(getString(R.string.solved_x_out_of_x_1attempt),questionSet.calculateNumberOfQuestionsSolved()[0], questionSet.getQuestions().length ));

        // Calculating the remaining questions:
        int remaining = (questionSet.getQuestions().length - questionSet.calculateNumberOfQuestionsSolved()[0]);

        // Only present if score isn't 100%
        if (remaining > 0) {
            txtSecondAttempt.setVisibility(View.VISIBLE);

           // Solved X out of the remaining X with two attempts.
            txtSecondAttempt.setText(String.format(getString(R.string.solved_x_out_of_x_2attempts),  questionSet.calculateNumberOfQuestionsSolved()[1], (remaining) ));
        } else {
            // If 100%, we don't need it:
            txtSecondAttempt.setVisibility(View.GONE);
        }

        // Result: X%
        txtResultPercentage.setText(String.format(getString(R.string.result_percent), questionSet.calculateResult()));


        // Calculating the topics and models that the student failed.
        calculateFailed();


        // Adding the failed topics and question types:

        if (this.failedTopics.size() > 0){
            // Practice the following topics:
            txtFailed.append(getString(R.string.practice_topics));
        }
        int count = 1;
        for (String topic: this.failedTopics) {
            if (topic != null){
                txtFailed.append("\t" + count + ") " + topic + "\n");
                count += 1;
            }
        }

        if (this.failedModels.size() > 0){
            // Practice the following question types:
            txtFailed.append(getString(R.string.practice_models));
        }
        count = 1;
        for (String model: this.failedModels){
            if (model != null) {
                txtFailed.append("\t" + count + ") " + model + "\n");
                count += 1;
            }
        }

        // Creating values for the pie chart:
        int firstAttempt = questionSet.calculateNumberOfQuestionsSolved()[0];
        int secondAttempt = questionSet.calculateNumberOfQuestionsSolved()[1];
        int moreAttempts = questionSet.getQuestions().length - (questionSet.calculateNumberOfQuestionsSolved()[0] + questionSet.calculateNumberOfQuestionsSolved()[1]);

        int[] values = {firstAttempt, secondAttempt, moreAttempts};
        String[] labels = {"1st Attempt", "2nd Attempt", "3rd Attempt"};

        // Creating the Pie Chart
        Utils.getInstance().createPieChart(this, pieResults,
                values,
                13,
                "",
                0,
                labels,
                13,
                R.color.Secondary,
                getString(R.string.results),
                16,
                R.color.Primary,
                R.color.Surface1);


    }


    @Override
    public void onClick(View view) {
        int v = view.getId();

        if (v == R.id.imgExit || v == R.id.btnFinish) {
            questionSet.reset();
            startActivity(new Intent(this, MainMenuActivity.class));
        }

    }

    private void calculateFailed() {

        for (Question question : questionSet.getQuestions()) {
            String topic = question.getTopic();
            String model = question.getModel();

            // If answered incorrectly, add the topic/model to arraylist.
           if (question.getPointsPossible() != question.getPointsEarned()){
               if (!this.failedTopics.contains(topic)){
                   this.failedTopics.add(topic);
               }
               if (!this.failedModels.contains(model)){
                   this.failedModels.add(model);
               }
           }
        }

    }
}