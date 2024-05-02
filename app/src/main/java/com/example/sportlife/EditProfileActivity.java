package com.example.sportlife;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPhone, editTextEmail, editTextPassword, editTextHeight, editTextWeight;
    private Button buttonSaveChanges;

    private ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imageViewBack = findViewById(R.id.imageViewBack);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void saveChanges() {
        String username = editTextUsername.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim(); // Важно обеспечить безопасность передачи пароля
        String height = editTextHeight.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();

        // Сохранение роста и веса в SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userHeight", height);
        editor.putString("userWeight", weight);
        editor.apply();

        // Отправка данных о пользователе на сервер
        saveUserDataToServer(username, phone, email, password);

        Toast.makeText(EditProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
    }
    private void saveUserDataToServer(String username, String phone, String email, String password) {
        // Получаем user_id, который нужно будет использовать в URL
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);  // Получаем user_id из SharedPreferences
        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_LONG).show();
            return;  // Возвращаемся, если user_id не найден
        }// Убедитесь, что метод getUserId действительно возвращает текущий ID пользователя
        String url = "http://192.168.43.169:5000/update-user/" + userId; // Подставьте сюда правильный URL вашего сервера

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL serverUrl = new URL(url);
                    connection = (HttpURLConnection) serverUrl.openConnection();
                    connection.setRequestMethod("PUT"); // Используем PUT вместо POST
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    JSONObject userData = new JSONObject();
                    userData.put("username", username);
                    userData.put("phone_number", phone); // Используйте ключ, который ожидается на сервере
                    userData.put("email", email);
                    userData.put("password", password); // Важно использовать безопасное соединение (HTTPS)

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(userData.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "User data updated successfully", Toast.LENGTH_LONG).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Failed to update user data", Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Error connecting to the server", Toast.LENGTH_LONG).show());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}