package com.example.firstdatequestions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
/*
this class just shows other profile's answers to a user's questionnaire
 */
public class ViewAnswers extends AppCompatActivity {

    private ImageButton goBack;
    private TextView name, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answers);

        name = findViewById(R.id.textView8);
        text = findViewById(R.id.displayAnswers);
        goBack = findViewById(R.id.imageButton);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();

        List<Question> questionaire = Utils.getQuestions("MYQUESTIONS"+userID,getApplicationContext());//used to get the user's questionnaire from the file it is saved in
        String username = getIntent().getStringExtra("Name");//gets the name of the user who answered the questionnaire as it is passed through an intent from the previous activity
        ArrayList<String> listofAnswers = getIntent().getStringArrayListExtra("Answers");// the list of answers is passed through an intent from the previous activity

        String displayText = "";



        for (int i=0;  i<questionaire.size(); i++){
            Question question = questionaire.get(i);
            String currQuestion = question.getQuestion();
            String currAnswer = listofAnswers.get(i);
            displayText += currQuestion+" Answer: "+currAnswer+"\n";


        }
        name.setText(username);
        text.setText(displayText);

        /*
        using this button sends user back to the Dashboard Activity
         */
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DashBoardActivity.class));
            }
        });


    }
}