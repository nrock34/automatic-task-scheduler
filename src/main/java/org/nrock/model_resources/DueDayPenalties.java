package org.nrock.model_resources;

import com.google.ortools.sat.IntVar;

import java.util.HashMap;

public class DueDayPenalties extends ModelVar{

    public static HashMap<Integer, IntVar> due_day_penalties = new HashMap<Integer, IntVar>();

    int task_id;
    IntVar int_var;


    public DueDayPenalties(int task_id, IntVar int_var) {
        super();
        this.task_id = task_id;
        this.int_var = int_var;

        due_day_penalties.put(task_id, int_var);
    }

}
