package com.example.naplanner.model;

public class TaskModel {

    private int taskId;
    private String name;
    private typeOfTask taskType;
    private boolean completeTask;

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

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public typeOfTask getTaskType() {
        return taskType;
    }

    public void setTaskType(typeOfTask taskType) {
        this.taskType = taskType;
    }

    public boolean isCompleteTask() {
        return completeTask;
    }

    public void setCompleteTask(boolean completeTask) {
        this.completeTask = completeTask;
    }


}
