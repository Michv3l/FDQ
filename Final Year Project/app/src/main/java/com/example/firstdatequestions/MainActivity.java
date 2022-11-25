package com.example.firstdatequestions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;



public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getInstance().getCurrentUser() != null){

            fstore = FirebaseFirestore.getInstance();
            String userId = fAuth.getCurrentUser().getUid();
            DocumentReference docRef = fstore.collection("users").document(userId);
            Task<DocumentSnapshot> future = docRef.get();
            Task<DocumentSnapshot> document = future.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap.exists()){
                        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                    }else{
                        startActivity(new Intent(getApplicationContext(), NewProfile.class));
                    }
                }
            });
        }

        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new login());
        pagerAdapter.addFragment(new register());
        viewPager.setAdapter(pagerAdapter);
    }

    class AuthenticationPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentList = new ArrayList<>();

        public AuthenticationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
