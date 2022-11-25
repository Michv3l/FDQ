package com.example.firstdatequestions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText dname,work,uni,info;
    private TextView birthday;
    private RadioGroup gender, orientation;
    private RadioButton pickedGender, pickedOrientation;
    private Button submit;
    private ImageView uploadP,photo1,photo2,photo3,photo4,photo5,photo6;
    StorageReference storage;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    UploadTask uploadTask;
    FirebaseStorage photoStore;
    private FloatingActionButton addPhoto;
    private ImageButton camera;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        dname = findViewById(R.id.displayName);
        birthday = findViewById(R.id.birthday);
        work = findViewById(R.id.work);
        uni = findViewById(R.id.university);
        info = findViewById(R.id.userInfo);
        gender = findViewById(R.id.gender);
        orientation = findViewById(R.id.orientation);
        submit = findViewById(R.id.submitDets);
        uploadP = findViewById(R.id.uploadPhoto);
        addPhoto = findViewById(R.id.addPhotoButton);
        uploadP = findViewById(R.id.uploadPhoto);
        photo1 = findViewById(R.id.viewPhoto1);
        photo2 = findViewById(R.id.viewPhoto2);
        photo3 = findViewById(R.id.viewPhoto3);
        photo4 = findViewById(R.id.viewPhoto4);
        photo5 = findViewById(R.id.viewPhoto5);
        photo6 = findViewById(R.id.viewPhoto6);
        camera = findViewById(R.id.picFromcamera);

        try{

            DocumentReference docRef = fstore.collection("users").document(userId);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String dispName = (String) documentSnapshot.get("Display_Name");
                        String dob = (String) documentSnapshot.get("Age");
                        String about = (String) documentSnapshot.get("About");
                        String occupation = (String) documentSnapshot.get("Occupation");
                        String school = (String) documentSnapshot.get("School");


                        dname.setText(dispName);
                        birthday.setText(dob);
                        info.setText(about);
                        work.setText(occupation);
                        uni.setText(school);


                    } else {
                        System.out.println("No such document!");
                    }
                }
            });

            storage = FirebaseStorage.getInstance().getReference().child("images/"+userId+"/photos/photo1");
            final long ONE_MEGABYTE = 1024*1024;
            storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    uploadP.setImageBitmap(photo);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "No Profile Photo found", Toast.LENGTH_SHORT).show();


                }
            });
        } catch(Exception e){
            e.printStackTrace();
        }

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioGender = gender.getCheckedRadioButtonId();
                int radioOrientation = orientation.getCheckedRadioButtonId();
                pickedGender = findViewById(radioGender);
                pickedOrientation = findViewById(radioOrientation);
                try {

                    DocumentReference docRef = fstore.collection("users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Display_Name",dname.getText().toString());
                    user.put("Age", birthday.getText().toString());
                    user.put("Occupation",work.getText().toString());
                    user.put("School",uni.getText().toString());
                    user.put("About",info.getText().toString());
                    user.put("Gender",pickedGender.getText());
                    user.put("Orientation",pickedOrientation.getText());

                    docRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Message", "Details Updated");
                            Toast.makeText(EditProfile.this,"Details Updated",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),DashBoardActivity.class));
                        }
                    });


                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data != null) {
                //if(requestCode == 1 && resultCode == RESULT_OK && data != null){

                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();


                photoStore = FirebaseStorage.getInstance();
                StorageReference storageRef = photoStore.getReference();
                StorageReference userRef = storageRef.child("images/"+userID+"/photos");

                if(requestCode == 1){
                    Bundle extras = data.getExtras();
                    Bitmap imgSelected = (Bitmap) extras.get("data");
                    uploadP.setImageBitmap(imgSelected);
                    byte[] pic = bitmapToByte(imgSelected);

                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();


                    if(photo1.getDrawable() == null){
                        photo1.setImageBitmap(imgSelected);
                        StorageReference photoRef1 = userRef.child("/photo1");
                        uploadTask = photoRef1.putBytes(pic);
                    } else if(photo2.getDrawable() == null){
                        photo2.setImageBitmap(imgSelected);
                        StorageReference photoRef2 = userRef.child("/photo2");
                        uploadTask = photoRef2.putBytes(pic);
                    } else if(photo3.getDrawable() == null) {
                        photo3.setImageBitmap(imgSelected);
                        StorageReference photoRef3 = userRef.child("/photo3");
                        uploadTask = photoRef3.putBytes(pic);
                    }else if(photo4.getDrawable() == null) {
                        photo4.setImageBitmap(imgSelected);
                        StorageReference photoRef4 = userRef.child("/photo4");
                        uploadTask = photoRef4.putBytes(pic);
                    }else if(photo5.getDrawable() == null) {
                        photo5.setImageBitmap(imgSelected);
                        StorageReference photoRef5 = userRef.child("/photo5");
                        uploadTask = photoRef5.putBytes(pic);
                    }else if(photo6.getDrawable() == null) {
                        photo6.setImageBitmap(imgSelected);
                        StorageReference photoRef6 = userRef.child("/photo6");
                        uploadTask = photoRef6.putBytes(pic);
                    } else {
                        Toast.makeText(getApplicationContext(), "Maximum number of photos uploaded", Toast.LENGTH_SHORT).show();
                    }
                } else if(requestCode == 2){
                    Uri imgSelected = data.getData();
                    uploadP.setImageURI(imgSelected);

                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();

                    if(photo1.getDrawable() == null){
                        photo1.setImageURI(imgSelected);
                        StorageReference photoRef1 = userRef.child("/photo1");
                        uploadTask = photoRef1.putFile(imgSelected);
                    } else if(photo2.getDrawable() == null){
                        photo2.setImageURI(imgSelected);
                        StorageReference photoRef2 = userRef.child("/photo2");
                        uploadTask = photoRef2.putFile(imgSelected);
                    } else if(photo3.getDrawable() == null) {
                        photo3.setImageURI(imgSelected);
                        StorageReference photoRef3 = userRef.child("/photo3");
                        uploadTask = photoRef3.putFile(imgSelected);
                    }else if(photo4.getDrawable() == null) {
                        photo4.setImageURI(imgSelected);
                        StorageReference photoRef4 = userRef.child("/photo4");
                        uploadTask = photoRef4.putFile(imgSelected);
                    }else if(photo5.getDrawable() == null) {
                        photo5.setImageURI(imgSelected);
                        StorageReference photoRef5 = userRef.child("/photo5");
                        uploadTask = photoRef5.putFile(imgSelected);
                    }else if(photo6.getDrawable() == null) {
                        photo6.setImageURI(imgSelected);
                        StorageReference photoRef6 = userRef.child("/photo6");
                        uploadTask = photoRef6.putFile(imgSelected);
                    } else {
                        Toast.makeText(getApplicationContext(), "Maximum number of photos uploaded", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            else{
                Toast.makeText(getApplicationContext(), "Photo not Uploaded", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.CAMERA) // ask single or multiple permission once
                .subscribe(granted -> {
                    if (granted) {

                        try {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        } catch (ActivityNotFoundException e) {
                            // display error state to the user
                        }
                        // All requested permissions are granted
                    } else {
                        // At least one permission is denied
                    }
                });
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }


}