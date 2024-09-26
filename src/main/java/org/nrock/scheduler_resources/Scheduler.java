package org.nrock.scheduler_resources;

import org.nrock.Main;
import org.nrock.PreModel;
import org.nrock.model_resources.BlockedTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


    public void createSchedule(String name, HashMap<String, int[]> day_list) {

        this.schedules.add(new Schedule(name));

        for (String k : day_list.keySet()) {
            schedules.get(-1).setDay(k, day_list.get(k));
        }

    }

    public void setupBlockedTimes(Schedule schedule) {

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
                new BlockedTime(newday);
            }
            else {
                new BlockedTime(newday, 0, avail_times_for_day.get(k)[0]);
                new BlockedTime(newday, avail_times_for_day.get(k)[1], 1440);
            }

        }

    }

    public static ArrayList<BlockedTime> setupBlockedTimes(Schedule schedule, String current_day_of_week) {

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
                blockedtimes.add(new BlockedTime(newday));
            } else {
                blockedtimes.add(new BlockedTime(true, newday, 0, avail_times_for_day.get(k)[0]));
                blockedtimes.add(new BlockedTime(true, newday, avail_times_for_day.get(k)[1], 1440));
            }
        }

        return blockedtimes;

    }


}
