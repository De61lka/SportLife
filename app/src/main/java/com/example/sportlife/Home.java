package com.example.sportlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private static final String TRAININGS_KEY = "trainings";
    private static final int REQUEST_CODE_FULL_TRAINING = 1;
    private static final int REQUEST_CODE_ARMS_TRAINING = 2;
    private static final int REQUEST_CODE_LEGS_TRAINING = 3;
    private static final int REQUEST_CODE_BACK_TRAINING = 4;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private float stepCount = 0;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private TrainingAdapter adapter;
    private Gson gson = new Gson();
    private ProgressBar progressBarSteps;

    private SharedPreferences historySharedPreferences;

    private TextView textViewStepGoal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        stepsOffset = sharedPreferences.getFloat("stepsOffset", 0);
        stepCount = sharedPreferences.getFloat("stepCount", 0);

        historySharedPreferences = getSharedPreferences("StepsHistory", Context.MODE_PRIVATE);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        progressBarSteps = findViewById(R.id.progressBarSteps);
        textViewStepGoal = findViewById(R.id.textViewStepGoal);
        updateStepCount();

        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "No Step Counter Sensor!", Toast.LENGTH_SHORT).show();
        }

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerViewLastWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTrainings();

        ImageView trainingOneImageView = findViewById(R.id.imageViewTrainingOne);
        ImageView trainingTwoImageView = findViewById(R.id.imageViewTrainingTwo);
        ImageView trainingThreeImageView = findViewById(R.id.imageViewTrainingThree);
        ImageView trainingFourImageView = findViewById(R.id.imageViewTrainingFour);

        trainingOneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFullTrainingActivity();
            }
        });

        trainingTwoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startArmsTrainingActivity();
            }
        });

        trainingThreeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLegsTrainingActivity();
            }
        });

        trainingFourImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBackTrainingActivity();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            if (item.getItemId() == R.id.navigation_home) {
                return true;
            } else if (item.getItemId() == R.id.navigation_analytics) {
                intent = new Intent(this, AnalyticsActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.navigation_profile) {
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void loadTrainings() {
        String jsonTrainings = sharedPreferences.getString(TRAININGS_KEY, null);
        Type type = new TypeToken<List<Training>>() {}.getType();
        List<Training> trainings = gson.fromJson(jsonTrainings, type);
        if (trainings == null) {
            trainings = new ArrayList<>();
        }

        adapter = new TrainingAdapter(trainings);
        recyclerView.setAdapter(adapter);
    }

    private void addTrainingToList(String trainingName, int imageResource) {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Training newTraining = new Training(trainingName, currentDate, imageResource);

        String jsonTrainings = sharedPreferences.getString(TRAININGS_KEY, null);
        Type type = new TypeToken<List<Training>>() {}.getType();
        List<Training> trainings = gson.fromJson(jsonTrainings, type);
        if (trainings == null) {
            trainings = new ArrayList<>();
        }

        // Добавляем новую тренировку в начало списка
        trainings.add(0, newTraining);

        if (trainings.size() > 3) {
            trainings.remove(trainings.size() - 1);
        }

        String updatedJsonTrainings = gson.toJson(trainings);
        sharedPreferences.edit().putString(TRAININGS_KEY, updatedJsonTrainings).apply();
    }


    private void updateRecyclerView() {
        loadTrainings();
    }

    private void startFullTrainingActivity() {
        Intent intent = new Intent(Home.this, FullTraining.class);
        startActivityForResult(intent, REQUEST_CODE_FULL_TRAINING);
    }

    private void startArmsTrainingActivity() {
        Intent intent = new Intent(Home.this, ArmsTraining.class);
        startActivityForResult(intent, REQUEST_CODE_ARMS_TRAINING);
    }

    private void startLegsTrainingActivity() {
        Intent intent = new Intent(Home.this, LegsTraining.class);
        startActivityForResult(intent, REQUEST_CODE_LEGS_TRAINING);
    }

    private void startBackTrainingActivity() {
        Intent intent = new Intent(Home.this, BackTraining.class);
        startActivityForResult(intent, REQUEST_CODE_BACK_TRAINING);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FULL_TRAINING:
                    handleTrainingResult(data);
                    break;
                case REQUEST_CODE_ARMS_TRAINING:
                    handleTrainingResult(data);
                    break;
                case REQUEST_CODE_LEGS_TRAINING:
                    handleTrainingResult(data);
                    break;
                case REQUEST_CODE_BACK_TRAINING:
                    handleTrainingResult(data);
                    break;
                default:
                    // Обработка для других requestCode, если необходимо
                    break;
            }
        }
    }
    private void handleTrainingResult(Intent data) {
        if (data != null && data.hasExtra("trainingName") && data.hasExtra("imageResource")) {
            String trainingName = data.getStringExtra("trainingName");
            int imageResource = data.getIntExtra("imageResource", 0);
            addTrainingToList(trainingName, imageResource);
            updateRecyclerView();
        }
    }

    private float stepsOffset = 0;

    private final SensorEventListener stepListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                if (stepsOffset == 0) {
                    // Получите начальное значение, если его еще нет
                    stepsOffset = sharedPreferences.getFloat("stepsOffset", event.values[0]);
                    // Сохраните начальное значение шагов
                    sharedPreferences.edit().putFloat("stepsOffset", stepsOffset).apply();
                }
                // Рассчитываем текущее количество шагов, сделанных сегодня
                stepCount = event.values[0] - stepsOffset;
                // Сохраняем текущее значение шагов
                sharedPreferences.edit().putFloat("stepCount", stepCount).apply();
                updateStepCount();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void updateStepCount() {
        TextView stepView = findViewById(R.id.textViewStepCounter);
        stepView.setText("Шагов: " + (int) stepCount);

        // Обновление ProgressBar
        progressBarSteps.setProgress((int) stepCount);

        // Если достигнута цель, можно показать сообщение пользователю
        if (stepCount >= 10000) {
            Toast.makeText(this, "Цель достигнута! 10000 шагов!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetStepsDaily(); // Сброс шагов, если наступил новый день
        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        updateStepCount(); // Обновите количество шагов и прогресс-бар

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int stepsToday = (int) stepCount;
        historySharedPreferences.edit().putInt(currentDate, stepsToday).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepSensor != null) {
            sensorManager.unregisterListener(stepListener);
        }
    }
    private void resetStepsDaily() {
        SharedPreferences prefs = getSharedPreferences("StepCounterPrefs", MODE_PRIVATE);
        long lastResetTime = prefs.getLong("lastResetTime", System.currentTimeMillis());
        Calendar lastResetCalendar = Calendar.getInstance();
        lastResetCalendar.setTimeInMillis(lastResetTime);
        Calendar now = Calendar.getInstance();

        if (lastResetCalendar.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) ||
                lastResetCalendar.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            // Сброс шагов
            stepsOffset = stepCount; // Теперь stepCount содержит общее количество шагов с момента последнего включения устройства
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastResetTime", System.currentTimeMillis());
            editor.putFloat("stepsOffset", stepsOffset);
            editor.apply();
            stepCount = 0;
            updateStepCount();
        }
    }

}
