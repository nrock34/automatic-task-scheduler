package org.example;

import java.util.ArrayList;

public class Task {

    static ArrayList<Task> task_list = new ArrayList<>();
    static int max_weight = 0;
    static int numOfTasks = 0;

    final int id;
    int pref_start;
    int pref_start_day;
    int due_time;
    int duration;
    int due_day;
    int urgency;
    int padding;
    boolean hasHardDueDate;
    boolean hasCustomWeight;


    public Task(int id, int duration, int pref_start, int pref_start_day,
                int due_time, int due_day, int urgency, int padding,
                boolean hasHardDueDate, boolean hasCustomWeight) {

        this.id = id; this.pref_start = pref_start; this.pref_start_day = pref_start_day;
        this.duration = duration; this.due_time = due_time; this.due_day = due_day; this.urgency = urgency;
        this.padding = padding; this.hasHardDueDate = hasHardDueDate; this.hasCustomWeight = hasCustomWeight;

        task_list.add(this);
        numOfTasks++;
        max_weight = Math.max(urgency, max_weight);

    }


}
