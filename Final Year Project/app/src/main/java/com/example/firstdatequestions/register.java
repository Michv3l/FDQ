package com.example.firstdatequestions;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class register extends Fragment {

    private EditText email,password,cpassword;
    private Button regbtn;
    private FirebaseAuth fAuth;

    public register() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View regFrag = inflater.inflate(R.layout.fragment_register, container, false);
        email = regFrag.findViewById(R.id.email);
        password = regFrag.findViewById(R.id.password);
        cpassword = regFrag.findViewById(R.id.cpassword);
        regbtn = regFrag.findViewById(R.id.rbutton);
        fAuth = FirebaseAuth.getInstance();

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String remail = email.getText().toString().trim();
                final String rpassword = password.getText().toString().trim();
                final String ccheckpass = cpassword.getText().toString().trim();

                if (TextUtils.isEmpty(remail)){
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(rpassword)){
                    password.setError("Password is required");
                    return;
                }

                if (!ccheckpass.contentEquals(rpassword)){
                    cpassword.setError("Your password does not match");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(remail,rpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast toast = Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT);
                            toast.show();

                            startActivity(new Intent(getContext(), NewProfile.class));
                        }else{
                            Toast toast = Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            }
        });

        return regFrag;


    }
}
