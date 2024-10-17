package org.nrock.scheduler_resources;

import org.nrock.PreModel;
import org.nrock.model_resources.BlockedTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages schedules and blocked times for tasks, utilizing a static list of schedulers
 * and schedule objects.
 */
public class Scheduler {

    public static ArrayList<Scheduler> schedulers = new ArrayList<Scheduler>();

    public ArrayList<Schedule> schedules = new ArrayList<Schedule>();

    int days;
    int timeslot_increment;
    String current_day_of_week = PreModel.cur_day_of_week;

    public Scheduler(String name, int days, int timeslot_increment) {
        this.days = days;
        this.timeslot_increment = timeslot_increment;

        schedulers.add(this);
    }

    public Scheduler(String name) {
        this.days = 28;
        this.timeslot_increment = 5;

        schedulers.add(this);

    }


    /**
     * Adds a new schedule to a collection and populates its days with data from a provided
     * HashMap, where each key corresponds to a day and its value is an array of integers.
     *
     * @param name name of the schedule that is being created and added to the `schedules`
     * collection.
     *
     * @param day_list schedule for each day, with keys being the day names and values
     * being arrays of integers representing the schedule for that day.
     */
    public void createSchedule(String name, HashMap<String, int[]> day_list) {

        this.schedules.add(new Schedule(name));

        for (String k : day_list.keySet()) {
            schedules.get(-1).setDay(k, day_list.get(k));
        }

    }

    /**
     * Processes a schedule by iterating over days and times, creating `BlockedTime`
     * objects based on availability. It considers the current day of the week, shifting
     * days to account for the difference, and creates blocked times for each day with
     * corresponding start and end times.
     *
     * @param schedule data structure containing the list of days and available times for
     * each day.
     *
     * Contain a list of days of the week (`list_of_days`) and a mapping of day names to
     * day numbers (`daylist`).
     *
     * @param forTask purpose for which blocked times are being created, with its value
     * determining the type of blocked time object instantiated.
     */
    public void setupBlockedTimes(Schedule schedule, boolean forTask) {

        HashMap<String, int[]> avail_times_for_day = schedule.daylist;
        HashMap<String, Integer> daytonum = new HashMap<String, Integer>();

        int idx = 0;
        for (String d : Schedule.list_of_days) {
            daytonum.put(d, idx);
            idx++;
        }

        for (String k : avail_times_for_day.keySet()) {

            int difference = daytonum.get(k) - daytonum.get(current_day_of_week);

            int newday = (daytonum.get(k) + difference) % 7;

            if(avail_times_for_day.get(k)[0] > 1410) {
                new BlockedTime(forTask, newday);
            }
            else {
                new BlockedTime(forTask, newday, 0, avail_times_for_day.get(k)[0]);
                new BlockedTime(forTask, newday, avail_times_for_day.get(k)[1], 1440);
            }

        }

    }

    /**
     * Calculates blocked time intervals for each day of the week based on available time
     * slots in a schedule. It creates BlockedTime objects for each day and returns them
     * in an ArrayList.
     *
     * @param schedule schedule data that contains a list of days and their corresponding
     * available time slots.
     *
     * Contain a `daylist` HashMap.
     *
     * @param current_day_of_week day for which available times are being considered.
     *
     * @param forTask purpose of the blocked time, with a value of `true` indicating the
     * time is blocked for a task and `false` indicating it is blocked for general availability.
     *
     * @returns a list of BlockedTime objects representing unavailable time slots for a
     * given schedule and day.
     *
     * The returned output is a list of `BlockedTime` objects, each representing a blocked
     * time period. Each `BlockedTime` object has the following attributes:
     * - `forTask`: a boolean indicating whether the blocked time is for a task.
     * - `day`: an integer representing the day of the week (0-6, where 0 is Sunday and
     * 6 is Saturday).
     * - `start`: an optional integer representing the start time of the blocked period
     * in minutes (0-1439).
     * - `end`: an optional integer representing the end time of the blocked period in
     * minutes (0-1439).
     */
    public static ArrayList<BlockedTime> setupBlockedTimes(Schedule schedule, String current_day_of_week, boolean forTask) {

        ArrayList<BlockedTime> blockedtimes = new ArrayList<>();

        HashMap<String, int[]> avail_times_for_day = schedule.daylist;
        HashMap<String, Integer> daytonum = new HashMap<String, Integer>();

        int idx = 0;
        for (String d : Schedule.list_of_days) {
            daytonum.put(d, idx);
            idx++;
        }

        for (String k : avail_times_for_day.keySet()) {
            System.out.println("\n" + k + " " + daytonum.get(k));
            int difference = daytonum.get(current_day_of_week) - daytonum.get(k);
            int newday = (daytonum.get(k) - daytonum.get(current_day_of_week) + 7) % 7;
            System.out.println("\n" + newday + " " + difference );
            if(avail_times_for_day.get(k)[0] > 1410) {
                blockedtimes.add(new BlockedTime(forTask, newday));
            } else {
                blockedtimes.add(new BlockedTime(forTask, newday, 0, avail_times_for_day.get(k)[0]));
                blockedtimes.add(new BlockedTime(forTask, newday, avail_times_for_day.get(k)[1], 1440));
            }
        }

        return blockedtimes;

    }


}
