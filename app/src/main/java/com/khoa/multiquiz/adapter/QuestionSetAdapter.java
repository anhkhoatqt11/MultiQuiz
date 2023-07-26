package com.khoa.multiquiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.multiquiz.GroupQuestion;
import com.khoa.multiquiz.GroupQuestionCreate;
import com.khoa.multiquiz.GroupQuestionDetail;
import com.khoa.multiquiz.GroupQuestionSetInfo;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuestionSetAdapter extends RecyclerView.Adapter<QuestionSetAdapter.QuestionSetViewHolder> {

    public interface OnClickListener {
        void onClick(int position, GroupQuestionSetInfo groupQuestionSetInfo);
    }

    public interface OnPlayButtonClickListener {
        void onPlayButtonClick(int position, GroupQuestionSetInfo groupQuestionSetInfo);
    }

    public interface OnEditButtonClickListener {
        void onEditButtonClick(int position, GroupQuestionSetInfo groupQuestionSetInfo);
    }

    private OnClickListener onClickListener;
    private OnPlayButtonClickListener onPlayButtonClickListener;
    private OnEditButtonClickListener onEditButtonClickListener;
    Context context;
    private ArrayList<GroupQuestionSetInfo> questionSetInfos;

    public QuestionSetAdapter(Context context, ArrayList<GroupQuestionSetInfo> groupQuestionSetInfo){
        this.context = context;
        this.questionSetInfos = groupQuestionSetInfo;
    }


    @NonNull
    @Override
    public QuestionSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_questionset_row_item,parent,false);
        return new QuestionSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionSetViewHolder holder, int position) {
        GroupQuestionSetInfo groupQuestionSetInfo = questionSetInfos.get(position);
        String QuestionSetTitle = groupQuestionSetInfo.getQuestionSetTitle();
        String QuestionSetDescription = groupQuestionSetInfo.getQuestionSetDescription();

        holder.QuestionSetTitleTextView.setText(QuestionSetTitle);
        holder.QuestionSetDescriptionTextView.setText(QuestionSetDescription);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null){
                    onClickListener.onClick(position, groupQuestionSetInfo);
                }
            }
        });

        holder.PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlayButtonClickListener != null) {
                    onPlayButtonClickListener.onPlayButtonClick(position, groupQuestionSetInfo);
                }
            }
        });

        holder.EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlayButtonClickListener != null) {
                    onPlayButtonClickListener.onPlayButtonClick(position, groupQuestionSetInfo);
                }
            }
        });


    }

    public int getItemCount() {
        return questionSetInfos.size();
    }

    public void setOnClickListener(QuestionSetAdapter.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public void setOnPlayButtonClickListener(QuestionSetAdapter.OnPlayButtonClickListener onPlayButtonClickListener){
        this.onPlayButtonClickListener = onPlayButtonClickListener;
    }

    public void setOnEditButtonClickListener(QuestionSetAdapter.OnEditButtonClickListener onEditButtonClickListener){
        this.onEditButtonClickListener = onEditButtonClickListener;
    }




    public static class QuestionSetViewHolder extends RecyclerView.ViewHolder {

        TextView QuestionSetTitleTextView, QuestionSetDescriptionTextView;
        ImageView PlayButton, EditButton;
        public QuestionSetViewHolder(@NonNull View itemView) {
            super(itemView);
            QuestionSetTitleTextView = itemView.findViewById(R.id.QuestionSetName);
            QuestionSetDescriptionTextView = itemView.findViewById(R.id.QuestionSetDescription);
            PlayButton = itemView.findViewById(R.id.QuestionSetPlayButton);
            EditButton = itemView.findViewById(R.id.QuestionSetEditButton);




        }
    }


}
