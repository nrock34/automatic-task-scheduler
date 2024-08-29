package org.example;

import java.util.ArrayList;

public class Solution {

    static ArrayList<Solution> solutions = new ArrayList<>();
    ArrayList<SolutionTask> tasks = new ArrayList<>();
    final int id;

    public Solution(int id) {
        solutions.add(this);
        this.id = id;
    }

    public void addTask(SolutionTask task) {

        tasks.add(task);

    }

}
