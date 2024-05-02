package com.example.sportlife;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChallengeDialog extends Dialog {

    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewTimer;
    private Button buttonStartTimer;

    private String title;
    private String description;
    private long timeLeftMilliseconds;

    private CountDownTimer countDownTimer;

    public ChallengeDialog(Context context, String title, String description, long timeLeftMilliseconds) {
        super(context);
        this.title = title;
        this.description = description;
        this.timeLeftMilliseconds = timeLeftMilliseconds;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_challenge);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewTimer = findViewById(R.id.textViewTimer);
        buttonStartTimer = findViewById(R.id.buttonStartTimer);

        textViewTitle.setText(title);
        textViewDescription.setText(description);

        updateTimerText();

        buttonStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMilliseconds = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                textViewTimer.setText("Время вышло!");
            }
        };
        countDownTimer.start();
    }

    private void updateTimerText() {
        long seconds = timeLeftMilliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;

        textViewTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }
}