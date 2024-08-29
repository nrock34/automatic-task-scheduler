package org.example;

import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.*;
import com.google.ortools.sat.LinearArgument;
import org.example.Resources.*;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchedulerModel extends PreModel{

    ArrayList<ModelVar> model_variables = new ArrayList<>();

    int timeslots_max;
    int days_available_max;

    CpSolverSolutionCallback cb = new SolutionPrinter(this);

    public SchedulerModel(ArrayList<Task> tasks_to_schedule) {
        super(tasks_to_schedule);

    }
    public SchedulerModel() {
        super();
    }

    void initModelVariables() {

        this.timeslots_max = TIMESLOTS.max().orElse(0);
        this.days_available_max = DAYS_AVAILABLE.max().orElse(0);

        for (int i = 0; i < tasks_to_schedule.size(); i++) {
            Task temp_task = tasks_to_schedule.get(i);
            model_variables.add(new StartTime(temp_task.id,
                                this.model.newIntVar(0, timeslots_max,
                                                    String.format("task_%s_start_time", temp_task.id))));
            model_variables.add(new EndTime(temp_task.id,
                                this.model.newIntVar(0, timeslots_max,
                                                    String.format("task_%s_end_time", temp_task.id))));
            model_variables.add(new AssignedDay(temp_task.id,
                                this.model.newIntVar(0, days_available_max,
                                                    String.format("task_%s_assigned_day", temp_task.id))));
            model_variables.add(new DueDayPenalties(temp_task.id, this.model.newIntVar(0,
                                                (long) timeslots_max * days_available_max * Task.max_weight,
                                                    String.format("task_%s_due_day_penalty", temp_task.id))));
            model_variables.add(new PrefDayPenalties(temp_task.id, this.model.newIntVar(0,
                                                (long) timeslots_max * days_available_max * Task.max_weight,
                                                    String.format("task_%s_pref_day_penalty", temp_task.id))));
            model_variables.add(new TimePenalties((temp_task.id), this.model.newIntVar(0,
                                                    (long) timeslots_max * Task.max_weight,
                                                    String.format("task_%s_time_penalty", temp_task.id))));
        }
    }

    void defineModel() {
        for (Task task : tasks_to_schedule) {

            //task must have a endtime = start time + duration
            model.addEquality(EndTime.end_times.get(task.id),
                                LinearExpr.affine(StartTime.start_times.get(task.id), 1, task.duration));

            if (task.hasHardDueDate) {
                //day must be before due date
                model.addLessOrEqual(AssignedDay.assigned_days.get(task.id), task.due_day);
                //boolean set to true only when task is on due date
                BoolVar task_on_due_day = model.newBoolVar(String.format("task_%s_is_on_same_day_as_due_day", task.id));
                model.addEquality(AssignedDay.assigned_days.get(task.id), task.due_day).onlyEnforceIf(task_on_due_day);
                //task must be finished by due time only on day when task is due
                model.addLessOrEqual(EndTime.end_times.get(task.id), task.due_time).onlyEnforceIf(task_on_due_day);

            } else if (task.pref_start_day != -1) {
                //penalty for every day away for prefered day
                model.addAbsEquality(PrefDayPenalties.pref_day_penalties.get(task.id), LinearExpr.sum(new LinearExpr[] {
                                     LinearExpr.term(AssignedDay.assigned_days.get(task.id), (long) task.urgency * timeslots_max),
                                     LinearExpr.term(LinearExpr.constant(task.pref_start_day), -1L * task.urgency * timeslots_max)}));
            } else {
                //penalty for everyday away from zero for the tasks without pref day
                model.addAbsEquality(PrefDayPenalties.pref_day_penalties.get(task.id),
                                    LinearExpr.term(AssignedDay.assigned_days.get(task.id), (long) timeslots_max * task.urgency));
            }
            if (task.pref_start != -1 && !task.hasHardDueDate) {
                //penalty for task with not due date for every minute away from pref time
                model.addAbsEquality(TimePenalties.time_penalties.get(task.id),
                        LinearExpr.sum(new LinearExpr[] {
                                LinearExpr.term(StartTime.start_times.get(task.id), task.urgency),
                                LinearExpr.constant((long) task.pref_start * task.urgency * -1)}));
            } else {
                model.addEquality(TimePenalties.time_penalties.get(task.id), 0);
            }

            for (Task task2 : tasks_to_schedule) {
                if (task.id < task2.id) {
                    BoolVar one_before_two = model.newBoolVar(String.format("%s_before_%s", task.id, task2.id));
                    BoolVar two_before_one = model.newBoolVar(String.format("%s_before_%s", task2.id, task.id));

                    BoolVar same_day = model.newBoolVar(String.format("%s_same_day_as_%s", task.id, task2.id));

                    System.out.printf("%s     %s %n %s     %s", task.id, task2.id, AssignedDay.assigned_days.get(task.id), AssignedDay.assigned_days.get(task2.id));
                    model.addEquality(AssignedDay.assigned_days.get(task.id), AssignedDay.assigned_days.get(task2.id)).onlyEnforceIf(same_day);
                    model.addDifferent(AssignedDay.assigned_days.get(task.id), AssignedDay.assigned_days.get(task2.id)).onlyEnforceIf(same_day.not());

                    model.addGreaterOrEqual(LinearExpr.sum(new LinearArgument[] {
                                            StartTime.start_times.get(task.id), LinearExpr.constant(task.padding * -1)})
                                            , EndTime.end_times.get(task2.id)).onlyEnforceIf(new BoolVar[] {two_before_one, same_day});
                    model.addGreaterOrEqual(LinearExpr.sum(new LinearArgument[] {
                                            StartTime.start_times.get(task2.id), LinearExpr.constant(task2.padding * -1)})
                                            , EndTime.end_times.get(task.id)).onlyEnforceIf(new BoolVar[] {one_before_two, same_day});
                    model.addBoolOr(new Literal[] {two_before_one, one_before_two, same_day.not()});
                }
            }

            for (BlockedTime time : BlockedTime.blocked_times) {
                BoolVar task_before_blocked_time = model.newBoolVar(String.format("%s_before_%s-%s-%s", task.id, time.on_day, time.start_time, time.end_time));
                BoolVar task_after_blocked_time = model.newBoolVar(String.format("%s_after_%s-%s-%s", task.id, time.on_day, time.start_time, time.end_time));

                BoolVar same_day_as_blocked_time = model.newBoolVar(String.format("%s_same_day_as_blocked_%s-%s-%s", task.id, time.on_day, time.start_time, time.end_time));

                model.addEquality(AssignedDay.assigned_days.get(task.id), time.on_day).onlyEnforceIf(same_day_as_blocked_time);
                model.addDifferent(AssignedDay.assigned_days.get(task.id), time.on_day).onlyEnforceIf(same_day_as_blocked_time.not());

                model.addGreaterOrEqual(StartTime.start_times.get(task.id), time.end_time).onlyEnforceIf(new BoolVar[] {task_after_blocked_time, same_day_as_blocked_time});
                model.addLessOrEqual(EndTime.end_times.get(task.id), time.start_time).onlyEnforceIf(new BoolVar[] {task_before_blocked_time, same_day_as_blocked_time});
            }
        }

    }

    LinearArgument gatherPenaltySums(int opt) {

        LinearArgument[] tm_penalties = tasks_to_schedule.stream()
                .map(task -> TimePenalties.time_penalties.get(task.id))
                .toList().toArray(LinearArgument[]::new);
        //LinearExpr time_penalty_sum = LinearExpr.sum(tm_penalties);

        LinearArgument[] pref_day_penalties = tasks_to_schedule.stream()
                .map(task -> PrefDayPenalties.pref_day_penalties.get(task.id))
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

    void firstRun(int opt) {
        solver.getParameters().setLogSearchProgress(true);
        solver.getParameters().setNumWorkers(10);
        solver.getParameters().setCpModelPresolve(true);
        //solver.getParameters().setEnumerateAllSolutions(true);
        solver.getParameters().setMaxTimeInSeconds(600);
        System.out.println(gatherPenaltySums(opt));
        model.minimize(gatherPenaltySums(opt));
        this.status = solver.solveWithSolutionCallback(model, cb);

    }
    void optimizeModel(int opt) {

        solver.getParameters().setLogSearchProgress(true);
        solver.getParameters().setNumWorkers(8);

        model.clearHints();
        for (Task task : tasks_to_schedule) {
            model.addHint(StartTime.start_times.get(task.id), solver.value(StartTime.start_times.get(task.id)));
            model.addHint(EndTime.end_times.get(task.id), solver.value(EndTime.end_times.get(task.id)));
            model.addHint(TimePenalties.time_penalties.get(task.id), solver.value(TimePenalties.time_penalties.get(task.id)));
            model.addHint(AssignedDay.assigned_days.get(task.id), solver.value(AssignedDay.assigned_days.get(task.id)));
            model.addHint(DueDayPenalties.due_day_penalties.get(task.id), solver.value(DueDayPenalties.due_day_penalties.get(task.id)));
        }

        if (opt <= 2) {
            model.minimize(gatherPenaltySums(opt));
        } else {
            model.minimize(LinearExpr.sum(tasks_to_schedule.stream()
                    .map(task -> {return AssignedDay.assigned_days.get(task.id);})
                    .toList().toArray(LinearArgument[]::new)));
        }
        this.status = solver.solveWithSolutionCallback(model, cb);

    }

    public void runModel() {

        if (!modelValidityCheck(this)) return;
        initModelVariables();
        defineModel();
        firstRun(0);
        optimizeModel(1);
        //optimizeModel(2);
//        optimizeModel(3);
//        optimizeModel();
        optimizeModel(0);
    }




}
