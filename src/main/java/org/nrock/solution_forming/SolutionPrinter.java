package org.nrock.solution_forming;

import com.google.ortools.sat.CpSolverSolutionCallback;
import org.nrock.SchedulerModel;
import org.nrock.Task;

/**
 * Extends CpSolverSolutionCallback to process and record solution callbacks from a
 * constraint programming solver.
 */
public class SolutionPrinter extends CpSolverSolutionCallback{

    private final SchedulerModel model;
    public static int solutions_made = 0;

    public SolutionPrinter(SchedulerModel model) {

        super();
        this.model = model;

    }


    /**
     * Updates a solution object with new task data from the model, incrementing the
     * solution count and creating a new Solution object for each solution made.
     */
    @Override
    public void onSolutionCallback() {

        solutions_made++;

        Solution cur_sol = new Solution(solutions_made);

        for (Task task : model.tasks_to_schedule) {
            int start_time = (int) value(org.nrock.model_resources.StartTime.start_times.get(task.id));
            int end_time = (int) value(org.nrock.model_resources.EndTime.end_times.get(task.id));
            int assigned_day = (int) value(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id));
            int due_day_penalty = (int) value(org.nrock.model_resources.DueDayPenalties.due_day_penalties.get(task.id));
            int pref_day_penalty = (int) value(org.nrock.model_resources.PrefDayPenalties.pref_day_penalties.get(task.id));
            int time_penalty = (int) value(org.nrock.model_resources.TimePenalties.time_penalties.get(task.id));

            cur_sol.addTask(new SolutionTask(
                    start_time, end_time, assigned_day,
                    due_day_penalty, pref_day_penalty, time_penalty
            ));
        }
    }
}
