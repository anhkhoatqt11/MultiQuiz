package com.khoa.multiquiz.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoa.multiquiz.BottomSheetDialogFragment;
import com.khoa.multiquiz.DuelWaitingIngame;
import com.khoa.multiquiz.GroupCollection;
import com.khoa.multiquiz.GroupQuestion;
import com.khoa.multiquiz.GroupQuestionCreate;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.ThemeLobby;

public class GroupFragment extends Fragment {
    LinearLayout EnterCodeLinearLayout, CreateQuestionLinearLayout, QuestionSetCollectionLinearLayout;
    FirebaseDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        EnterCodeLinearLayout = rootView.findViewById(R.id.EnterCodeLinearLayout);
        CreateQuestionLinearLayout = rootView.findViewById(R.id.CreateQuestionLinearLayout);
        QuestionSetCollectionLinearLayout = rootView.findViewById(R.id.QuestionSetCollectionLinearLayout);

        database = FirebaseDatabase.getInstance();

        CreateQuestionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupQuestionCreate.class);
                startActivity(intent);
            }
        });

        EnterCodeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDialogFragment();
                bottomSheetDialogFragment.show(getChildFragmentManager(), "BottomSheet");
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
