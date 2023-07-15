package com.khoa.multiquiz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.multiquiz.QuestionTheme;
import com.khoa.multiquiz.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// ...

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    Context context;
    private ArrayList<QuestionTheme> questionThemes;
    private OnClickListener onClickListener;


    public CarouselAdapter(Context context, ArrayList<QuestionTheme> questionThemes) {
        this.context = context;
        this.questionThemes = questionThemes;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questiontheme_row_item, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, @SuppressLint("RecyclerView") int position) {
        QuestionTheme questionTheme = questionThemes.get(position);
        String imageUrl = questionTheme.getLink_image();
        String nameQT = questionTheme.getTheme_name();
        Log.e("STATE", imageUrl);
        Picasso.get().load(imageUrl).into(holder.imageView);
        holder.textView.setText(nameQT);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null){
                    onClickListener.onClick(position, questionTheme);
                }
            }
        });
    }

    @Override
    public int getItemCount() {return questionThemes.size();}

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, QuestionTheme questionTheme);
    }
    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.questiontheme_image);
            textView = itemView.findViewById(R.id.questiontheme_name);
        }
    }
}