package org.nrock.model_resources;

import com.google.ortools.sat.IntVar;

import java.util.HashMap;

/**
 * Stores and manages task-specific integer variables.
 *
 * - pref_day_penalties (HashMap<Integer, IntVar>): is a static HashMap that stores
 * mappings of task IDs to IntVars.
 *
 * - task_id (int): stores an integer value.
 *
 * - int_var (IntVar): Represents a variable of type IntVar.
 */
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
