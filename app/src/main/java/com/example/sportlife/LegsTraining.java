package com.example.sportlife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LegsTraining extends AppCompatActivity {
    private Button startButton;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;

    private ImageView imageViewBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legs_training);

        startButton = findViewById(R.id.buttonStartTimer);
        timerTextView = findViewById(R.id.textViewTimer);
        imageViewBack = findViewById(R.id.imageViewBack);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTrainingDataToHomeActivity("Тренировка ног", R.drawable.training_three);
                startTimer();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void startTimer() {
        // Отображаем начальное время на таймере
        updateTimer(35 * 60 * 1000); // 35 минут в миллисекундах

        // Создаем и запускаем таймер обратного отсчета на 35 минут
        countDownTimer = new CountDownTimer(35 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Обновляем таймер при каждом тике
                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // Действия при завершении таймера
                timerTextView.setText("00:00:00");

            }
        }.start();
    }

    private void updateTimer(long millisecondsUntilFinished) {
        int seconds = (int) (millisecondsUntilFinished / 1000) % 60;
        int minutes = (int) ((millisecondsUntilFinished / (1000 * 60)) % 60);
        int hours = (int) ((millisecondsUntilFinished / (1000 * 60 * 60)) % 24);

        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Останавливаем таймер при уничтожении активити (например, при завершении приложения)
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    private void sendTrainingDataToHomeActivity(String trainingName, int imageResource) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("trainingName", trainingName);
        resultIntent.putExtra("imageResource", imageResource);
        setResult(RESULT_OK, resultIntent);
    }
}