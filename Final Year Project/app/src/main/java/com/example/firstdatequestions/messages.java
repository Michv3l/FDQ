package com.example.firstdatequestions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firstdatequestions.databinding.ActivityMainBinding;
import com.getstream.sdk.chat.ChatUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;

import static java.util.Collections.singletonList;


public class messages extends Fragment {

    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al, userIdList;
    int totalUsers = 0;
    ProgressDialog pd;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;

    public messages() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View messageFrag = inflater.inflate(R.layout.fragment_messages, container, false);


        al = new ArrayList<>();
        userIdList = new ArrayList<>();
        usersList = (ListView) messageFrag.findViewById(R.id.usersList);
        noUsersText = (TextView) messageFrag.findViewById(R.id.noUsersText);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        String displayName = Utils.getName(getContext());

        //pd = new ProgressDialog(DashBoardActivity.this);
        pd = new ProgressDialog(messages.super.getContext());
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://final-year-fds-default-rtdb.firebaseio.com/users/"+userId+displayName+".json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(messages.super.getContext());
        rQueue.add(request);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = userIdList.get(position);
                startActivity(new Intent(messages.super.getContext(), Chat.class));
            }
        });

        return messageFrag;
    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(UserDetails.username)) {
                    //String keyName = key.substring(28);
                    al.add(key.substring(28));
                    userIdList.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(super.getContext(), android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();

    }
}