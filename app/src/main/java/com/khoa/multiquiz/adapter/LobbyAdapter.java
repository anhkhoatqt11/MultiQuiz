package com.khoa.multiquiz.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoa.multiquiz.QuestionTheme;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.Room;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder> {

    Context context;
    private ArrayList<Room> roomList;
    private OnClickListener onClickListener;
    private FirebaseStorage storage;
    public LobbyAdapter(Context context, ArrayList<Room> roomList, FirebaseStorage storage){
        this.context = context;
        this.roomList = roomList;
        this.storage = storage;
    }

    @NonNull
    @Override
    public LobbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_row_item,parent,false);
        return new LobbyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyViewHolder holder, int position) {
        Room room = roomList.get(position);
        String RoomName = room.getRoomName();
        String UserCreated = room.getOwnerCreatedName();
        String OwnerUID = room.getUserUID();
        int NumberOfQuestion = room.getNumberOfQuestion();

        holder.RoomName.setText(RoomName);
        holder.UserCreated.setText(UserCreated);
        holder.NumberOfQuestion.setText(String.valueOf(NumberOfQuestion));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null){
                    onClickListener.onClick(position, room);
                }
            }
        });

        if (OwnerUID != null && !OwnerUID.isEmpty()){
            String avatarPath = "users/" + OwnerUID + "/avatar.jpg";
            StorageReference avatarRef = storage.getReference();
            avatarRef.child(avatarPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(holder.OwnerAvatar);
                }
            });
        }

    }
    public interface OnClickListener {
        void onClick(int position, Room room);
    }

    public void setOnClickListener(LobbyAdapter.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class LobbyViewHolder extends RecyclerView.ViewHolder {
        TextView UserCreated, NumberOfQuestion, RoomName;
        ImageView OwnerAvatar;

        public LobbyViewHolder(@NonNull View itemView) {
            super(itemView);
            RoomName = itemView.findViewById(R.id.RoomName);
            UserCreated = itemView.findViewById(R.id.UserCreated);
            NumberOfQuestion = itemView.findViewById(R.id.NumberOfQuestion);
            OwnerAvatar = itemView.findViewById(R.id.OwnerAvatar);
        }
    }
}
