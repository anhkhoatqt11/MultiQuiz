package com.khoa.multiquiz.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoa.multiquiz.DuelMatchHistoryLog;
import com.khoa.multiquiz.GroupQuestionSetInfo;
import com.khoa.multiquiz.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {


    public interface OnClickListener {
        void onClick(int position, DuelMatchHistoryLog duelMatchHistoryLog);
    }

    private OnClickListener onClickListener;
    Context context;
    private ArrayList<DuelMatchHistoryLog> duelMatchHistoryLogs;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    CollectionReference collectionReferenceUserData;
    StorageReference storageReferenceAvatar;



    public HistoryAdapter(Context context, ArrayList<DuelMatchHistoryLog> duelMatchHistoryLogs, FirebaseFirestore firestore, FirebaseStorage firebaseStorage){
        this.context = context;
        this.duelMatchHistoryLogs = duelMatchHistoryLogs;
        this.storage = firebaseStorage;
        this.firestore = firestore;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DuelMatchHistoryLog duelMatchHistoryLog = duelMatchHistoryLogs.get(position);
        String OwnerUID = duelMatchHistoryLog.getOwnerUID();
        String OpponentUID = duelMatchHistoryLog.getOpponentUID();
        String OwnerPoint = String.valueOf(duelMatchHistoryLog.getOwnerPoint());
        String OpponentPoint = String.valueOf(duelMatchHistoryLog.getOpponentPoint());

        long ownerPoint = duelMatchHistoryLog.getOwnerPoint();
        long opponentPoint = duelMatchHistoryLog.getOpponentPoint();
        long createdAt = duelMatchHistoryLog.getCreatedAt();

        Date date = new Date(createdAt);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf.format(date);

        collectionReferenceUserData = firestore.collection("UserData");

        if (OwnerUID != null){
            collectionReferenceUserData.document(OwnerUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    holder.OwnerName.setText(documentSnapshot.getString("displayName"));
                }
            });

            storageReferenceAvatar = storage.getReference();
            String childPath = "users/" + OwnerUID + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(holder.OwnerAvatar);
                }
            });
        }

        if (OpponentUID != null){
            collectionReferenceUserData.document(OpponentUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    holder.OwnerName.setText(documentSnapshot.getString("displayName"));
                }
            });

            storageReferenceAvatar = storage.getReference();
            String childPath = "users/" + OpponentUID + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(holder.OpponentAvatar);
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null){
                    onClickListener.onClick(position, duelMatchHistoryLog);
                }
            }
        });
        holder.OwnerPoint.setText(OwnerPoint);
        holder.OpponentPoint.setText(OpponentPoint);

        holder.OwnerCompetitiveResul.setText(ownerPoint > opponentPoint ? "CHIẾN THẮNG" :
                ownerPoint < opponentPoint ? "THUA CUỘC" : "HOÀ");
        holder.OpponentCompetitiveResult.setText(opponentPoint > ownerPoint ? "CHIẾN THẮNG" :
                opponentPoint < ownerPoint ? "THUA CUỘC" : "HOÀ");
        holder.HistoryCreatedDate.setText(String.valueOf(date));
    }

    @Override
    public int getItemCount() {
        return duelMatchHistoryLogs.size();
    }

    public void setOnClickListener(HistoryAdapter.OnClickListener onClickListener ){
        this.onClickListener = onClickListener;
    }
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        ImageView OwnerAvatar, OpponentAvatar;
        TextView OwnerPoint, OwnerName, OwnerCompetitiveResul, OpponentPoint, OpponentName, OpponentCompetitiveResult, HistoryCreatedDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            OwnerAvatar = itemView.findViewById(R.id.OwnerAvatarHistory);
            OpponentAvatar = itemView.findViewById(R.id.OpponentAvatarHistory);
            OwnerPoint = itemView.findViewById(R.id.OwnerPointHistory);
            OwnerName = itemView.findViewById(R.id.OwnerNameHistory);
            OwnerCompetitiveResul = itemView.findViewById(R.id.OwnerCompetitiveResultHistory);
            OpponentPoint = itemView.findViewById(R.id.OpponentPointHistory);
            OpponentName = itemView.findViewById(R.id.OpponentNameHistory);
            OpponentCompetitiveResult = itemView.findViewById(R.id.OpponentCompetitiveResultHistory);
            HistoryCreatedDate = itemView.findViewById(R.id.HistoryCreatedDate);
        }
    }

}
