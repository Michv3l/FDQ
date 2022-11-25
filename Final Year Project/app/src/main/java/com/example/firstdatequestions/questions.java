package com.example.firstdatequestions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class questions extends Fragment {

    Button editQuest;
    Context context = getContext();
    FirebaseFirestore fstore;
    int noOfLikes;

    public questions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View questionFrag = inflater.inflate(R.layout.fragment_questions, container, false);
        context = getContext();

        editQuest = questionFrag.findViewById((R.id.imageButtonEditQuest));
        RecyclerView questions = questionFrag.findViewById(R.id.recyclerview1);
        ListView answers = questionFrag.findViewById(R.id.listview2);
        List<String> namesOfAnswers = new ArrayList<>();
        ArrayList<ArrayList<String>> answersList = new ArrayList<>();
        String username = getName();
        List<String> matches = readFile(username+"matches");
        System.out.println(matches+"THESE ARE MATCHESSSSSSSSSSSSSSSSSSSSSS");
        List<Question> questionList = new ArrayList<Question>();
        List<String> idList = new ArrayList<>();
        if (matches == null){

        }else{
            for (String uid : matches){
                downloadQuestions(uid);
                List<Question> questionaire = Utils.getQuestions(uid,context);
                Question newQuestion = new Question(questionaire);
                idList.add(uid);
                questionList.add(newQuestion);
                System.out.println(newQuestion);
                Log.d("GETTING QUESTIONNAIRE "+uid, "Success");
            }
        }

        questions.setLayoutManager(new LinearLayoutManager(this.getContext()));
        QuestionAdapter qAdapter = new QuestionAdapter(this.getContext(), questionList, idList);
        questions.setAdapter(qAdapter);

        answers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,ViewAnswers.class);
                intent.putExtra("Name",namesOfAnswers.get(position));
                intent.putStringArrayListExtra("Answers",answersList.get(position));
                context.startActivity(intent);
            }
        });


        editQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Questionnaire.class));
            }
        });

        noOfLikes=0;

        fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference docRef = fstore.collection("users").document(userID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long likes = (Long) documentSnapshot.get("Likes");
                    noOfLikes = Math.toIntExact(likes);
                    if(noOfLikes>0){
                        saveLikes(getName());
                        saveMatches(getName());
                        //downloadAnswers();
                    }


                } else {
                    //System.out.println("No such document!");
                }
            }
        });



        Task<QuerySnapshot> documentReference = fstore.collection("questionnaire").document(userID).collection("answers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> docsnap = task.getResult().getDocuments();

                    for(DocumentSnapshot dSnap : docsnap){
                        ArrayList<String> listOfAnswers = new ArrayList<>();
                        String docId = dSnap.getId();
                        HashMap hashM = (HashMap) dSnap.getData();
                        String userName = (String) hashM.get("name");
                        namesOfAnswers.add(userName);
                        for (Object object : hashM.values()){
                            String answer = (String) object;
                            listOfAnswers.add(answer);
                        }
                        answersList.add(listOfAnswers);
                    }
                    answers.setAdapter(new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1,namesOfAnswers));
                }
            }
        });

        return questionFrag;
    }

    @SuppressWarnings("unchecked")
    private List<String> readFile(String fileName){
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream in = new ObjectInputStream(fis);
            List<String> list = new ArrayList<String>();
            list= (List<String>) in.readObject();
            in.close();
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    private void downloadQuestions(String userId){
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> docRef = fstore.collection("questionnaire").document(userId).collection("questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                List<Question> myQuestions = new ArrayList<>();
                List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
                int i = 0;
                for (DocumentSnapshot docSnap : documentSnapshot){
                    Question question = docSnap.toObject(Question.class);
                    myQuestions.add(question);
                    i++;
                }
                try {
                    FileOutputStream fos = context.openFileOutput(userId, Context.MODE_PRIVATE);
                    ObjectOutputStream out = new ObjectOutputStream(fos);
                    out.writeObject(myQuestions);
                    out.close();
                    Log.d("Saving File "+userId, "Success");
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void downloadAnswers(){
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        Task<QuerySnapshot> documentReference = fstore.collection("questionnaire").document(userId).collection("answers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> docsnap = task.getResult().getDocuments();

                    for(DocumentSnapshot dSnap : docsnap){
                        List<String> listOfAnswers = new ArrayList<>();
                        String docId = dSnap.getId();
                        HashMap hashM = (HashMap) dSnap.getData();
                        for (Object object : hashM.values()){
                            String answer = (String) object;
                            listOfAnswers.add(answer);
                        }
                        System.out.println(listOfAnswers+"THESE ARE ANSWERSSSS");
                        try {
                            FileOutputStream fos = context.openFileOutput("Answers"+docId, Context.MODE_PRIVATE);
                            ObjectOutputStream out = new ObjectOutputStream(fos);
                            out.writeObject(listOfAnswers);
                            out.close();
                            Log.d("Saving Answers "+docId, "Success");
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

    }

    private void saveLikes(String name){
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();


        Task<QuerySnapshot> docSnap = fstore.collection("matches").whereEqualTo(name,userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                Log.d("Getting Likes", "Success");

                List<String> matches = new ArrayList<String>();
                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                    matches.add(document.getId());
                    System.out.println(document.getId());
                }
                saveFile(name+"likes",matches);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("Getting Matches", "Failed");
            }
        });
    }

    private void saveMatches(String name){
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();

        DocumentReference docRef = fstore.collection("users").document(userId);

        Task<DocumentSnapshot> docSnap = fstore.collection("matches").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                List<String> likes = readFile(name+"likes");
                if(likes == null){

                }else{
                    DocumentSnapshot documentSnapshot = task.getResult();
                    HashMap hashM = (HashMap) documentSnapshot.getData();
                    List<String> matched = new ArrayList<String>();
                    for (Object object : hashM.values()){
                        String docId = object.toString();
                        for (String like : likes){
                            if (docId.matches(like)){
                                matched.add(like);
                            }
                        }
                    }
                    saveFile(name+"matches",matched);
                }

            }
        });
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

    private void saveFile(String filename, List<String> list){
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
            Log.d("Saving File "+filename, "Success");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}