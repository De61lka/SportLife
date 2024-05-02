package com.example.sportlife;

public class StepsHistoryItem {
    private String date;
    private int steps;

    public StepsHistoryItem(String date, int steps) {
        this.date = date;
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }
}
