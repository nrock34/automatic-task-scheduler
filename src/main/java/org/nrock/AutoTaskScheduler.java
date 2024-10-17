package org.nrock;

import com.google.ortools.Loader;
import org.nrock.scheduler_resources.Schedule;
import org.nrock.scheduler_resources.Scheduler;
import org.nrock.solution_forming.Solution;

import java.util.ArrayList;

/**
 * Provides a framework for automating task scheduling and optimization, utilizing a
 * model solver to optimize task placement and minimize penalties. It offers methods
 * for creating schedules and tasks, loading tasks and schedules, and running the
 * model to generate solutions.
 */
public class AutoTaskScheduler {


    static boolean isRunning = false;
    private boolean tasksloaded = false;
    private boolean schedulesloaded = false;
    private boolean modelInError = false;

    private ArrayList<String> log = new ArrayList<String>();

    SchedulerModel model;
    ArrayList<Schedule> schedulestobeadded = new ArrayList<>();


    //model settings

    private int timeslotLength;
    private int daysInAdvance;
    private String dayOfWeek;


    //model solver settings

    private boolean showModelLog;
    private int numOfWorkers;
    private boolean sequentialSearch;
    private boolean optimizeModel;
    private int timeToFind;



    public AutoTaskScheduler(int length_of_timeslot, int days_in_advance, String day_of_week) {

        this.timeslotLength = length_of_timeslot;
        this.daysInAdvance = days_in_advance;
        this.dayOfWeek = day_of_week;

        this.loadModelSettings();
        this.model = new SchedulerModel();

    }

    public AutoTaskScheduler() {
        this(5, 14, "Sunday");
    }

    /**
     * Returns a boolean indicating whether tasks are loaded.
     * It accesses a private variable `tasksloaded` to determine the status.
     *
     * @returns a boolean value representing the status of tasks being loaded.
     */
    public boolean tasksLoaded() {
        return this.tasksloaded;
    }

    /**
     * Sets the value of the `showModelLog` variable based on the input `show_log` boolean
     * parameter.
     *
     * @param show_log new value for the `showModelLog` variable.
     */
    public void showLog(boolean show_log) {
        showModelLog = show_log;
    }
    /**
     * Returns a boolean value indicating the current state of `showModelLog`.
     *
     * @returns a boolean value representing the state of `showModelLog`.
     */
    public boolean showLog() {
        return showModelLog;
    }

    /**
     * Sets the number of workers to the input `num_of_workers`, updating the value of
     * the `numOfWorkers` variable.
     *
     * @param num_of_workers number of workers being assigned to the function, which
     * updates the `numOfWorkers` variable.
     */
    public void numOfWorkers(int num_of_workers) {
        numOfWorkers = num_of_workers;
    }
    /**
     * Returns the value of `numOfWorkers`.
     *
     * @returns the value of the `numOfWorkers` variable.
     */
    public int numOfWorkers() {
        return numOfWorkers;
    }

    /**
     * Sets a boolean flag indicating whether a sequential search should be performed.
     *
     * @param sequentialSearch value to be assigned to the `sequentialSearch` field of
     * the class.
     */
    public void sequentialSearch(boolean sequentialSearch) {
        this.sequentialSearch = sequentialSearch;
    }
    /**
     * Returns a boolean value indicating the result of a sequential search, likely
     * performed elsewhere in the code.
     *
     * @returns a boolean value indicating the result of a sequential search.
     */
    public boolean sequentialSearch() {
        return sequentialSearch;
    }

    /**
     * Updates the `optimizeModel` instance variable to the specified value, allowing the
     * model to be optimized or not based on the boolean input.
     *
     * @param optimizeModel flag that determines whether the model should be optimized.
     */
    public void optimizeModel(boolean optimizeModel) {
        this.optimizeModel = optimizeModel;
    }
    /**
     * Returns a boolean value indicating whether the model is optimized.
     *
     * @returns a boolean value indicating the optimization status, presumably stored in
     * the `optimizeModel` variable.
     */
    public boolean optimizeModel() {
        return optimizeModel;
    }


