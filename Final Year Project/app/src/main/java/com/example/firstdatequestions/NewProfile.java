package com.example.firstdatequestions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Year;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewProfile extends AppCompatActivity {

    private EditText dname,birthday,work,uni,info;
    private RadioGroup gender, orientation;
    private RadioButton pickedGender, pickedOrientation;
    private Button submit;
    private ImageView uploadP;
    DatePickerDialog datePicker;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

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

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                datePicker = new DatePickerDialog(NewProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        birthday.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },mYear,mMonth,mDay);
                datePicker.show();
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
                        FirebaseAuth fAuth = FirebaseAuth.getInstance();
                        String userId = fAuth.getCurrentUser().getUid();

                        DocumentReference docRef = fstore.collection("users").document(userId);
                        Map<String, Object> user = new HashMap<>();
                        user.put("Display_Name",dname.getText().toString());
                        user.put("Age", birthday.getText().toString());
                        user.put("Occupation",work.getText().toString());
                        user.put("School",uni.getText().toString());
                        user.put("About",info.getText().toString());
                        user.put("Gender",pickedGender.getText());
                        user.put("Orientation",pickedOrientation.getText());
                        user.put("Likes", 0);

                        docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Message", "Details saved");
                                Toast.makeText(NewProfile.this,"Details Saved",Toast.LENGTH_SHORT).show();
                                Fragment upload = new UploadPhotos();
                                startFragment(upload);
                            }
                        });


                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });


    }

    private void startFragment(Fragment upload) {
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.replace(R.id.frameLayout, upload).commit();
    }
}