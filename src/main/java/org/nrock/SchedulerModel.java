package org.nrock;

import com.google.ortools.sat.*;
import com.google.ortools.sat.LinearArgument;
import org.nrock.model_resources.AssignedDay;
import org.nrock.model_resources.BlockedTime;
import org.nrock.model_resources.EndTime;
import org.nrock.model_resources.StartTime;
import org.nrock.solution_forming.SolutionPrinter;

import java.util.ArrayList;

/**
 * Defines a scheduling model using the Google OR-Tools library, allowing for
 * optimization of task assignments across days and time slots.
 * It uses a linear programming approach to minimize penalties based on task preferences
 * and constraints.
 */
public class SchedulerModel extends PreModel{

    ArrayList<org.nrock.model_resources.ModelVar> model_variables = new ArrayList<>();

    int timeslots_max;
    int days_available_max;

    CpSolverSolutionCallback cb = new SolutionPrinter(this);

    public SchedulerModel(ArrayList<Task> tasks_to_schedule) {
        super(tasks_to_schedule);

    }
    public SchedulerModel() {
        super();
    }

    /**
     * Initializes model variables for tasks to be scheduled, including start and end
     * times, assigned days, and penalty variables for due day, preferred day, and time
     * violations.
     */
    void initModelVariables() {

        this.timeslots_max = TIMESLOTS.max().orElse(0);
        this.days_available_max = DAYS_AVAILABLE.max().orElse(0);

        for (int i = 0; i < tasks_to_schedule.size(); i++) {
            Task temp_task = tasks_to_schedule.get(i);
            model_variables.add(new org.nrock.model_resources.StartTime(temp_task.id,
                                this.model.newIntVar(0, timeslots_max,
                                                    String.format("task_%s_start_time", temp_task.id))));
            model_variables.add(new org.nrock.model_resources.EndTime(temp_task.id,
                                this.model.newIntVar(0, timeslots_max,
                                                    String.format("task_%s_end_time", temp_task.id))));
            model_variables.add(new org.nrock.model_resources.AssignedDay(temp_task.id,
                                this.model.newIntVar(0, days_available_max,
                                                    String.format("task_%s_assigned_day", temp_task.id))));
            model_variables.add(new org.nrock.model_resources.DueDayPenalties(temp_task.id, this.model.newIntVar(0,
                                                (long) timeslots_max * days_available_max * Task.max_weight,
                                                    String.format("task_%s_due_day_penalty", temp_task.id))));
            model_variables.add(new org.nrock.model_resources.PrefDayPenalties(temp_task.id, this.model.newIntVar(0,
                                                (long) timeslots_max * days_available_max * Task.max_weight,
                                                    String.format("task_%s_pref_day_penalty", temp_task.id))));
            model_variables.add(new org.nrock.model_resources.TimePenalties((temp_task.id), this.model.newIntVar(0,
                                                    (long) timeslots_max * Task.max_weight,
                                                    String.format("task_%s_time_penalty", temp_task.id))));
        }
    }

