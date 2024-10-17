package org.nrock.model_resources;
import com.google.ortools.sat.IntVar;
import java.util.HashMap;

/**
 * Represents a variable for the start time of a task in a model, storing task IDs
 * and their corresponding start time variables in a static HashMap.
 *
 * - start_times (HashMap<Integer, IntVar>): stores a mapping of task IDs to corresponding
 * IntVar objects.
 *
 * - task_id (int): stores an integer value.
 *
 * - int_var (IntVar): is an IntVar object representing a variable in a model.
 */
public class StartTime extends ModelVar{

    public static HashMap<Integer, IntVar> start_times = new HashMap<Integer, IntVar>();

    int task_id;
    IntVar int_var;


    public StartTime(int task_id, IntVar int_var) {

        super();
        this.task_id = task_id;
        this.int_var = int_var;

        start_times.put(task_id, int_var);
    }


}
