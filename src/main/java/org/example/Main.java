package org.example;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();

        ArrayList<Task> tg = TaskGenerator.generateTasks(100, 90, 10, 0);
        ArrayList<Task> ttg = new ArrayList<>();
        SchedulerModel model = new SchedulerModel();
        Task task = new Task(
                35, 30, -1,
                -1, -1, -1,
                5, 0, false, false
        );
        Task task3 = new Task(
                390, 60, -1,
                -1, -1, -1,
                5, 0, false, false
        );
        ttg.add(task3);
        ttg.add(task);
        long start = System.nanoTime();
        model.load_tasks(tg);
        model.runModel();
        JsonSolutionGenerator.gen();
        long end = System.nanoTime();
        for (Task task1 : tg) {
            System.out.printf("Task: %s %n" +
                    "   duration - %s%n" +
                    "   due time - %s%n" +
                    "   due day - %s%n" +
                    "   pref start - %s%n" +
                    "   pref day start - %s%n" +
                    "   urgency - %s%n" +
                    "   padding - %s%n" +
                    "   hasHardDueDate - %s%n",
                    task1.id, task1.duration, task1.due_time, task1.due_day,
                    task1.pref_start, task1.pref_start_day, task1.urgency, task1.padding,
                    task1.hasHardDueDate);
        }

        System.out.printf("Took %s seconds %n",(end-start)/1e+9);

        System.out.println(SolutionPrinter.solutions_made);
    }
}