    /**
     * Defines a scheduling model by adding constraints to a linear or integer program.
     * It models task scheduling with various constraints, such as due dates, preferred
     * start days, time penalties, and blocked time intervals.
     */
    void defineModel() {
        for (Task task : tasks_to_schedule) {

            //task must have a endtime = start time + duration
            model.addEquality(org.nrock.model_resources.EndTime.end_times.get(task.id),
                                LinearExpr.affine(org.nrock.model_resources.StartTime.start_times.get(task.id), 1, task.duration));

            if (task.hasHardDueDate) {
                //day must be before due date
                model.addLessOrEqual(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), task.due_day);
                //boolean set to true only when task is on due date
                BoolVar task_on_due_day = model.newBoolVar(String.format("task_%s_is_on_same_day_as_due_day", task.id));
                model.addEquality(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), task.due_day).onlyEnforceIf(task_on_due_day);
                //task must be finished by due time only on day when task is due
                model.addLessOrEqual(org.nrock.model_resources.EndTime.end_times.get(task.id), task.due_time).onlyEnforceIf(task_on_due_day);

            } else if (task.pref_start_day != -1) {
                //penalty for every day away for prefered day
                model.addAbsEquality(org.nrock.model_resources.PrefDayPenalties.pref_day_penalties.get(task.id), LinearExpr.sum(new LinearExpr[] {
                                     LinearExpr.term(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), (long) task.urgency * timeslots_max),
                                     LinearExpr.term(LinearExpr.constant(task.pref_start_day), -1L * task.urgency * timeslots_max)}));
            } else {
                //penalty for everyday away from zero for the tasks without pref day
                model.addAbsEquality(org.nrock.model_resources.PrefDayPenalties.pref_day_penalties.get(task.id),
                                    LinearExpr.term(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), (long) timeslots_max * task.urgency));
            }
            if (task.pref_start != -1 && !task.hasHardDueDate) {
                //penalty for task with not due date for every minute away from pref time
                model.addAbsEquality(org.nrock.model_resources.TimePenalties.time_penalties.get(task.id),
                        LinearExpr.sum(new LinearExpr[] {
                                LinearExpr.term(org.nrock.model_resources.StartTime.start_times.get(task.id), task.urgency),
                                LinearExpr.constant((long) task.pref_start * task.urgency * -1)}));
            } else {
                model.addEquality(org.nrock.model_resources.TimePenalties.time_penalties.get(task.id), 0);
            }

            for (Task task2 : tasks_to_schedule) {
                if (task.id < task2.id) {
                    BoolVar one_before_two = model.newBoolVar(String.format("%s_before_%s", task.id, task2.id));
                    BoolVar two_before_one = model.newBoolVar(String.format("%s_before_%s", task2.id, task.id));

                    BoolVar same_day = model.newBoolVar(String.format("%s_same_day_as_%s", task.id, task2.id));

                    //System.out.printf("%s     %s %n %s     %s", task.id, task2.id, org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), org.nrock.model_resources.AssignedDay.assigned_days.get(task2.id));
                    model.addEquality(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), org.nrock.model_resources.AssignedDay.assigned_days.get(task2.id)).onlyEnforceIf(same_day);
                    model.addDifferent(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), org.nrock.model_resources.AssignedDay.assigned_days.get(task2.id)).onlyEnforceIf(same_day.not());

                    model.addGreaterOrEqual(LinearExpr.sum(new LinearArgument[] {
                                            org.nrock.model_resources.StartTime.start_times.get(task.id), LinearExpr.constant(task.padding * -1)})
                                            , org.nrock.model_resources.EndTime.end_times.get(task2.id)).onlyEnforceIf(new BoolVar[] {two_before_one, same_day});
                    model.addGreaterOrEqual(LinearExpr.sum(new LinearArgument[] {
                                            org.nrock.model_resources.StartTime.start_times.get(task2.id), LinearExpr.constant(task2.padding * -1)})
                                            , org.nrock.model_resources.EndTime.end_times.get(task.id)).onlyEnforceIf(new BoolVar[] {one_before_two, same_day});
                    model.addBoolOr(new Literal[] {two_before_one, one_before_two, same_day.not()});
                }
            }


            if(!task.bypassRegBlockedTime) {
                for (org.nrock.model_resources.BlockedTime time : org.nrock.model_resources.BlockedTime.blocked_times) {
                    BoolVar task_before_blocked_time = model.newBoolVar(String.format("%s_before_%s-%s-%s", task.id, time.on_day, time.start_time, time.end_time));
                    BoolVar task_after_blocked_time = model.newBoolVar(String.format("%s_after_%s-%s-%s", task.id, time.on_day, time.start_time, time.end_time));

                    BoolVar same_day_as_blocked_time = model.newBoolVar(String.format("%s_same_day_as_blocked_%s-%s-%s", task.id, time.on_day, time.start_time, time.end_time));

                    //model.addModuloEquality(LinearExpr.constant(time.on_day), org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), 7).onlyEnforceIf(same_day_as_blocked_time);
                    IntVar tempSchedDayModuloVar = model.newIntVar(0, days_available_max, "tempsched_bypass_sec_+" + task.id + time.on_day);
                    model.addModuloEquality(tempSchedDayModuloVar, AssignedDay.assigned_days.get(task.id),  7);
                    model.addEquality(LinearExpr.constant(time.on_day), tempSchedDayModuloVar).onlyEnforceIf(same_day_as_blocked_time);
                    model.addDifferent(tempSchedDayModuloVar, time.on_day).onlyEnforceIf(same_day_as_blocked_time.not());

                    model.addGreaterOrEqual(org.nrock.model_resources.StartTime.start_times.get(task.id), time.end_time).onlyEnforceIf(new BoolVar[]{task_after_blocked_time, same_day_as_blocked_time});
                    model.addLessOrEqual(org.nrock.model_resources.EndTime.end_times.get(task.id), time.start_time).onlyEnforceIf(new BoolVar[]{task_before_blocked_time, same_day_as_blocked_time});

                    model.addBoolOr(new Literal[] {task_before_blocked_time, task_after_blocked_time, same_day_as_blocked_time.not()});
                }
            } else if (task.hasCustomSchedule) {
                for (BlockedTime time : task.taskBlockedTimes) {
                    BoolVar task_before_blocked_time = model.newBoolVar(String.format("%s_before_%s-%s-%s_for_task", task.id, time.on_day, time.start_time, time.end_time));
                    BoolVar task_after_blocked_time = model.newBoolVar(String.format("%s_after_%s-%s-%s_for_task", task.id, time.on_day, time.start_time, time.end_time));

                    BoolVar same_day_as_blocked_time = model.newBoolVar(String.format("%s_same_day_as_blocked_%s-%s-%s_for_task", task.id, time.on_day, time.start_time, time.end_time));



                    IntVar tempSchedDayModuloVar = model.newIntVar(0, days_available_max, "tempcustomsched_sec_+" + task.id + time.on_day);
                    model.addModuloEquality(tempSchedDayModuloVar, AssignedDay.assigned_days.get(task.id), 7 );
                    model.addEquality(LinearExpr.constant(time.on_day), tempSchedDayModuloVar).onlyEnforceIf(same_day_as_blocked_time);
                    model.addDifferent(tempSchedDayModuloVar, time.on_day).onlyEnforceIf(same_day_as_blocked_time.not());

                    model.addGreaterOrEqual(StartTime.start_times.get(task.id), time.end_time).onlyEnforceIf(new BoolVar[]{task_after_blocked_time, same_day_as_blocked_time});
                    model.addLessOrEqual(EndTime.end_times.get(task.id), time.start_time).onlyEnforceIf(new BoolVar[]{task_before_blocked_time, same_day_as_blocked_time});

                    model.addBoolOr(new Literal[] {task_before_blocked_time, task_after_blocked_time, same_day_as_blocked_time.not()});
                }
            }

        }

    }

    /**
     * Calculates the sum of time penalties and preference day penalties for a set of
     * tasks. It returns the total sum based on the provided option, which can be either
     * the sum of both penalties or one of them.
     *
     * @param opt option to select the type of penalties to be summed, with values 0, 1,
     * and 2 corresponding to total, time, and preference day penalties, respectively.
     *
     * @returns a sum of penalty values, either total penalties or task-specific penalties.
     *
     * Returns an array of two LinearArgument values.
     */
    LinearArgument gatherPenaltySums(int opt) {

        LinearArgument[] tm_penalties = tasks_to_schedule.stream()
                .map(task -> org.nrock.model_resources.TimePenalties.time_penalties.get(task.id))
                .toList().toArray(LinearArgument[]::new);
        //LinearExpr time_penalty_sum = LinearExpr.sum(tm_penalties);

        LinearArgument[] pref_day_penalties = tasks_to_schedule.stream()
                .map(task -> org.nrock.model_resources.PrefDayPenalties.pref_day_penalties.get(task.id))
                .toList()
                .toArray(LinearArgument[]::new);

        if (opt == 0) {
            return LinearExpr.sum(new LinearArgument[]{
                    LinearExpr.sum(tm_penalties),
                    LinearExpr.sum(pref_day_penalties)
            });
        } else if (opt == 1) {
            return LinearExpr.sum(tm_penalties);
        } else if (opt == 2) {
            return LinearExpr.sum(pref_day_penalties);
        } else {
            return LinearExpr.sum(new LinearArgument[]{
                    LinearExpr.sum(tm_penalties),
                    LinearExpr.sum(pref_day_penalties)
            });
        }
    }

    /**
     * Configures solver parameters for optimization, sets up a model for minimization,
     * and solves it using a callback function to track the status.
     *
     * @param opt option or configuration that influences the behavior of the `gatherPenaltySums`
     * method, which is used to calculate the penalty sums for the model.
     */
    void firstRun(int opt) {
        solver.getParameters().setLogSearchProgress(true);
        //solver.getParameters().setNumWorkers(10);
        solver.getParameters().setCpModelPresolve(true);
        solver.getParameters().setEnumerateAllSolutions(true);
        solver.getParameters().setMaxTimeInSeconds(60);
        //System.out.println(gatherPenaltySums(opt));
        model.minimize(gatherPenaltySums(opt));
        this.status = solver.solveWithSolutionCallback(model, cb);

    }
    /**
     * Clears model hints, adds task hints based on task attributes, minimizes the model
     * objective, and solves the model using a solver. The objective function changes
     * based on the `opt` parameter, and the solver's behavior changes based on the
     * `lastrn` parameter.
     *
     * @param opt optimization objective, determining the specific penalty to be minimized.
     *
     * @param lastrn condition that determines whether the solver should be passed a
     * callback object (`cb`) when solving the model.
     */
    void optimizeModel(int opt, boolean lastrn) {

        model.clearHints();
        for (Task task : tasks_to_schedule) {
            model.addHint(org.nrock.model_resources.StartTime.start_times.get(task.id), solver.value(org.nrock.model_resources.StartTime.start_times.get(task.id)));
            model.addHint(org.nrock.model_resources.EndTime.end_times.get(task.id), solver.value(org.nrock.model_resources.EndTime.end_times.get(task.id)));
            model.addHint(org.nrock.model_resources.TimePenalties.time_penalties.get(task.id), solver.value(org.nrock.model_resources.TimePenalties.time_penalties.get(task.id)));
            model.addHint(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id), solver.value(org.nrock.model_resources.AssignedDay.assigned_days.get(task.id)));
            model.addHint(org.nrock.model_resources.DueDayPenalties.due_day_penalties.get(task.id), solver.value(org.nrock.model_resources.DueDayPenalties.due_day_penalties.get(task.id)));
        }

        if (opt <= 2) {
            model.minimize(gatherPenaltySums(opt));
        } else {
            model.minimize(LinearExpr.sum(tasks_to_schedule.stream()
                    .map(task -> {return org.nrock.model_resources.AssignedDay.assigned_days.get(task.id);})
                    .toList().toArray(LinearArgument[]::new)));
        }


        if (lastrn) {
            this.status = solver.solve(model, cb);
        } else {
            this.status = solver.solve(model);
        }

    }

    /**
     * Executes a series of model-related operations when called, including validity
     * checks, initialization, definition, first run, and optimization. The optimization
     * process is performed multiple times with varying parameters.
     */
    public void runModel() {

        if (!modelValidityCheck(this)) return;
        initModelVariables();
        defineModel();
        firstRun(0);
        optimizeModel(1, false);
        //optimizeModel(2);
        //optimizeModel(3);
//        optimizeModel();
        optimizeModel(0, true);
    }




}
