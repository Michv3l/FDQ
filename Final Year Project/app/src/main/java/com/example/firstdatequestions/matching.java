package com.example.firstdatequestions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/*
used to manage the profiles shown in the matching fragment
original code gotten from https://blog.mindorks.com/android-tinder-swipe-view-example-3eca9b0d4794
 */
public class matching extends Fragment {


    SwipePlaceHolderView mSwipeView;
    private Context context;

    public matching() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();

        View matchFrag = inflater.inflate(R.layout.fragment_matching, container, false);



        mSwipeView = (SwipePlaceHolderView)matchFrag.findViewById(R.id.swipeView);
        context = getContext();

        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out));

        FirebaseFirestore fstore = FirebaseFirestore.getInstance();

        List<Profile> profileList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        Task<QuerySnapshot> future = fstore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for(DocumentSnapshot document : documents){
                    Log.d("Getting Users", "Working");
                    String docID = document.getId();
                    Double newlat = (Double) document.get("Latitude");
                    Double newlon = (Double) document.get("Longitude");
                    String gender = (String) document.get("Gender");
                    String birthday = (String) document.get("Age");
                    int age = Utils.userAge(birthday);
                    Double distance = getDistance(newlat, newlon, UserDetails.latitude, UserDetails.longitude);
                    List<String> likes = readFile(getName()+"likes");
                    if (likes == null){

                    }else{
                        if(likes.contains(docID)){
                            continue;
                        }
                    }
                    if(!UserDetails.preferredGender.matches("default")){
                        if(!gender.matches(UserDetails.preferredGender)){
                            continue;
                        }
                    }
                    if(age<18 || distance > UserDetails.maxDist || docID.matches(userID)){
                        continue;
                    }
                    idList.add(docID);
                    Profile userProf = document.toObject(Profile.class);
                    profileList.add(userProf);

                }
                int i = 0;
                for (Profile profile : profileList){
                    mSwipeView.addView(new UserCard(context, profile, mSwipeView, idList.get(i)));
                    i++;
                }
            }
        });



        matchFrag.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });


        matchFrag.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });


        return matchFrag;
    }
    

    // Calculate distance between two points in latitude and longitude taking
    // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude?rq=1
    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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




}

