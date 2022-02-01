package com.example.naplanner.model;

import java.util.ArrayList;

public class TasksArray {

    ArrayList<TaskModel> tasks;

    public void setTask(String taskName, int taskType) {
        tasks.add(new TaskModel(taskName, taskType));
    }

    public TaskModel getTask(int position) {
        return tasks.get(position);
    }
}
