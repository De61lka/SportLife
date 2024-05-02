package com.example.sportlife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPhone, editTextEmail, editTextPassword;
    private CheckBox checkBoxAgree;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()) {
                    registerUser();
                }
            }
        });

        TextView textViewLogin = findViewById(R.id.textViewLogin);
        Spannable spannable = new SpannableString(textViewLogin.getText().toString());
        int startIndex = textViewLogin.getText().toString().indexOf("Войти"); // Индекс начала слова "Войти"
        int endIndex = startIndex + "Войти".length(); // Индекс конца слова "Войти"
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F18B03")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewLogin.setText(spannable);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs() {
        if(editTextUsername.getText().toString().isEmpty() ||
                editTextPhone.getText().toString().isEmpty() ||
                editTextEmail.getText().toString().isEmpty() ||
                editTextPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser() {
        // Получите данные из EditText
        String username = editTextUsername.getText().toString();
        String phoneNumber = editTextPhone.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        // Создайте объект JSONObject с данными
        JSONObject registrationData = new JSONObject();
        try {
            registrationData.put("username", username);
            registrationData.put("phone_number", phoneNumber);
            registrationData.put("email", email);
            registrationData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL сервера для регистрации (замените на ваш настоящий URL)
        String registrationUrl = "http://192.168.43.169:5000/reg";

        // Выполните отправку данных в фоновом потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    // Создание подключения
                    URL url = new URL(registrationUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    // Отправка POST-запроса
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(registrationData.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    // Проверка ответа сервера
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Отображение сообщения и запуск HomeActivity
                                Toast.makeText(MainActivity.this, "Регистрация прошла успешно", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                                finish(); // Закрыть текущую активность
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Ошибка регистрации", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Не удалось соединиться с сервером", Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }
}
