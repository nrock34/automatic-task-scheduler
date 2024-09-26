package org.nrock.model_resources;
import java.util.HashMap;
import com.google.ortools.sat.IntVar;

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
