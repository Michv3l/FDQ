package com.example.firstdatequestions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This activity controls the entire process of answering a questionnaire
Original code can be found at https://codinginflow.com/tutorials/android/quiz-app-with-sqlite/part-5-quiz-activity
 */
public class AnswerActivity extends AppCompatActivity {

    private TextView textViewQuestion, textViewQuestionCount, username;
    private ImageView userPhoto;
    private EditText textAnswer;
    private RadioGroup rbGroup;
    private RadioButton rb1, rb2, rb3, rb4;
    private Button buttonConfirmNext;
    private List<Question> questionnaire;
    private int questionCounter, questionCountTotal;
    private ColorStateList textColorDefaultRb;
    private Question newQuestion;
    private String displayName;
    List<String> answers;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        context = getApplicationContext();
        questionnaire = getCurrentQuestion();
        answers = new ArrayList<>();
        displayName = Utils.getName(context);
        username = findViewById(R.id.textView9);
        userPhoto = findViewById(R.id.imageView2);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textAnswer = findViewById(R.id.openAnswer);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textColorDefaultRb = rb1.getTextColors();

        questionCountTotal = questionnaire.size();

        showNextQuestion();

        /*
        clicking the conform button starts a check to make sure either Edit text field is not empty or one of the 2 or 4 radio
        buttons is checked
         */
        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
                    String answer = rbSelected.getText().toString();
                    answers.add(answer);
                    showNextQuestion();
                } else if(!textAnswer.getText().toString().trim().isEmpty()){
                    answers.add(textAnswer.getText().toString().trim());
                    showNextQuestion();
                } else {
                    Toast.makeText(AnswerActivity.this, "Please select/type an answer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        if the user clicks on the profile avatar, they are taken to the View profile activity
        the profile's user ID is passed as an intent to the next activtiy after being read from an intent with
        the previous activity in the question fragment
         */
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currId = getIntent().getStringExtra("userID");
                Intent intent = new Intent(getApplicationContext(),ViewProfile.class);
                intent.putExtra("uid", currId);
                startActivity(intent);
            }
        });


    }

    /*
    this method is used store the answers of the current user to the right collection path of the questionnaire owner
    afterwards, a realtime databse child is created to add the questionnaire owner to the list of profiles the current user
    can chat with
     */
    private void saveAnswer(List<String> answers){

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        String currId = getIntent().getStringExtra("userID");
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fstore.collection("questionnaire").document(currId);
        Map<String, Object> answersHash = new HashMap<>();
        int i = 1;
        for(String answer : answers){
            answersHash.put(String.valueOf(i),answer);
            i++;
        }
        String name = Utils.getName(context);
        answersHash.put("name",name);

        docRef.collection("answers").document(userId).set(answersHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Log.d("SAVING ANSWERS", "Success");
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        dbRef = database.getReference("/users/"+userId+displayName+"/"+currId+newQuestion.getName()).push();
        dbRef.setValue("Hello");
    }

    /*
    this class is used to check the question type of the current question and use a switch to load up the
    appropriate format to answer the question
     */
    private void showNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rb4.setTextColor(textColorDefaultRb);
        textAnswer.setVisibility(View.GONE);
        rb1.setVisibility(View.GONE);
        rb2.setVisibility(View.GONE);
        rb3.setVisibility(View.GONE);
        rb4.setVisibility(View.GONE);
        rbGroup.clearCheck();
        if (questionCounter < questionCountTotal) {
            newQuestion = questionnaire.get(questionCounter);
            username.setText(newQuestion.getName());
            switch (newQuestion.getType()){
                case "yesOrno":
                    yesOrnoQuestion();
                    break;
                case "mcq":
                    mcqQuestion();
                    break;
                case "open":
                    openQuestion();
                    break;
            }
        } else {
            saveAnswer(answers);
            finishQuiz();
        }
    }

    /*
    this method makes the textAnswer Edit text visible so the user can type in their answer for the open question
     */
    private void openQuestion() {
        textAnswer.getText().clear();
        textAnswer.setVisibility(View.VISIBLE);
        textViewQuestion.setText(newQuestion.getQuestion());
        questionCounter++;
        textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
        buttonConfirmNext.setText("Confirm");
    }

    /*
    this method is called when the question type is multiple choice so it makes all 4 radio buttons visible and populates them with the
    correct options from that question
     */
    private void mcqQuestion() {
        textViewQuestion.setText(newQuestion.getQuestion());
        rb1.setVisibility(View.VISIBLE);
        rb2.setVisibility(View.VISIBLE);
        rb3.setVisibility(View.VISIBLE);
        rb4.setVisibility(View.VISIBLE);
        rb1.setText(newQuestion.getOption1());
        rb2.setText(newQuestion.getOption2());
        rb3.setText(newQuestion.getOption3());
        rb4.setText(newQuestion.getAnswer());
        questionCounter++;
        textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
        buttonConfirmNext.setText("Confirm");
    }

    /*
    this method is called if the question type is yes or no
    2 radio buttons are then made visible for the user to answer
     */
    private void yesOrnoQuestion() {
        textViewQuestion.setText(newQuestion.getQuestion());
        rb1.setVisibility(View.VISIBLE);
        rb2.setVisibility(View.VISIBLE);
        rb1.setText("Yes");
        rb2.setText("No");
        questionCounter++;
        textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
        buttonConfirmNext.setText("Confirm");

    }


    /*
    this method is called once all the questions in the list have beeen answered
    the activity is closed and a toast is displayed to let the user know
     */
    private void finishQuiz() {
        finish();
        Toast.makeText(getApplicationContext(), "Quiz has ended", Toast.LENGTH_SHORT).show();
    }

    /*
    this method is used to return the list of questions for the answer activity
    it takes user ID from the prevent activity intent and finds the users downloaded question file
    the 1st photo of the user is then displayed in the image view if the storage call is successful
     */
    private List<Question> getCurrentQuestion(){

        if(getIntent().hasExtra("userID")){
            String currId = getIntent().getStringExtra("userID");
            List<Question> questionaire = Utils.getQuestions(currId,context);
            try{
                StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/"+currId+"/photos/photo1");
                final long ONE_MEGABYTE = 1024*1024;
                storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        userPhoto.setImageBitmap(photo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "No Profile Photo found", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
            return questionaire;
        }
        return null;
    }


}