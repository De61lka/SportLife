package com.example.sportlife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {
    private List<Training> trainings;

    public TrainingAdapter(List<Training> trainings) {
        this.trainings = trainings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Training training = trainings.get(position);
        holder.textViewName.setText(training.getName());
        holder.textViewDate.setText(training.getLastAccessedDate());
        holder.imageViewTraining.setImageResource(training.getImageResource());
    }


    @Override
    public int getItemCount() {
        return trainings.size();
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDate;
        ImageView imageViewTraining;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewTraining = itemView.findViewById(R.id.imageViewTraining);
        }
    }
}
