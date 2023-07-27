package com.khoa.multiquiz.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LeaderboardAdpter extends RecyclerView.Adapter<LeaderboardAdpter.LeaderboardViewHolder> {


    Context context;
    private ArrayList<User> userArrayList;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    CollectionReference collectionReferenceUserData;
    StorageReference storageReferenceAvatar;


    public LeaderboardAdpter(Context context, ArrayList<User> user, FirebaseFirestore firestore, FirebaseStorage firebaseStorage){
        this.context = context;
        this.userArrayList = user;
        this.storage = firebaseStorage;
        this.firestore = firestore;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item_row,parent,false);
      return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = userArrayList.get(position);
        String UserUID = user.getUserUID();
        String Point = String.valueOf(user.getUserPoint());
        collectionReferenceUserData = firestore.collection("UserData");
        if (UserUID != null){
            collectionReferenceUserData.document(UserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    holder.ParticipantName.setText(documentSnapshot.getString("displayName"));
                }
            });

            storageReferenceAvatar = storage.getReference();
            String childPath = "users/" + UserUID + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(holder.ParticipantAvatar);
                }
            });
        }
        holder.ParticipantPoint.setText(Point);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }


    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        ImageView ParticipantAvatar;
        TextView ParticipantName, ParticipantPoint;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            ParticipantAvatar = itemView.findViewById(R.id.ParticipantAvatar);
            ParticipantName = itemView.findViewById(R.id.ParticipantName);
            ParticipantPoint = itemView.findViewById(R.id.ParticipantPoint);
        }
    }


}
