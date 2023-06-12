package com.khoa.multiquiz.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.khoa.multiquiz.Login;
import com.khoa.multiquiz.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserFragment extends Fragment {

    TextView userName, joinDate;
    FirebaseUser currentUser;
    Button logoutButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        View rootView = inflater.inflate(R.layout.fragment_userpage, container, false);
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        userName = rootView.findViewById(R.id.userCurrentLogin);
        joinDate = rootView.findViewById(R.id.joinDate);
        logoutButton = rootView.findViewById(R.id.logoutButton);
        currentUser = MAuth.getCurrentUser();

        if (currentUser == null){
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            userName.setText(currentUser.getEmail());
            joinDate.setText("Creation Date: " +  String.valueOf(new Date(currentUser.getMetadata().getCreationTimestamp())));
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MAuth.signOut();
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;




    }
}