    /**
     * Validates input parameters for a model, including timeslot length, days in advance,
     * and day of week. It logs warnings or errors if parameters are outside accepted
     * ranges or invalid, and sets a flag to indicate model error status.
     */
    public void loadModelSettings() {
        // to-do: implement checks on these variables to warn about optimization
        if (timeslotLength < 5) {
            this.log.add("[WARN] - Range Warning: Setting the model timeslot length this low can increase run cost and time. Especially with a large amount of tasks.");
        }
        if (timeslotLength > 30 && daysInAdvance <= 14) {
            this.log.add("[WARN] - Inefficient Usage Warning: Using the model for automatic optimization on tasks in 30 minute increments is not recommened. Their may be more efficient ways for your use case");
        }
        if (timeslotLength % 5 != 0 && timeslotLength != 1) {
            this.log.add("[ERROR] - Invalid Variable: Timeslot Length must be a multiple of 5");
            this.modelInError = true;
        }
        if (daysInAdvance < 1) {
            this.log.add("[ERROR] - Invalid Variable: Days In Advance must be greater than 0. You currently have (" + daysInAdvance+")");
            this.modelInError = true;
        }
        if (timeslotLength > 360) {
            this.log.add("[ERROR] - Invalid Timeslot Length: " + timeslotLength + " is too big. Please decrease the model length. x <= 360");
            this.modelInError = true;
        }
        if (daysInAdvance > 28) {
            this.log.add("[WARN] - Large Value Warning: Your Days In Advance is large. Warning that this may cause slower model times. Adjust your timeToFind to compensate");
        }
        boolean containsDay = false;
        for (String day: new String[] {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"}) {
            if (!day.equalsIgnoreCase(dayOfWeek)) {
                continue;
            } else {
                containsDay = true;
                break;
            }
        }
        if (!containsDay) {
            this.log.add("[ERROR] - Incorrect Day Of Week Provided: Make sure day of week is a String equal to one of the following {\"monday\", \"tuesday\", \"wednesday\", \"thursday\", \"friday\", \"saturday\", \"sunday\"} ");
            this.modelInError = true;
        }
        if (!this.modelInError) {
            PreModel.TIMESLOT_LENGTH = timeslotLength;
            PreModel.DAYS_IN_ADAVANCE = daysInAdvance;
            PreModel.cur_day_of_week = dayOfWeek;
        } else {
            System.out.println("[ERROR] - Check Log - Incorrect Variables");
        }
    }


    /**
     * Creates a new `Schedule` object with the specified name, adds it to a collection
     * of schedules to be added, and returns the newly created schedule.
     *
     * @param name name of the schedule being created, which is used to initialize a new
     * `Schedule` object.
     *
     * @returns a Schedule object with the specified name.
     */
    public Schedule createSchedule(String name) {

        Schedule tempSched = new Schedule(name);
        schedulestobeadded.add(tempSched);
        return tempSched;

    }

//    public Schedule createSchedule(String name, HashMap<String, int[]> schd) {
//        Schedule tempSched = createSchedule(name);
//        return tempSched;
//    }

    /**
     * Sets a day in the `Schedule` object with a default time of 8:00 AM if tasks have
     * not been loaded and the day is not already set.
     *
     * @param schd Schedule object on which the day and time are set.
     *
     * @param day day of the week for which the schedule is being set.
     *
     * @param time total minutes in a day to be allocated for the specified day in the schedule.
     */
    public void setDay(Schedule schd, String day, int time) {
        if (!tasksloaded) schd.setDay(day, new int[] { 1440, 0 });
    }

    /**
     * Updates a schedule with a new day and time range, if tasks have not already been
     * loaded. It calls `schd.setDay` with the provided day and a time array containing
     * the start and end times. The function does not modify the schedule if tasks are
     * already loaded.
     *
     * @param schd Schedule object to be modified.
     *
     * @param day day of the week or a specific date for which the schedule is being set.
     *
     * @param startTime start time of the scheduled task.
     *
     * @param endTime end time of a scheduled task for the specified day.
     */
    public void setDay(Schedule schd, String day, int startTime, int endTime) {
        if (!tasksloaded) schd.setDay(day, new int[] { startTime, endTime });
    }

    /**
     * Sets the schedule for Monday if the tasks have not been loaded, by setting the day
     * to "monday" with a default time of 1440 (24 hours) and 0 minutes.
     *
     * @param scd Schedule object that is being modified to set the Monday time.
     *
     * @param time time in minutes past 12:00 AM on Monday.
     */
    public void setMonday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("monday", new int[] {1440, 0});
    }

