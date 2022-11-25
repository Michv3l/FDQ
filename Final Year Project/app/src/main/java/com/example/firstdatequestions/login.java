package com.example.firstdatequestions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.StringValue;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class login extends Fragment {

    private EditText email,password;
    private Button lgbutton;
    private TextView forgotLink;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private static final String EMAIL = "email";
    CallbackManager callbackManager = CallbackManager.Factory.create();

    public login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //CallbackManager callbackManager = CallbackManager.Factory.create();



        View logFrag = inflater.inflate(R.layout.fragment_login, container, false);
        email = logFrag.findViewById(R.id.et_email);
        password = logFrag.findViewById(R.id.et_password);
        lgbutton = logFrag.findViewById(R.id.btn_login);
        forgotLink = logFrag.findViewById(R.id.forgotLink);
        fAuth = FirebaseAuth.getInstance();

        LoginButton loginButton = (LoginButton) logFrag.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setFragment(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


        lgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lemail = email.getText().toString().trim();
                String lpassword = password.getText().toString().trim();
                if (TextUtils.isEmpty(lemail)){
                    email.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(lpassword)){
                    password.setError("Password is required");
                    return;
                }
                fAuth.signInWithEmailAndPassword(lemail,lpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast toast = Toast.makeText(getContext(),"Logged in Successfully",Toast.LENGTH_SHORT);
                            toast.show();
                            fstore = FirebaseFirestore.getInstance();
                            String userId = fAuth.getCurrentUser().getUid();
                            DocumentReference docRef = fstore.collection("users").document(userId);
                            Task<DocumentSnapshot> future = docRef.get();
                            Task<DocumentSnapshot> document = future.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot docSnap = task.getResult();
                                    if (docSnap.exists()){
                                        startActivity(new Intent(getContext(), DashBoardActivity.class));
                                    }else{
                                        startActivity(new Intent(getContext(), NewProfile.class));
                                    }
                                }
                            });
                        }
                        else{
                            Toast toast = Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            }
        });

        forgotLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(getContext());
                adb.setTitle("Reset Password");
                adb.setMessage("Please type in your Email address");
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                adb.setView(input);
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailAddress = input.getText().toString();
                        fAuth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("EMAIL REQUEST", "Email sent.");
                                            Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Log.d("EMAIL REQUEST",String.valueOf(e));
                                Toast.makeText(getContext(), "Email not sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                adb.show();
            }
        });


        return logFrag;



    }
}
