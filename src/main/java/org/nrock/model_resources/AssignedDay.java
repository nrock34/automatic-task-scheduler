package org.nrock.model_resources;
import java.util.HashMap;
import com.google.ortools.sat.IntVar;

/**
 * Represents a mapping between task IDs and integer variables in a model.
 *
 * - assigned_days (HashMap<Integer, IntVar>): is a static HashMap that stores task
 * IDs as keys and IntVars as values.
 *
 * - task_id (int): represents a unique integer identifier for a task.
 *
 * - int_var (IntVar): represents a variable of type IntVar.
 */
public class AssignedDay extends ModelVar {

    public static HashMap<Integer, IntVar> assigned_days = new HashMap<Integer, IntVar>();

    int task_id;
    IntVar int_var;

    public AssignedDay(int task_id, IntVar int_var) {
        super();
        this.task_id = task_id;
        this.int_var = int_var;

        assigned_days.put(task_id, int_var);

    }

}