    /**
     * Sets a schedule for Monday by calling the `setDay` method of the `Schedule` object,
     * if tasks have not already been loaded. It takes a `Schedule` object, start time,
     * and end time as parameters.
     *
     * @param scd Schedule object being updated with the start and end times for Monday.
     *
     * @param startTime start time for the tasks scheduled on Monday.
     *
     * @param endTime end time of a scheduled task on a Monday.
     */
    public void setMonday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("monday", new int[] {startTime, endTime});
    }

    /**
     * Sets the schedule for Tuesday to a full day with no breaks.
     * The function checks if tasks have been loaded before setting the schedule.
     * If not, it initializes the day with a full 24-hour period.
     *
     * @param scd Schedule object that is being updated with the task.
     *
     * @param time time in minutes to be scheduled for Tuesday.
     */
    public void setTuesday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("tuesday", new int[] {1440, 0});
    }

    /**
     * Sets the schedule for Tuesday in a given `Schedule` object, specifying the start
     * and end times. It only performs this action if the `tasksloaded` flag is false.
     * The start and end times are stored as an array within the schedule's Tuesday entry.
     *
     * @param scd Schedule object that is being modified to set the Tuesday schedule.
     *
     * @param startTime start time of a scheduled task on Tuesday, which is stored in the
     * `Schedule` object.
     *
     * @param endTime end time of a scheduled task on Tuesday.
     */
    public void setTuesday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("tuesday", new int[] {startTime, endTime});
    }

    /**
     * Sets the schedule for Wednesday if tasks have not been loaded, initializing it
     * with a default time of 1440 minutes at 0 minutes.
     *
     * @param scd Schedule object, which is used to set the time for Wednesday.
     *
     * @param time time of day in minutes to schedule the task on Wednesday, but it is
     * not utilized within the provided function.
     */
    public void setWednesday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("wednesday", new int[] {1440, 0});
    }

    /**
     * Sets the schedule for Wednesday in the provided `Schedule` object if tasks have
     * not been loaded, and adds times between `startTime` and `endTime` to the schedule.
     *
     * @param scd Schedule object that is being modified to set Wednesday's schedule.
     *
     * @param startTime start time of a scheduled task on Wednesday.
     *
     * @param endTime end time of the scheduled task on Wednesday.
     */
    public void setWednesday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("wednesday", new int[] {startTime, endTime});
    }

    /**
     * Sets the schedule for Thursday to have a start time of 12:00 PM and an end time
     * of 12:00 AM, if tasks have not been loaded.
     *
     * @param scd Schedule object to be modified.
     *
     * @param time time in minutes, but it is not used within the function.
     */
    public void setThursday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("thursday", new int[] {1440, 0});
    }

    /**
     * Sets the schedule for Thursday in the given `Schedule` object, specifying start
     * and end times if tasks have not been loaded.
     *
     * @param scd Schedule object onto which the Thursday schedule is being set.
     *
     * @param startTime start time of the task scheduled for Thursday.
     *
     * @param endTime end of the schedule for Thursday.
     */
    public void setThursday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("thursday", new int[] {startTime, endTime});
    }

    /**
     * Sets a default schedule for Friday if tasks are not already loaded. It calls the
     * `setDay` method on the `Schedule` object to set the day to "friday" with a time
     * of 1440 (24 hours) and 0 minutes.
     *
     * @param scd Schedule object to which the Friday schedule is being set.
     *
     * @param time time in minutes to be scheduled for Friday, however it is not utilized
     * within the provided code snippet.
     */
    public void setFriday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("friday", new int[] {1440, 0});
    }

    /**
     * Sets a schedule for Friday. It checks if tasks have already been loaded, and if
     * not, it sets the start and end times for the day. The schedule is updated with the
     * provided start and end times.
     *
     * @param scd Schedule object on which the day "friday" is being set.
     *
     * @param startTime start time of the task scheduled for Friday.
     *
     * @param endTime end time of a scheduled task on Friday.
     */
    public void setFriday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("friday", new int[] {startTime, endTime});
    }

    /**
     * Sets the Saturday schedule of a given `Schedule` object `scd` to start at 12:00
     * AM and end at 11:59 PM if tasks have not been loaded.
     *
     * @param scd Schedule object to which Saturday's time is being set.
     *
     * @param time specific time to be scheduled on Saturday, but it is not used in the
     * given code snippet.
     */
    public void setSaturday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("saturday", new int[] {1440, 0});
    }

    /**
     * Sets the start and end times for Saturday in a Schedule object if tasks have not
     * been loaded.
     *
     * @param scd Schedule object that is being modified to include Saturday's schedule.
     *
     * @param startTime start time of a task scheduled on a Saturday.
     *
     * @param endTime end time of a scheduled task on a Saturday.
     */
    public void setSaturday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("saturday", new int[] {startTime, endTime});
    }

    /**
     * Sets the Sunday schedule in the `Schedule` object `scd` to 1440 minutes and 0 hours
     * if the tasks have not been loaded, indicated by the `tasksloaded` flag being false.
     *
     * @param scd Schedule object that gets updated by the function.
     *
     * @param time time in minutes, which is used to populate the second element of the
     * array passed to `scd.setDay("sunday", new int[] {1440, 0})`.
     */
    public void setSunday(Schedule scd, int time) {
       if (!tasksloaded) scd.setDay("sunday", new int[] {1440, 0});
    }

    /**
     * Sets the schedule for Sunday in the `Schedule` object, assigning start and end
     * times if tasks have been loaded and the schedule is not already set.
     *
     * @param scd object being manipulated to set the Sunday schedule.
     *
     * @param startTime start time of a scheduled task on Sunday.
     *
     * @param endTime end time of a scheduled task on Sunday.
     */
    public void setSunday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("sunday", new int[] {startTime, endTime});
    }


    ArrayList<Task> taskscreated = new ArrayList<>();


    /**
     * Creates a new task object and adds it to the `taskscreated` collection if no tasks
     * have been loaded, otherwise it prints an error message and returns null.
     *
     * @param name name of the task being created, but it is not used within the function.
     *
     * @param id unique identifier for the task being created.
     *
     * @param duration length of time required to complete a task.
     *
     * @param pref_start preferred start time of the task.
     *
     * @param pref_start_day day of the preferred start time of the task.
     *
     * @param due_time time of day when the task is due.
     *
     * @param due_day day of the month when a task is due.
     *
     * @param urgency priority level of the task, influencing its scheduling.
     *
     * @param padding additional time added to the task duration.
     *
     * @param hasHardDueDate presence of a hard due date for the task, indicating whether
     * the task has a strict deadline.
     *
     * @param hasCustomWeight existence of a custom weight for a task.
     *
     * @returns either a Task object or null.
     *
     * The returned output is a Task object.
     */
    public Task createTask(String name, int id, int duration, int pref_start, int pref_start_day,
                           int due_time, int due_day, int urgency, int padding,
                           boolean hasHardDueDate, boolean hasCustomWeight) {
        if (!tasksloaded) {
            Task temptask = new Task(id, duration, pref_start, pref_start_day, due_time,
                    due_day, urgency, padding, hasHardDueDate, hasCustomWeight);

            taskscreated.add(temptask);

            return temptask;
        } else {
            System.out.println("Can not create tasks after tasks have been loaded");
            return null;
        }
    }

    /**
     * Appears to create a task with a specified name, but its exact functionality is
     * unclear due to its incomplete implementation.
     *
     * @param name task name that will be created.
     */
    public void __createTask(String name) {

    }

    /**
     * Adds a custom schedule to a task when tasks have not been loaded, otherwise it
     * prints an error message. The schedule is removed from a list of schedules to be added.
     *
     * @param task task that is being assigned a custom schedule.
     *
     * @param schedule custom schedule to be added to the specified `Task` object.
     *
     * @param independentSchedule nature of the schedule, indicating whether it is
     * independent of other schedules.
     */
    public void setTaskCustomSchedule(Task task, Schedule schedule, boolean independentSchedule) {

        if (!tasksloaded) {
            task.addcustomschedule(schedule, independentSchedule);
            schedulestobeadded.remove(schedule);
        }
        else System.out.println("Can not alter tasks after tasks have been loaded");

    }


    /**
     * Loads tasks into a model, clearing the task list upon successful loading, and logs
     * a warning if the task list exceeds 60 tasks. It prevents task loading after the
     * initial load.
     */
    public void loadTasks() {

        if (taskscreated.size() > 60) {
            this.log.add("[WARN] - Excess Tasks: To maintain the speed this model provides it's recommended to stay below or at 60 tasks. More tasks than this can increase run times exponentially.");

        }
        if (!tasksloaded) {
            model.load_tasks(taskscreated);
            tasksloaded = true;
            taskscreated.clear();
        } else System.out.println("[ERROR] - Can not load tasks after tasks have been loaded");

    }

    /**
     * Loads schedules from a list of schedules to be added (`schedulestobeadded`) and
     * sets up blocked times for each schedule using the `Scheduler.setupBlockedTimes` method.
     */
    public void loadSchedules() {

        if (!tasksloaded) {
            for (Schedule schd: schedulestobeadded) {
                Scheduler.setupBlockedTimes(schd, dayOfWeek, false);
                schedulestobeadded.remove(schd);
            }
        }

    }

    /**
     * Runs the model when tasks are loaded and the model is not currently running, or
     * loads native libraries and runs the model if it is not running and tasks are loaded.
     */
    public void runModel() {

        if (tasksloaded || !isRunning) {
            Loader.loadNativeLibraries();
            isRunning = true;
            model.runModel();
            isRunning = false;
        } else {
            throw new RuntimeException("Make sure Tasks are loaded and the Model isn't running already");
        }

    }

    /**
     * Calls itself recursively with a default parameter value of `false`.
     */
    public void gatherSolutions() {
        gatherSolutions(false);
    }
    /**
     * Prints the IDs and corresponding penalty values of all solutions in the `solutions`
     * collection to the console, and
     * throws runtime exceptions if tasks have not been loaded or if the model is still
     * running.
     *
     * @param getTopThree option to gather only the top three solutions, although it does
     * not appear to be used within the function.
     */
    public void gatherSolutions(boolean getTopThree) {

        if (!tasksloaded) throw new RuntimeException("Model has not been run yet.");
        if (isRunning) throw new RuntimeException("Model Still Running");
        for (Solution sol : Solution.solutions) {
            System.out.println(sol.id + " penalty:  " + sol.sol_tol_pen);
        }

    }




}
