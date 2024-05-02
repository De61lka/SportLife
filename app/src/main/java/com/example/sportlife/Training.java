package com.example.sportlife;

public class Training {
    private String name;
    private String lastAccessedDate;
    private int imageResource; // Добавленное поле для хранения ресурса изображения

    public Training(String name, String lastAccessedDate, int imageResource) {
        this.name = name;
        this.lastAccessedDate = lastAccessedDate;
        this.imageResource = imageResource;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastAccessedDate() {
        return lastAccessedDate;
    }

    public void setLastAccessedDate(String lastAccessedDate) {
        this.lastAccessedDate = lastAccessedDate;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
