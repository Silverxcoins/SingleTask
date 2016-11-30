package com.example.sasha.singletask.choice;

import com.example.sasha.singletask.choice.categoriesRecyclerView.CategoriesItem;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;

import java.util.ArrayList;
import java.util.List;

public class CurrentChoice {

    private int time;
    private List<CategoriesItem> categories;
    private static final CurrentChoice instance;
    private List<TaskDataSet> urgentTasks;
    private List<TaskDataSet> simpleTasks;

    static {
        instance = new CurrentChoice();
    }

    private CurrentChoice() {
        time = 0;
        categories = new ArrayList<>();
    }

    public static CurrentChoice getInstance() { return instance; }

    public int getTime() { return time; }

    public void setTime(int time) { this.time = time; }

    public List<CategoriesItem> getCategories() { return categories; }

    public void setCategories(List<CategoriesItem> categories) { this.categories = categories; }

    public List<TaskDataSet> getUrgentTasks() { return urgentTasks; }

    public void setUrgentTasks(List<TaskDataSet> tasks) { this.urgentTasks = tasks; }

    public List<TaskDataSet> getSimpleTasks() { return simpleTasks; }

    public void setSimpleTasks(List<TaskDataSet> tasks) { this.simpleTasks = tasks; }
}
