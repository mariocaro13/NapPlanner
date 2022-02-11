package com.example.naplanner.model;

public class TaskModel {

    private int taskId;
    private String name;
    private TaskType taskType;
    private boolean completeTask;

    public TaskModel() {
    }

    public TaskModel(int taskId, String name, TaskType taskType, boolean completeTask) {
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

    public boolean isCompleteTask() {
        return completeTask;
    }

    public void setCompleteTask(boolean completeTask) {
        this.completeTask = completeTask;
    }

    public enum TaskType{
        LEGENDARY,
        EPIC,
        NORMAL
    }
}
