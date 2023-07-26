package com.khoa.multiquiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.multiquiz.GroupQuestion;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    Context context;
    private ArrayList<GroupQuestion>  groupQuestionArrayList;
    private OnDeleteClickListener onDeleteClickListener;
    private OnItemClickListener onItemClickListener;

    public QuestionAdapter(Context context, ArrayList<GroupQuestion> GroupQuestionLists){
        this.context = context;
        this.groupQuestionArrayList = GroupQuestionLists;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(groupQuestionArrayList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        // Update QuestionNumber for all items after the move
        for (int i = 0; i < groupQuestionArrayList.size(); i++) {
            groupQuestionArrayList.get(i).setQuestionNumber(i + 1);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row_item,parent,false);
        return new QuestionAdapter.QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        GroupQuestion groupQuestion = groupQuestionArrayList.get(position);

        // Set the updated QuestionNumber based on the position
        groupQuestion.setQuestionNumber(position + 1);

        String QuestionNumber = String.valueOf(groupQuestion.getQuestionNumber());
        String QuestionText = groupQuestion.getQuestionText();

        holder.QuestionNumber.setText(QuestionNumber);
        holder.QuestionText.setText(QuestionText);

        holder.DeleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }


    public int getItemCount() {
        return groupQuestionArrayList.size();
    }
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView QuestionNumber, QuestionText;
        ImageView DeleteQuestion;
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            QuestionNumber = itemView.findViewById(R.id.QuestionNumber);
            QuestionText = itemView.findViewById(R.id.QuestionText);
            DeleteQuestion = itemView.findViewById(R.id.DeleteQuestion);
        }
    }
    public void setData(ArrayList<GroupQuestion> newData) {
        this.groupQuestionArrayList = newData;
        // Update QuestionNumber for all items in the new data
        for (int i = 0; i < groupQuestionArrayList.size(); i++) {
            groupQuestionArrayList.get(i).setQuestionNumber(i + 1);
        }
        notifyDataSetChanged();
    }
    public void sortDataByQuestionNumber() {
        Collections.sort(groupQuestionArrayList, new Comparator<GroupQuestion>() {
            @Override
            public int compare(GroupQuestion q1, GroupQuestion q2) {
                return q1.getQuestionNumber() - q2.getQuestionNumber();
            }
        });
        notifyDataSetChanged();
    }
}
