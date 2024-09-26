package org.nrock.model_resources;
import com.google.ortools.sat.IntVar;
import java.util.HashMap;

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
