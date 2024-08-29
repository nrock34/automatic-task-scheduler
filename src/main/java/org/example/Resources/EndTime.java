package org.example.Resources;
import java.util.HashMap;
import com.google.ortools.sat.IntVar;

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
