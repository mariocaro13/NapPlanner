package com.example.naplanner.model;

public class TaskModel {

    private int taskId;
    private String name;
    private TaskType taskType;
    private boolean complete;

    public TaskModel() {
    }

    public TaskModel(int taskId, String name, TaskType taskType, boolean complete) {
        this.taskId = taskId;
        this.name = name;
        this.taskType = taskType;
        this.complete = complete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public enum TaskType{
        LEGENDARY,
        EPIC,
        NORMAL
    }
}
