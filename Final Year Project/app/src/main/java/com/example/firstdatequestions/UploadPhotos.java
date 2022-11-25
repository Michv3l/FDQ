package com.example.firstdatequestions;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class UploadPhotos extends Fragment {



    private Button doneBtn;
    private FloatingActionButton addPhoto;
    private ImageButton camera;
    private ImageView uploadP, photo1,photo2,photo3,photo4,photo5,photo6;
    UploadTask uploadTask;
    FirebaseStorage photoStore;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int RESULT_OK = 1;

    public UploadPhotos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View upPhotos = inflater.inflate(R.layout.fragment_upload_photos, container, false);
        doneBtn = upPhotos.findViewById(R.id.doneSubmitButton);
        addPhoto = upPhotos.findViewById(R.id.addPhotoButton);
        uploadP = upPhotos.findViewById(R.id.uploadPhoto);
        photo1 = upPhotos.findViewById(R.id.viewPhoto1);
        photo2 = upPhotos.findViewById(R.id.viewPhoto2);
        photo3 = upPhotos.findViewById(R.id.viewPhoto3);
        photo4 = upPhotos.findViewById(R.id.viewPhoto4);
        photo5 = upPhotos.findViewById(R.id.viewPhoto5);
        photo6 = upPhotos.findViewById(R.id.viewPhoto6);
        camera = upPhotos.findViewById(R.id.picFromcamera);


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DashBoardActivity.class));

            }
        });




        return upPhotos;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data != null) {

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

                    Toast.makeText(getContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();


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
                        Toast.makeText(getContext(), "Maximum number of photos uploaded", Toast.LENGTH_SHORT).show();
                    }
                } else if(requestCode == 2){
                    Uri imgSelected = data.getData();
                    uploadP.setImageURI(imgSelected);

                    Toast.makeText(getContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(getContext(), "Maximum number of photos uploaded", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            else{
                Toast.makeText(getContext(), "Photo not Uploaded", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {

        }
    }

    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

}