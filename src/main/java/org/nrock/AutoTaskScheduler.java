package org.nrock;

import com.google.ortools.Loader;
import org.nrock.scheduler_resources.Schedule;
import org.nrock.scheduler_resources.Scheduler;
import org.nrock.solution_forming.Solution;

import java.util.ArrayList;

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

    public boolean tasksLoaded() {
        return this.tasksloaded;
    }

    public void showLog(boolean show_log) {
        showModelLog = show_log;
    }
    public boolean showLog() {
        return showModelLog;
    }

    public void numOfWorkers(int num_of_workers) {
        numOfWorkers = num_of_workers;
    }
    public int numOfWorkers() {
        return numOfWorkers;
    }

    public void sequentialSearch(boolean sequentialSearch) {
        this.sequentialSearch = sequentialSearch;
    }
    public boolean sequentialSearch() {
        return sequentialSearch;
    }

    public void optimizeModel(boolean optimizeModel) {
        this.optimizeModel = optimizeModel;
    }
    public boolean optimizeModel() {
        return optimizeModel;
    }


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


    public Schedule createSchedule(String name) {

        Schedule tempSched = new Schedule(name);
        schedulestobeadded.add(tempSched);
        return tempSched;

    }

//    public Schedule createSchedule(String name, HashMap<String, int[]> schd) {
//        Schedule tempSched = createSchedule(name);
//        return tempSched;
//    }

    public void setDay(Schedule schd, String day, int time) {
        if (!tasksloaded) schd.setDay(day, new int[] { 1440, 0 });
    }

    public void setDay(Schedule schd, String day, int startTime, int endTime) {
        if (!tasksloaded) schd.setDay(day, new int[] { startTime, endTime });
    }

    public void setMonday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("monday", new int[] {1440, 0});
    }

    public void setMonday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("monday", new int[] {startTime, endTime});
    }

    public void setTuesday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("tuesday", new int[] {1440, 0});
    }

    public void setTuesday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("tuesday", new int[] {startTime, endTime});
    }

    public void setWednesday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("wednesday", new int[] {1440, 0});
    }

    public void setWednesday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("wednesday", new int[] {startTime, endTime});
    }

    public void setThursday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("thursday", new int[] {1440, 0});
    }

    public void setThursday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("thursday", new int[] {startTime, endTime});
    }

    public void setFriday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("friday", new int[] {1440, 0});
    }

    public void setFriday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("friday", new int[] {startTime, endTime});
    }

    public void setSaturday(Schedule scd, int time) {
        if (!tasksloaded) scd.setDay("saturday", new int[] {1440, 0});
    }

    public void setSaturday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("saturday", new int[] {startTime, endTime});
    }

    public void setSunday(Schedule scd, int time) {
       if (!tasksloaded) scd.setDay("sunday", new int[] {1440, 0});
    }

    public void setSunday(Schedule scd, int startTime, int endTime) {
        if (!tasksloaded) scd.setDay("sunday", new int[] {startTime, endTime});
    }


    ArrayList<Task> taskscreated = new ArrayList<>();


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

    public void __createTask(String name) {

    }

    public void setTaskCustomSchedule(Task task, Schedule schedule, boolean independentSchedule) {

        if (!tasksloaded) {
            task.addcustomschedule(schedule, independentSchedule);
            schedulestobeadded.remove(schedule);
        }
        else System.out.println("Can not alter tasks after tasks have been loaded");

    }


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

    public void loadSchedules() {

        if (!tasksloaded) {
            for (Schedule schd: schedulestobeadded) {
                Scheduler.setupBlockedTimes(schd, dayOfWeek, false);
                schedulestobeadded.remove(schd);
            }
        }

    }

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

    public void gatherSolutions() {
        gatherSolutions(false);
    }
    public void gatherSolutions(boolean getTopThree) {

        if (!tasksloaded) throw new RuntimeException("Model has not been run yet.");
        if (isRunning) throw new RuntimeException("Model Still Running");
        for (Solution sol : Solution.solutions) {
            System.out.println(sol.id + " penalty:  " + sol.sol_tol_pen);
        }

    }




}
