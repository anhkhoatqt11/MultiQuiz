package com.khoa.multiquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khoa.multiquiz.fragment.DuelFragment;
import com.khoa.multiquiz.fragment.GroupFragment;
import com.khoa.multiquiz.fragment.HomeFragment;
import com.khoa.multiquiz.fragment.LeaderboardFragment;

public class MainActivity extends AppCompatActivity implements BottomSheetDialogFragment.BottomSheetListener {

    FirebaseAuth MAuth;
    FirebaseUser currentUser;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore firestore;
    FirebaseDatabase database;
    CollectionReference collectionReferenceUserData;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReferenceLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                switch (item.getItemId()){
                    case R.id.menu_mainpage_navigation:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.menu_duel_navigation:
                        selectedFragment = new DuelFragment();
                        break;
                    case R.id.menu_group_navigation:
                        selectedFragment = new GroupFragment();
                        break;
                    case R.id.menu_leaderboard_navigation:_navigation:
                        selectedFragment = new LeaderboardFragment();
                        break;
                    default:
                        return false;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer, selectedFragment).commit();

                return true;
            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.FrameContainer, new HomeFragment()).commit();

        getUserData();


        if (currentUser == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }else {
        }

    }
    private void getUserData(){
        collectionReferenceUserData = firestore.collection("UserData");
        collectionReferenceUserData.document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String displayName = documentSnapshot.getString("displayName");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("displayName", displayName);
                editor.apply();
            }
        });
    }

    @Override
    public void onButtonClick(String joinCode) {
        joinRoom(joinCode);
    }

    private void joinRoom(String joinCode) {
        databaseReferenceLobby = database.getReference().child("GroupLobby").child(joinCode);
        databaseReferenceLobby.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "Mã tham gia hợp lệ", Toast.LENGTH_SHORT).show();
                } else {
                    // The joinCode does not match any room ID, handle this case
                    // For example, show an error message, display an error dialog, etc.
                    Toast.makeText(MainActivity.this, "Mã tham gia không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that may occur during the database operation
                Toast.makeText(MainActivity.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}