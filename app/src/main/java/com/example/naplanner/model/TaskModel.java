package com.example.naplanner.model;

public class TaskModel {

    private int id;
    private String name;
    private TaskType type;
    private boolean complete;

    public TaskModel() {
    }

    public TaskModel(int id, String name, TaskType type, boolean complete) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.complete = complete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
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
