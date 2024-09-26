package org.nrock.scheduler_resources;

import java.util.ArrayList;
import java.util.HashMap;

public class Schedule {

    String name;
    static ArrayList<Schedule> schedules = new ArrayList<>();

    int[] monday = new int[2];
    int[] tuesday = new int[2];
    int[] wednesday = new int[2];
    int[] thursday = new int[2];
    int[] friday = new int[2];
    int[] saturday = new int[2];
    int[] sunday = new int[2];

    public static String[] list_of_days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};;
    HashMap<String, int[]> daylist = new HashMap<>();

    void config_daylist() {
        for (String day : list_of_days) {
            daylist.put(day, day.equals("monday") ? monday : day.equals("tuesday") ? tuesday :
                    day.equals("wednesday") ? wednesday : day.equals("thursday") ? thursday :
                    day.equals("friday") ? friday : day.equals("saturday") ? saturday :
                    day.equals("sunday") ? sunday : null);
        }
    }


    public Schedule(String name) {
        this.name = name;
        System.out.printf("Duplicate schedule name (%s) found. Will be replaced by new schedule.", name);
    }

    boolean checkNameUnique(String name) {
        for (Schedule schd : schedules) {
            if (schd.name.equals(name)) {
                schedules.remove(schd);
                return false;
            }
        }
        return true;
    }

    public void setDays(int[][] days) {


    }

    public void setDay(String day, int[] timeavail) {

        daylist.put(day, timeavail);

    }

}
