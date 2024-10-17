package org.nrock.model_resources;

import com.google.ortools.sat.IntVar;

import java.util.HashMap;

/**
 * Stores and manages time penalties related to tasks using a HashMap of IntVars.
 *
 * - time_penalties (HashMap<Integer, IntVar>): stores a static mapping of task IDs
 * to IntVar objects.
 *
 * - task_id (int): stores a unique integer identifier.
 *
 * - int_var (IntVar): is an integer variable.
 */
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
