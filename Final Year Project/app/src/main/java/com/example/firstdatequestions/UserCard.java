package com.example.firstdatequestions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Layout(R.layout.user_profile_view)
public class UserCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.aboutMetxt)
    private TextView aboutMeTxt;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private Bitmap photo;
    private String mUserId;
    FirebaseFirestore fstore;


    public UserCard(Context context, Profile profile, SwipePlaceHolderView swipeView, String userId) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
        mUserId= userId;
    }

    @Resolve
    private void onResolved(){

        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/"+mUserId+"/photos/photo1");
        final long ONE_MEGABYTE = 1024*1024;
        storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileImageView.setImageBitmap(photo);
                String birthday = mProfile.getAge();
                int age = Utils.userAge(birthday);
                nameAgeTxt.setText(mProfile.getDisplay_Name() + ", "+ age + ", " + mProfile.getSchool() );
                aboutMeTxt.setText(mProfile.getAbout()+ ", " + mProfile.getOrientation());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String birthday = mProfile.getAge();
                int age = Utils.userAge(birthday);
                nameAgeTxt.setText(mProfile.getDisplay_Name() + ", "+ age + ", " + mProfile.getSchool() );
                aboutMeTxt.setText(mProfile.getAbout()+ ", " + mProfile.getOrientation());

                Toast.makeText(mContext.getApplicationContext(), "No Profile Photo found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        fstore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put(mProfile.getDisplay_Name(), mUserId);
        Task<DocumentSnapshot> docReference = fstore.collection("matches").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean isMatch = documentSnapshot.exists();
                if (isMatch == true){
                    Task<Void> docRef = fstore.collection("matches").document(userId).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("onSwipedIn", "Success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d("onSwipedIn", "Failed");
                        }
                    });
                    Log.d("EVENT", "onSwipedIn");
                } else {
                    Task<Void> docRef = fstore.collection("matches").document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("onSwipedIn", "Success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d("onSwipedIn", "Failed");
                        }
                    });
                    Log.d("EVENT", "onSwipedIn");
                }
            }
        });

        DocumentReference docRef = fstore.collection("users").document(mUserId);
        docRef.update("Likes",1);

    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

}
