package org.nrock;

import org.nrock.model_resources.BlockedTime;
import org.nrock.scheduler_resources.Schedule;
import org.nrock.scheduler_resources.Scheduler;

import java.util.ArrayList;

public class Task {

    static ArrayList<Task> task_list = new ArrayList<>();
    static int max_weight = 0;
    static int numOfTasks = 0;

    String name;
    public final int id;
    int pref_start;
    int pref_start_day;
    int due_time;
    int duration;
    int due_day;
    int urgency;
    int padding;
    Schedule customSchedule;
    ArrayList<BlockedTime> taskBlockedTimes = new ArrayList<>();
    boolean hasHardDueDate;
    boolean hasCustomWeight;
    public boolean hasCustomSchedule;
    boolean bypassRegBlockedTime;


    public Task(int id, int duration, int pref_start, int pref_start_day,
                int due_time, int due_day, int urgency, int padding,
                boolean hasHardDueDate, boolean hasCustomWeight) {

        this.id = id;
        this.pref_start = pref_start;// PreModel.TIMESLOT_LENGTH;
        this.pref_start_day = pref_start_day;
        this.duration = duration;//> PreModel.TIMESLOT_LENGTH ? duration / PreModel.TIMESLOT_LENGTH : PreModel.TIMESLOT_LENGTH;
        this.due_time = due_time;// PreModel.TIMESLOT_LENGTH;
        this.due_day = due_day;
        this.urgency = urgency;
        this.padding = padding;
        this.hasHardDueDate = hasHardDueDate; this.hasCustomWeight = hasCustomWeight;

        task_list.add(this);
        numOfTasks++;
        max_weight = Math.max(urgency, max_weight);

    }

    public Task(int id, int duration, int pref_start, int pref_start_day,
                int due_time, int due_day, int urgency, int padding,
                boolean hasHardDueDate, boolean hasCustomWeight, Schedule customSchedule, boolean bypassRegBlockedTime) {

        this.id = id;
        this.pref_start = pref_start;// PreModel.TIMESLOT_LENGTH;
        this.pref_start_day = pref_start_day;
        this.duration = duration;//> PreModel.TIMESLOT_LENGTH ? duration / PreModel.TIMESLOT_LENGTH : PreModel.TIMESLOT_LENGTH;
        this.due_time = due_time; // PreModel.TIMESLOT_LENGTH;
        this.due_day = due_day;
        this.urgency = urgency;
        this.padding = padding; // PreModel.TIMESLOT_LENGTH;
        this.hasHardDueDate = hasHardDueDate; this.hasCustomWeight = hasCustomWeight;
        this.customSchedule = customSchedule; this.bypassRegBlockedTime = bypassRegBlockedTime;

        this.hasCustomSchedule = true;

        taskBlockedTimes.addAll(Scheduler.setupBlockedTimes(customSchedule, PreModel.cur_day_of_week, true));
        task_list.add(this);
        numOfTasks++;
        max_weight = Math.max(urgency, max_weight);

    }

    public void addcustomschedule(Schedule schedule, boolean bypassRegBlockedTime) {
        this.customSchedule = schedule;
        taskBlockedTimes.addAll(Scheduler.setupBlockedTimes(customSchedule, PreModel.cur_day_of_week, true));
        this.hasCustomSchedule = true;
        this.bypassRegBlockedTime = bypassRegBlockedTime;
    }


}
