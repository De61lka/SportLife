package com.example.sportlife;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepsHistoryAdapter extends RecyclerView.Adapter<StepsHistoryAdapter.StepsHistoryViewHolder> {
    private List<StepsHistoryItem> stepsHistoryList;

    public StepsHistoryAdapter(List<StepsHistoryItem> stepsHistoryList) {
        this.stepsHistoryList = stepsHistoryList;
    }

    @NonNull
    @Override
    public StepsHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_steps_history, parent, false);
        return new StepsHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StepsHistoryViewHolder holder, int position) {
        StepsHistoryItem item = stepsHistoryList.get(position);
        holder.textViewDate.setText(item.getDate());
        holder.textViewSteps.setText(item.getSteps() + " шагов");

        // Установите ширину полоски в зависимости от количества шагов, например, в процентах
        // holder.progressBarSteps.setMax(100); // Вы можете установить максимальное значение по своему усмотрению
        holder.progressBarSteps.setProgress(item.getSteps() * 100 / 10000); // Приведите шаги к заданному максимуму

        // Если достигнута цель, покрасьте полоску в другую цвет
        if (item.getSteps() >= 10000) {
            holder.progressBarSteps.getProgressDrawable().setColorFilter(Color.parseColor("#FF00FF00"), PorterDuff.Mode.SRC_IN); // Зеленый цвет для достижения цели
        } else {
            holder.progressBarSteps.getProgressDrawable().setColorFilter(Color.parseColor("#FF000000"), PorterDuff.Mode.SRC_IN); // Черный цвет по умолчанию
        }
    }

    @Override
    public int getItemCount() {
        return stepsHistoryList.size();
    }

    public class StepsHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;
        private TextView textViewSteps;
        private ProgressBar progressBarSteps;

        public StepsHistoryViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewStepsHistoryDate);
            textViewSteps = itemView.findViewById(R.id.textViewStepsHistorySteps);
            progressBarSteps = itemView.findViewById(R.id.progressBarStepsHistory);
        }
    }
}