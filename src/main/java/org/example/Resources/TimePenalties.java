package org.example.Resources;

import com.google.ortools.sat.IntVar;

import java.util.HashMap;

public class TimePenalties extends ModelVar {

    public static HashMap<Integer, IntVar> time_penalties = new HashMap<Integer, IntVar>();

    int task_id;
    IntVar int_var;


    public TimePenalties(int task_id, IntVar int_var) {

        super();
        this.task_id = task_id;
        this.int_var = int_var;

        time_penalties.put(task_id, int_var);

    }

}
