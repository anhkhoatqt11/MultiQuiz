package com.khoa.multiquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.khoa.multiquiz.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth MAuth;
    TextView textView;
    FirebaseUser currentUser;
    Button logoutButton;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MAuth = FirebaseAuth.getInstance();
//        textView = findViewById(R.id.userCurrentLogin);
        currentUser = MAuth.getCurrentUser();
//        logoutButton = findViewById(R.id.logoutButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                switch (item.getItemId()){
                    case R.id.menu_mainpage_navigation:
                        selectedFragment = new HomeFragment();
                        break;
                    default:
                        return false;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer, selectedFragment).commit();

                return true;
            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer, new HomeFragment()).commit();




        if (currentUser == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }else {
        }

    }
}