package org.example.Resources;

import com.google.ortools.sat.IntVar;

import java.util.HashMap;

public class PrefDayPenalties extends ModelVar {

    public static HashMap<Integer, IntVar> pref_day_penalties = new HashMap<Integer, IntVar>();

    int task_id;
    IntVar int_var;


    public PrefDayPenalties(int task_id, IntVar int_var) {

        super();
        this.task_id = task_id;
        this.int_var = int_var;

        pref_day_penalties.put(task_id, int_var);

    }

}
