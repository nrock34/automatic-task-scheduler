package org.nrock.solution_forming;

import java.util.ArrayList;

/**
 * Represents a task with associated penalties and is designed to be added to a static
 * list of solutions.
 *
 * - solutions (ArrayList<SolutionTask>): stores a list of SolutionTask objects.
 *
 * - start_time (int): represents the start time.
 *
 * - end_time (int): represents the end time of a task.
 *
 * - assigned_day (int): represents the day assigned to a task.
 *
 * - due_day_penalty (int): represents a penalty.
 *
 * - pref_day_penalty (int): Represents a penalty.
 *
 * - time_penalty (int): represents a penalty associated with time.
 *
 * - total_penalty (int): Calculates the sum of due_day_penalty, pref_day_penalty,
 * and time_penalty.
 */
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
