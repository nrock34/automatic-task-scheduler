package org.nrock.solution_forming;

import java.util.ArrayList;

/**
 * Represents a solution with a unique identifier, stores associated tasks and
 * calculates a total penalty.
 */
public class Solution {

    public static ArrayList<Solution> solutions = new ArrayList<>();
    ArrayList<SolutionTask> tasks = new ArrayList<>();
    public int sol_tol_pen = 0;
    public final int id;

    public Solution(int id) {
        solutions.add(this);
        this.id = id;
    }

    /**
     * Adds a task to a collection, and updates a penalty total.
     *
     * @param task solution task to be added.
     */
    public void addTask(SolutionTask task) {

        tasks.add(task);
        sol_tol_pen += task.total_penalty;


    }

}
