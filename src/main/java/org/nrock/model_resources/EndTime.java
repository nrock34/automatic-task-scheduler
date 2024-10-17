package org.nrock.model_resources;
import java.util.HashMap;
import com.google.ortools.sat.IntVar;

/**
 * Represents a model variable for task end times in a scheduling optimization problem.
 *
 * - end_times (HashMap<Integer, IntVar>): stores a static map of task IDs to IntVars.
 *
 * - task_id (int): stores a unique integer identifier.
 *
 * - int_var (IntVar): represents an integer variable.
 */
public class EndTime extends ModelVar {

    public static HashMap<Integer, IntVar> end_times = new HashMap<Integer, IntVar>();

    int task_id;
    IntVar int_var;

    public EndTime(int task_id, IntVar int_var) {

        super();
        this.task_id = task_id;
        this.int_var = int_var;

        end_times.put(task_id, int_var);
    }

}
