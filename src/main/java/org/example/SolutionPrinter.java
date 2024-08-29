package org.example;

import com.google.ortools.sat.CpSolverSolutionCallback;
import org.example.Resources.*;

public class SolutionPrinter extends CpSolverSolutionCallback{

    private final SchedulerModel model;
    static int solutions_made = 0;

    public SolutionPrinter(SchedulerModel model) {

        super();
        this.model = model;

    }


    @Override
    public void onSolutionCallback() {

        solutions_made++;

        Solution cur_sol = new Solution(solutions_made);

        for (Task task : model.tasks_to_schedule) {
            int start_time = (int) value(StartTime.start_times.get(task.id));
            int end_time = (int) value(EndTime.end_times.get(task.id));
            int assigned_day = (int) value(AssignedDay.assigned_days.get(task.id));
            int due_day_penalty = (int) value(DueDayPenalties.due_day_penalties.get(task.id));
            int pref_day_penalty = (int) value(PrefDayPenalties.pref_day_penalties.get(task.id));
            int time_penalty = (int) value(TimePenalties.time_penalties.get(task.id));

            cur_sol.addTask(new SolutionTask(
                    start_time, end_time, assigned_day,
                    due_day_penalty, pref_day_penalty, time_penalty
            ));
        }
    }
}
