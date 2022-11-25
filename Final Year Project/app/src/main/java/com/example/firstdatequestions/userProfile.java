package com.example.firstdatequestions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class userProfile extends Fragment {


    Button logOut;
    ImageView profilePhoto;
    StorageReference storage;
    FirebaseFirestore fstore;
    TextView displayName, aboutMe;
    ImageButton editbtn, settings;
    Context context = getContext();


    public userProfile() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View userFrag = inflater.inflate(R.layout.fragment_user_profile, container, false);
        fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        displayName = userFrag.findViewById(R.id.name_display);
        editbtn = userFrag.findViewById(R.id.editButton);
        profilePhoto = userFrag.findViewById(R.id.dProfilePhoto);
        settings = userFrag.findViewById(R.id.imageButton3);
        context = getContext();
        aboutMe = userFrag.findViewById(R.id.aboutMe);


        try{

            DocumentReference docRef = fstore.collection("users").document(userID);// firestore reference to the current user's details

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String dispName = (String) documentSnapshot.get("Display_Name");
                        String info = (String) documentSnapshot.get("About") ;
                        String birthday = (String) documentSnapshot.get("Age");
                        int age = Utils.userAge(birthday);
                        if (age<18){
                            startActivity(new Intent(getContext(),AgeRestriction.class));// starts the age restriction activity
                        }
                        displayName.setText(dispName);
                        aboutMe.setText(info);
                        UserDetails.username = userID+dispName;
                        UserDetails.longitude = (Double) documentSnapshot.get("Longitude");
                        UserDetails.latitude = (Double) documentSnapshot.get("Latitude");

                        saveName(dispName);

                    } else {

                    }
                }
            });

            // checks the storage reference for the media photo and if successful displays it in the Image View
            storage = FirebaseStorage.getInstance().getReference().child("images/"+userID+"/photos/photo1");
            final long ONE_MEGABYTE = 1024*1024;
            storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profilePhoto.setImageBitmap(photo);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), "No Profile Photo found", Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception e){
            e.printStackTrace();
        }

        logOut = userFrag.findViewById(R.id.logOutbtn);

        // button used to start the edit profile activity
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),EditProfile.class));
            }
        });

        //uses firebase's authentication to log a user out of the application and return them to the login fragment
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));

            }
        });

        //starts the settings activity
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });

        return userFrag;
    }

   /*
   method used to save the display name of the current user to a local file on their mobile device
    */
    private void saveName(String name){
        try{
            FileOutputStream fos = context.openFileOutput("displayName", Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(name);
            out.close();
            Log.d("Saving Name", "Success");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}