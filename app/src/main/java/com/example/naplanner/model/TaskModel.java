package com.example.naplanner.model;

public class TaskModel {

    int taskId;
    String name;
    typeOfTask taskType;
    boolean completeTask;

    public TaskModel(int taskId, String name, typeOfTask taskType, boolean completeTask) {
        this.taskId = taskId;
        this.name = name;
        this.taskType = taskType;
        this.completeTask = completeTask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public typeOfTask getTypeOfTask() {
        return taskType;
    }

    public void setTypeOfTask(int typeOfTask) {
        this.taskType = taskType;
    }

    public enum typeOfTask {
        LEGENDARY,
        EPIC,
        NORMAL
    }
}
