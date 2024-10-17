package org.nrock.scheduler_resources;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a schedule with a unique name and allows configuration of available
 * times for each day of the week. It maintains a collection of schedules and checks
 * for duplicate names. The class provides methods to set and configure day lists.
 */
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

    /**
     * Populates a `daylist` map with day names as keys and corresponding day objects as
     * values, using a ternary operator to determine the day object based on the day name.
     */
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

    /**
     * Checks if a given name is unique among a collection of schedules, removing and
     * returning false if a duplicate is found, and returning true otherwise.
     *
     * @param name name to be checked for uniqueness among the existing schedules.
     *
     * @returns A boolean value indicating whether the given name is unique among existing
     * schedules, or true if it is.
     */
    boolean checkNameUnique(String name) {
        for (Schedule schd : schedules) {
            if (schd.name.equals(name)) {
                schedules.remove(schd);
                return false;
            }
        }
        return true;
    }

    /**
     * Accepts a 2D integer array `days` as input and is intended to set or update the
     * value of `days`.
     *
     * @param days a two-dimensional array of integers.
     */
    public void setDays(int[][] days) {


    }

    /**
     * Stores a given day and its corresponding available time slots in a data structure,
     * specifically a key-value pair in a map called `daylist`. The day serves as the
     * key, and the available time slots are stored as an array of integers. This function
     * updates the existing entry if the day already exists in the map.
     *
     * @param day day of the week, used as a key to store corresponding time availability
     * in the `daylist` map.
     *
     * @param timeavail available time slots for the specified day, stored in an integer
     * array.
     */
    public void setDay(String day, int[] timeavail) {

        daylist.put(day, timeavail);

    }

}
