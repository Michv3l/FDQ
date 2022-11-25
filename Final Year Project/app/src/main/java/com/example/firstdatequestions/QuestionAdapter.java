package com.example.firstdatequestions;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.StringValue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>{

    List<Question> questionList;
    List<String> idList;

    private Context contx;

    public QuestionAdapter(Context contx, List<Question> questionList, List<String> idList){
        this.questionList = questionList;
        this.contx = contx;
        this.idList = idList;

    }

    @NonNull
    @NotNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contx);
        View view = inflater.inflate(R.layout.recyclerview_item, null);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull QuestionViewHolder holder, int position) {

        List<Question> questionnaire = (List<Question>) questionList.get(position).getQuestionnaire();
        if(questionnaire == null){

        }else{
            for (Question question : questionnaire){
                holder.name.setText(question.getName());
                holder.quest.setText(question.getQuestion());
                holder.goToQuest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(contx,AnswerActivity.class);
                        intent.putExtra("userID",idList.get(position));
                        contx.startActivity(intent);
                    }
                });
                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder{

        ImageView imgView;
        TextView name;
        TextView quest;
        ImageButton goToQuest;

        public QuestionViewHolder(View view) {
            super(view);

            imgView = view.findViewById(R.id.image);
            name = view.findViewById(R.id.textView);
            quest = view.findViewById(R.id.firstQ);
            goToQuest = view.findViewById(R.id.goToQuest);

        }
    }
}
