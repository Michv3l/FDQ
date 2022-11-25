package com.example.firstdatequestions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/*
this activity is used to load and display user profiles
 */
public class ViewProfile extends AppCompatActivity {

    Button goBack;
    ImageView photo1, photo2, photo3, photo4, photo5, photo6;
    TextView displayName, aboutMe, age, gender, orientation, work, uni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        displayName = findViewById(R.id.newProfileTitle);
        aboutMe = findViewById(R.id.userInfo);
        age = findViewById(R.id.birthday);
        gender = findViewById(R.id.gender);
        orientation = findViewById(R.id.orientation);
        work = findViewById(R.id.work);
        uni = findViewById(R.id.university);
        photo1 = findViewById(R.id.viewPhoto1);
        photo2 = findViewById(R.id.viewPhoto2);
        photo3 = findViewById(R.id.viewPhoto3);
        photo4 = findViewById(R.id.viewPhoto4);
        photo5 = findViewById(R.id.viewPhoto5);
        photo6 = findViewById(R.id.viewPhoto6);



/*
the ID of the required profile is passed to this activity
firstore instance is created to get a reference to the document with the user ID
Once the document is successfully found, a snapshot is taken and used to populate the fields on thsi layout
 */
        String userId = getIntent().getStringExtra("uid");
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fstore.collection("users").document(userId);

        try{

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String dispName = (String) documentSnapshot.get("Display_Name");
                    String info = (String) documentSnapshot.get("About") ;
                    String birthday = (String) documentSnapshot.get("Age");
                    int userAge = Utils.userAge(birthday);
                    String mGender = (String) documentSnapshot.get("Gender");
                    String mOrientation = (String) documentSnapshot.get("Orientation");
                    String occupation = (String) documentSnapshot.get("Occupation");
                    String school = (String) documentSnapshot.get("School");

                    displayName.setText(dispName+"'s profile");
                    age.setText("Age: "+userAge);
                    gender.setText("Gender: "+mGender);
                    orientation.setText("Orientation: "+mOrientation);
                    work.setText("Occupation: "+occupation);
                    uni.setText("University: "+school);

                    aboutMe.setText("About Me: \n"+info);

                } else {
                    System.out.println("No such document!");
                }
            }
        });


        /*
        this for loop is used to check each of the 6 photo locations of the user if a picture exists and if it does, it sets the
        picture in the appropriate image view
         */
        for(int i=1; i<7; i++){
            StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/"+userId+"/photos/photo"+i);
            final long ONE_MEGABYTE = 1024*1024;
            int finalI = i;
            storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    switch (finalI){
                        case 1:
                            photo1.setImageBitmap(photo);
                            break;
                        case 2:
                            photo2.setImageBitmap(photo);
                            break;
                        case 3:
                            photo3.setImageBitmap(photo);
                            break;
                        case 4:
                            photo4.setImageBitmap(photo);
                            break;
                        case 5:
                            photo5.setImageBitmap(photo);
                            break;
                        case 6:
                            photo6.setImageBitmap(photo);
                            break;

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }

    } catch(Exception e){
        e.printStackTrace();
    }


    }
}