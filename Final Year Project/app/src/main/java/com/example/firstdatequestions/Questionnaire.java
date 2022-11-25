package com.example.firstdatequestions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Questionnaire extends AppCompatActivity {

    EditText ynQuestion, mcqQuestion, openQuestion, mcqAnswer, mcqOption1, mcqOption2, mcqOption3;
    ToggleButton ynAnswer;
    ImageButton submitYN, submitMCQ, submitOpen, goBack;
    Button ynButton, mcqButton, openButton, submitQuestions;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    List<Question> listOfQuestions;
    List<String> displayList;
    String userId;
    Context context;
    private View yesOrNoView, mCQView, openView;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        context = getApplicationContext();


        ynQuestion = findViewById(R.id.YNQuestion);
        mcqQuestion = findViewById(R.id.MCQQuestion);
        openQuestion = findViewById(R.id.openQuestion);
        mcqAnswer = findViewById(R.id.MCQAnswer);
        mcqOption1 = findViewById(R.id.MCQOption1);
        mcqOption2 = findViewById(R.id.MCQOption2);
        mcqOption3 = findViewById(R.id.MCQOption3);
        ynAnswer = findViewById(R.id.YNAnswer);
        submitYN = findViewById(R.id.imageBtnYN);
        submitOpen = findViewById(R.id.imageBtnOpen);
        submitMCQ = findViewById(R.id.imageBtnMCQ);
        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        yesOrNoView = findViewById(R.id.yesOrNoView);
        mCQView = findViewById(R.id.MCQView);
        openView = findViewById(R.id.openView);
        ynButton = findViewById(R.id.ynbutton);
        openButton = findViewById(R.id.openButton);
        goBack = findViewById(R.id.goBack);
        listOfQuestions = new ArrayList<>();
        displayList = new ArrayList<>();
        mcqButton = findViewById(R.id.mcqbutton);
        submitQuestions = findViewById(R.id.submitQuestions);
        ListView listView = (ListView) findViewById(R.id.questionList);

        yesOrNoView.setVisibility(View.GONE);
        mCQView.setVisibility(View.GONE);
        openView.setVisibility(View.GONE);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        DocumentReference docRef = fstore.collection("questionnaire").document(userId);

        List<Question> myQuestionnaire = Utils.getQuestions("MYQUESTIONS"+userId, getApplicationContext());
        if(myQuestionnaire != null){
            for (Question question : myQuestionnaire){
                listOfQuestions.add(question);
                displayList.add(question.getQuestion());
                //System.out.println(question.getQuestion());
                //listView.setVisibility(View.VISIBLE);
                updateList();

            }
        }

        ynButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.GONE);
                crossfade(yesOrNoView,mCQView,openView);
            }
        });

        mcqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.GONE);
                crossfade(mCQView,yesOrNoView,openView);
            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.GONE);
                crossfade(openView,yesOrNoView,mCQView);
            }
        });


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DashBoardActivity.class));
            }
        });



        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview,displayList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(Questionnaire.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete this question? ");
                final int posToremove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listOfQuestions.remove(posToremove);
                        displayList.remove(posToremove);
                        deleteItem(docRef,posToremove);
                        adapter.notifyDataSetChanged();
                        updateList();
                    }});
                adb.show();
            }
        });

        submitMCQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = mcqQuestion.getText().toString().trim();
                String answer = mcqAnswer.getText().toString().trim();
                String option1 = mcqOption1.getText().toString().trim();
                String option2 = mcqOption2.getText().toString().trim();
                String option3 = mcqOption3.getText().toString().trim();
                String type = "mcq";
                String name = getName();
                Question newQuestion = new Question(name, type, question, answer, option1, option2, option3);
                listView.setVisibility(View.VISIBLE);
                mCQView.setVisibility(View.GONE);
                mcqQuestion.setText("");

                if(listOfQuestions.isEmpty()){
                    listOfQuestions.add(newQuestion);
                    displayList.add(question);
                    updateList();
                    //saveQuestion(docRef,newQuestion,1);

                }
                else {
                    if(listOfQuestions.size()<8){
                        listOfQuestions.add(newQuestion);
                        displayList.add(question);
                        updateList();
                        System.out.println(listOfQuestions);
                        //saveQuestion(docRef,newQuestion,listOfQuestions.size());
                    } else{
                        Toast.makeText(Questionnaire.this,"You already have 8 questions",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



        submitYN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = ynQuestion.getText().toString().trim();
                String answer = ynAnswer.getText().toString().trim();
                String type = "yesOrno";
                String name = getName();

                Question newQuestion = new Question(name, type, question, answer);
                listView.setVisibility(View.VISIBLE);
                yesOrNoView.setVisibility(View.GONE);
                ynQuestion.setText("");

                if(listOfQuestions.isEmpty()){
                    listOfQuestions.add(newQuestion);
                    displayList.add(question);
                    updateList();

                }
                else {
                    if(listOfQuestions.size()<8){
                        listOfQuestions.add(newQuestion);
                        displayList.add(question);
                        updateList();
                        System.out.println(listOfQuestions);
                    } else{
                        Toast.makeText(Questionnaire.this,"You already have 8 questions",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        submitOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = openQuestion.getText().toString().trim();
                String type = "open";
                String name = getName();

                Question newQuestion = new Question(name, type, question);
                listView.setVisibility(View.VISIBLE);
                openView.setVisibility(View.GONE);
                openQuestion.setText("");

                if(listOfQuestions.isEmpty()){
                    listOfQuestions.add(newQuestion);
                    displayList.add(question);
                    updateList();

                }
                else {
                    if(listOfQuestions.size()<8){
                        listOfQuestions.add(newQuestion);
                        displayList.add(question);
                        updateList();
                        System.out.println(listOfQuestions);
                    } else{
                        Toast.makeText(Questionnaire.this,"You already have 8 questions",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        submitQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion(docRef, listOfQuestions);
                Toast.makeText(Questionnaire.this,"Questionnaire Saved",Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void saveQuestion(DocumentReference docRef, List<Question> questionnaire){

        int num = 1;
        for(Question question : questionnaire){
            docRef.collection("questions").document(String.valueOf(num)).set(question).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Questionnaire.this,"Questions Saved",Toast.LENGTH_SHORT).show();
                }
            });
            num++;
        }
        try {
            FileOutputStream fos = context.openFileOutput("MYQUESTIONS"+userId, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(questionnaire);
            out.close();
            Log.d("Saving File "+userId, "Success");
        } catch(Exception e){
            e.printStackTrace();
        }

        startActivity(new Intent(getApplicationContext(),DashBoardActivity.class));

    }

    private void crossfade(View newView, View currentView, View otherView) {

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        System.out.println(shortAnimationDuration);
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        newView.setAlpha(0f);
        newView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        newView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration+1000)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        currentView.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration+1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentView.setVisibility(View.GONE);
                    }
                });
        otherView.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration+1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        otherView.setVisibility(View.GONE);
                    }
                });


    }

    private void updateList(){
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview,displayList);
        ListView listView = (ListView) findViewById(R.id.questionList);
        listView.setAdapter(adapter);
    }

    private String getName(){
        try {
            FileInputStream fis = context.openFileInput("displayName");
            ObjectInputStream in = new ObjectInputStream(fis);
            String name = (String) in.readObject();
            in.close();
            return name;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void deleteItem(DocumentReference docRef, int num){
        docRef.collection("questions").document(String.valueOf(num)).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("DELETE QUESTION", "FAILED");
            }
        });
    }

}