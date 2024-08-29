package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;

public class PreModel {

    //constants
    static int TIMESLOT_LENGTH = 5;
    static int DAYS_IN_ADAVANCE = 7;
    static IntStream DAYS_AVAILABLE = IntStream.range(0, DAYS_IN_ADAVANCE);
    static IntStream TIMESLOTS = IntStream.range(0, 1440/TIMESLOT_LENGTH);



    ArrayList<Task> tasks_to_schedule = new ArrayList<>();
    HashMap<Integer, Integer> task_weights = new HashMap<>();
    CpModel model = new CpModel();
    CpSolver solver = new CpSolver();
    CpSolverStatus status;

    public PreModel(ArrayList<Task> tasks_to_schedule) {
        this.tasks_to_schedule.addAll(tasks_to_schedule);
    }
    public PreModel() {

    }

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

    static public boolean modelValidityCheck(SchedulerModel model) {

        int total_durations=0;
        for (Task task : model.tasks_to_schedule) {
            total_durations += task.duration + task.padding;
        }
        return total_durations <= 1440 * DAYS_IN_ADAVANCE;

    }

    static public void taskProcessor(Task task) {

        task.duration = task.duration < TIMESLOT_LENGTH ? 1 : task.duration/TIMESLOT_LENGTH;
        task.due_time = task.due_time != -1 ? task.due_time / TIMESLOT_LENGTH : -1;
        task.pref_start = task.pref_start != -1 ? task.pref_start / TIMESLOT_LENGTH : -1;
        task.padding = task.padding != 0 ? task.padding / TIMESLOT_LENGTH : 0;

    }

    public void load_tasks(ArrayList<Task> tasks_to_load) {
        for (Task task : tasks_to_load) {
            if (checkTaskViability(task)) tasks_to_schedule.add(task);
            System.out.printf("%s   %s   %s %n", task.id, task.duration, task.hasHardDueDate);
        }
        tasks_to_schedule.forEach(PreModel::taskProcessor);
        //tasks_to_schedule.addAll(tasks_to_load);
    }

    public void load_task(Task task_to_load) {
        taskProcessor(task_to_load);
        if (checkTaskViability(task_to_load)) tasks_to_schedule.add(task_to_load);

    }


}
