package com.example.sportlife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Login extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        TextView textViewLogin = findViewById(R.id.textViewRegister);
        Spannable spannable = new SpannableString(textViewLogin.getText().toString());
        int startIndex = textViewLogin.getText().toString().indexOf("Зарегистрируйтесь"); // Индекс начала слова "Войти"
        int endIndex = startIndex + "Зарегистрируйтесь".length(); // Индекс конца слова "Зарегистрируйтесь"
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F18B03")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewLogin.setText(spannable);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Отправка данных на сервер
        loginUser(username, password);
    }

    private void loginUser(String username, String password) {
        String url = "http://192.168.43.169:5000/login"; // Замените на актуальный адрес вашего сервера

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL loginUrl = new URL(url);
                    connection = (HttpURLConnection) loginUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    JSONObject credentials = new JSONObject();
                    credentials.put("username", username);
                    credentials.put("password", password);

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(credentials.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        int userId = jsonResponse.getInt("user_id");  // Получаем user_id из JSON ответа

                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", userId);  // Сохраняем user_id
                        editor.apply();

                        UserManager.getInstance().setUsername(username);
                        UserManager.getInstance().setUserId(userId);

                        runOnUiThread(() -> {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(Login.this, "Error connecting to the server", Toast.LENGTH_LONG).show());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}