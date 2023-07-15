package com.khoa.multiquiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.multiquiz.QuestionTheme;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.Room;

import java.util.ArrayList;

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder> {

    Context context;
    private ArrayList<Room> roomList;
    private OnClickListener onClickListener;

    public LobbyAdapter(Context context, ArrayList<Room> roomList){
        this.context = context;
        this.roomList = roomList;
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
        String RoomID = room.getRoomID();
        String UserCreated = room.getUserUID();
        int NumberOfQuestion = room.getNumberOfQuestion();
        holder.RoomID.setText(RoomID);
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
        TextView RoomID, UserCreated, NumberOfQuestion;

        public LobbyViewHolder(@NonNull View itemView) {
            super(itemView);
            RoomID = itemView.findViewById(R.id.RoomID);
            UserCreated = itemView.findViewById(R.id.UserCreated);
            NumberOfQuestion = itemView.findViewById(R.id.NumberOfQuestion);
        }
    }

}
