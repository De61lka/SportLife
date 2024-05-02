package com.example.sportlife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {
    private SharedPreferences historySharedPreferences;

    private ImageView chel1;
    private ImageView chel2;
    private ImageView chel3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        chel1 = findViewById(R.id.chel1);
        chel2 = findViewById(R.id.chel2);
        chel3 = findViewById(R.id.chel3);

        // Инициализация SharedPreferences для истории шагов
        historySharedPreferences = getSharedPreferences("StepsHistory", Context.MODE_PRIVATE);

        // Извлечение истории шагов
        List<StepsHistoryItem> stepsHistory = new ArrayList<>();

        // Преобразуем Map<String, ?> в Map<String, Integer>
        for (Map.Entry<String, ?> entry : historySharedPreferences.getAll().entrySet()) {
            if (entry.getValue() instanceof Integer) {
                stepsHistory.add(new StepsHistoryItem(entry.getKey(), (Integer) entry.getValue()));
            }
        }

        // Отображение истории шагов
        RecyclerView recyclerView = findViewById(R.id.recyclerViewStepsHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new StepsHistoryAdapter(stepsHistory));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_analytics);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            if (item.getItemId() == R.id.navigation_home) {
                intent = new Intent(this, Home.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.navigation_analytics) {
                return true;
            } else if (item.getItemId() == R.id.navigation_profile) {
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        chel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Отжимания";
                String description = "Сделай как можно больше отжиманий за минуту!";
                long timeInMilliseconds = 60000; //

                ChallengeDialog challengeDialog = new ChallengeDialog(AnalyticsActivity.this, title, description, timeInMilliseconds);
                challengeDialog.show();
            }
        });

        chel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Спринт";
                String description = "Беги на максимальной скорости в течении 30 секунд!";
                long timeInMilliseconds = 30000;

                ChallengeDialog challengeDialog = new ChallengeDialog(AnalyticsActivity.this, title, description, timeInMilliseconds);
                challengeDialog.show();
            }
        });

        chel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Приседания";
                String description = "45 приседаний за минуту. Справишься?";
                long timeInMilliseconds = 60000;

                ChallengeDialog challengeDialog = new ChallengeDialog(AnalyticsActivity.this, title, description, timeInMilliseconds);
                challengeDialog.show();
            }
        });
    }
}