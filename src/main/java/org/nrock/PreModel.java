package org.nrock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;

/**
 * Provides a framework for scheduling tasks, with features for checking task viability
 * and loading tasks into a schedule. It utilizes the Google OR-Tools library for
 * constraint programming.
 */
public class PreModel {

    //constants
    public static int TIMESLOT_LENGTH = 5;
    static int DAYS_IN_ADAVANCE = 7;
    static IntStream DAYS_AVAILABLE = IntStream.range(0, DAYS_IN_ADAVANCE);
    static IntStream TIMESLOTS = IntStream.range(0, 1440/TIMESLOT_LENGTH);
    public static String cur_day_of_week;


    public ArrayList<Task> tasks_to_schedule = new ArrayList<>();
    HashMap<Integer, Integer> task_weights = new HashMap<>();
    CpModel model = new CpModel();
    CpSolver solver = new CpSolver();
    CpSolverStatus status;

    public PreModel(ArrayList<Task> tasks_to_schedule) {
        this();
        this.tasks_to_schedule.addAll(tasks_to_schedule);

    }
    public PreModel() {
        DAYS_AVAILABLE = IntStream.range(0, DAYS_IN_ADAVANCE);
        TIMESLOTS = IntStream.range(0, 1440/TIMESLOT_LENGTH);
    }

    /**
     * Verifies the viability of a task based on its properties, including due time,
     * preferred start time, due day, duration, and padding, ensuring they adhere to
     * specific constraints and ranges. It returns true if the task is viable and false
     * otherwise.
     *
     * @param task object being checked for viability, containing attributes such as due
     * time, preferred start time, due day, duration, and padding.
     *
     * Destructure `task` to reveal its properties:
     * - `due_time`: represents a time in seconds, must be non-negative
     * - `pref_start`: represents a preferred start time in seconds, must be non-negative
     * when specified
     * - `due_day`: represents a day to complete a task, must be non-negative when specified
     * - `duration`: represents the task duration in seconds, must be non-negative
     *
     * @returns a boolean value indicating task viability, true for viable, false otherwise.
     */
    static public boolean checkTaskViability(Task task) {
       if (task.due_time != -1 && task.pref_start != -1 && task.due_day == 0) {
           if (!(task.pref_start < task.due_time)) return false;
       } else if (task.pref_start != -1) {
           if (!(task.pref_start >= 0)) return false;
       } else if (task.due_day != -1) {
           if (!(task.due_day >= 0)) return false;
       } else if (task.due_time != -1) {
           if (!(task.due_time >= 0 && task.due_time >= task.duration)) return false;
       } else {
           if (!(task.duration >= TIMESLOT_LENGTH)) return false;
           else if (!(task.padding >= 0)) return false;
       }

       return true;
    }

    /**
     * Validates a scheduler model by checking if the total duration of tasks plus padding
     * does not exceed a certain limit, calculated as 1440 minutes multiplied by the
     * number of days in advance.
     *
     * @param model SchedulerModel being checked for validity.
     *
     * @returns a boolean value indicating whether the model's total duration is within
     * the specified limit.
     */
    static public boolean modelValidityCheck(SchedulerModel model) {

        int total_durations=0;
        for (Task task : model.tasks_to_schedule) {
            total_durations += task.duration + task.padding;
        }
        return total_durations <= 1440 * DAYS_IN_ADAVANCE;

    }

    /**
     * Scales task properties by a fixed TIMESLOT_LENGTH. It adjusts duration, due_time,
     * pref_start, and padding to fit within a defined time slot, clamping negative values
     * to -1 and non-zero values to the nearest TIMESLOT_LENGTH.
     *
     * @param task object that is being processed and modified by the function.
     */
    static public void taskProcessor(Task task) {

        task.duration = task.duration < TIMESLOT_LENGTH ? 1 : task.duration/TIMESLOT_LENGTH;
        task.due_time = task.due_time != -1 ? task.due_time / TIMESLOT_LENGTH : -1;
        task.pref_start = task.pref_start != -1 ? task.pref_start / TIMESLOT_LENGTH : -1;
        task.padding = task.padding != 0 ? task.padding / TIMESLOT_LENGTH : 0;

    }

    /**
     * Filters a list of tasks based on their viability, adds viable tasks to a separate
     * list, and then processes each task in the list using the `taskProcessor` method.
     *
     * @param tasks_to_load collection of tasks to be processed and filtered for scheduling.
     */
    public void load_tasks(ArrayList<Task> tasks_to_load) {
        for (Task task : tasks_to_load) {
            if (checkTaskViability(task)) tasks_to_schedule.add(task);
            //System.out.printf("%s   %s   %s %n", task.id, task.duration, task.hasHardDueDate);
        }
        tasks_to_schedule.forEach(PreModel::taskProcessor);
        //tasks_to_schedule.addAll(tasks_to_load);
    }

    /**
     * Processes a given task by calling the `taskProcessor` method and checks its viability
     * with `checkTaskViability`. If viable, the task is added to the `tasks_to_schedule`
     * collection.
     *
     * @param task_to_load task to be processed and potentially added to the `tasks_to_schedule`
     * collection.
     */
    public void load_task(Task task_to_load) {
        taskProcessor(task_to_load);
        if (checkTaskViability(task_to_load)) tasks_to_schedule.add(task_to_load);

    }


}
