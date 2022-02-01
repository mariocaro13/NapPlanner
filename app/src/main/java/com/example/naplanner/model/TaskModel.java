package com.example.naplanner.model;

public class TaskModel {

    String name;
    /*
     * Legendary tasks will be asociate with 1.
     * Epic tasks will be asociate with 2.
     * Normal tasks will be asociate with 3.
     */
    int typeOfTask;

    public TaskModel(String name, int typeOfTask) {
        this.name = name;
        this.typeOfTask = typeOfTask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeOfTask() {
        return typeOfTask;
    }

    public void setTypeOfTask(int typeOfTask) {
        this.typeOfTask = typeOfTask;
    }
}
