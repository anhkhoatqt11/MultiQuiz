package com.khoa.multiquiz;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

public class BottomSheetDialogFragment extends com.google.android.material.bottomsheet.BottomSheetDialogFragment {

    public static BottomSheetDialogFragment newInstance() {
        return new BottomSheetDialogFragment();
    }

    private BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onButtonClick(String joinCode);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        EditText JoinCode = view.findViewById(R.id.JoinCodeTextInput);

        Button JoinButton = view.findViewById(R.id.JoinButton);

        JoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String JoinCodeValue = JoinCode.getText().toString().trim();
                if (mListener != null) {
                    mListener.onButtonClick(JoinCodeValue);
                }
                dismiss();
            }
        });

        return view;
    }
}
