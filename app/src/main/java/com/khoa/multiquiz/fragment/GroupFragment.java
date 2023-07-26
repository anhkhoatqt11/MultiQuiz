package com.khoa.multiquiz.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khoa.multiquiz.DuelWaitingIngame;
import com.khoa.multiquiz.GroupCollection;
import com.khoa.multiquiz.GroupQuestion;
import com.khoa.multiquiz.GroupQuestionCreate;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.ThemeLobby;

public class GroupFragment extends Fragment {
    LinearLayout EnterCodeLinearLayout, CreateQuestionLinearLayout, QuestionSetCollectionLinearLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        EnterCodeLinearLayout = rootView.findViewById(R.id.EnterCodeLinearLayout);
        CreateQuestionLinearLayout = rootView.findViewById(R.id.CreateQuestionLinearLayout);
        QuestionSetCollectionLinearLayout = rootView.findViewById(R.id.QuestionSetCollectionLinearLayout);
        CreateQuestionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupQuestionCreate.class);
                startActivity(intent);
            }
        });

        QuestionSetCollectionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupCollection.class);
                startActivity(intent);
            }
        });


        return rootView;
    }
}
