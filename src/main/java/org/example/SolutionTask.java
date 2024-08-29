package org.example;

import java.util.ArrayList;

public class SolutionTask {

    static ArrayList<SolutionTask> solutions = new ArrayList<>();

    final int start_time;
    final int end_time;
    final int assigned_day;
    final int due_day_penalty;
    final int pref_day_penalty;
    final int time_penalty;
    final int total_penalty;

    public SolutionTask(int start_time, int end_time,
                    int assigned_day, int due_day_penalty,
                    int pref_day_penalty, int time_penalty) {

        this.start_time = start_time;
        this.end_time = end_time;
        this.assigned_day = assigned_day;
        this.due_day_penalty = due_day_penalty;
        this.pref_day_penalty = pref_day_penalty;
        this.time_penalty = time_penalty;
        this.total_penalty = pref_day_penalty + due_day_penalty + time_penalty;

        solutions.add(this);

    }

}
