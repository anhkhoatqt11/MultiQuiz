package com.khoa.multiquiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khoa.multiquiz.DuelUserAnswerLog;
import com.khoa.multiquiz.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnswerAdapter extends ArrayAdapter<DuelUserAnswerLog> {

    private Context context;
    private ArrayList<DuelUserAnswerLog> duelUserAnswerLogsArrayLists;
    private FirebaseFirestore firestore;

    public AnswerAdapter(Context context, ArrayList<DuelUserAnswerLog> duelUserAnswerLogs, FirebaseFirestore firestore){
        super(context, R.layout.answer_row_item, duelUserAnswerLogs);
        this.context = context;
        this.duelUserAnswerLogsArrayLists = duelUserAnswerLogs;
        this.firestore = firestore;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.answer_row_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.QuestionNumberOfAnswer = convertView.findViewById(R.id.QuestionNumberOfAnswer);
            viewHolder.QuestionTextOfAnswer = convertView.findViewById(R.id.QuestionTextOfAnswer);
            viewHolder.UserAnswer = convertView.findViewById(R.id.UserAnswer);
            viewHolder.CorrectAnswer = convertView.findViewById(R.id.CorrectAnswer);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DuelUserAnswerLog duelUserAnswerLog = duelUserAnswerLogsArrayLists.get(position);
        viewHolder.QuestionNumberOfAnswer.setText(String.valueOf(duelUserAnswerLog.getQuestionNumber()));

        firestore.collection(duelUserAnswerLog.getQuestionThemeID())
                .document(duelUserAnswerLog.getQuestionID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String questionText = documentSnapshot.getString("questionText");
                        viewHolder.QuestionTextOfAnswer.setText(questionText);

                        List<String> answers = (List<String>) documentSnapshot.get("answer");
                        viewHolder.UserAnswer.setText(answers.get(duelUserAnswerLog.getSelectedAnswer()));

                        int correctAnswerNum = Math.toIntExact(documentSnapshot.getLong("correctAnswerID"));
                        viewHolder.CorrectAnswer.setText(answers.get(correctAnswerNum));
                    }
                });
        return convertView;
    }

    // ViewHolder pattern to improve performance by recycling views
    static class ViewHolder {
        TextView QuestionNumberOfAnswer;
        TextView QuestionTextOfAnswer;
        TextView UserAnswer;
        TextView CorrectAnswer;
    }

    public void sortDataByQuestionNumber() {
        Collections.sort(duelUserAnswerLogsArrayLists, new Comparator<DuelUserAnswerLog>() {
            @Override
            public int compare(DuelUserAnswerLog log1, DuelUserAnswerLog log2) {
                return Integer.compare(log1.getQuestionNumber(), log2.getQuestionNumber());
            }
        });
        notifyDataSetChanged();
    }
}