package org.example;

import java.io.FileWriter;
import java.io.IOException;

public class JsonSolutionGenerator {

    public final int o = 0;
    static String json_file;


    static public void gen() {
        int idx = 0;
        int idx2 = 0;
        //String json_file = "";
        StringBuilder whole_json = new StringBuilder("{\"Solutions\": [");
        for (Solution sol : Solution.solutions) {
            idx++;
            StringBuilder sol_str = new StringBuilder(String.format("{" +
                    "\"id\": %s," +
                    "\"tasks\": [", sol.id));
            idx2 = 0;
            int solution_tol_penalty = 0;
            for (SolutionTask task : sol.tasks) {
                idx2++;
                //int sol_id = idx;

                String task_str = String.format(  "{"+
                                "\"id\": %s," +
                                "\"start_time\": %s," +
                                "\"end_time\": %s," +
                                "\"assigned_day\": %s," +
                                "\"due_day_penalty\": %s," +
                                "\"pref_day_penalty\": %s," +
                                "\"time_penalty\": %s," +
                                "\"total_penalty\": %s" +
                                "}" + (idx2 < sol.tasks.size() ? "," : ""),
                        idx2, task.start_time, task.end_time,
                        task.assigned_day, task.due_day_penalty,
                        task.pref_day_penalty, task.time_penalty,
                        (task.pref_day_penalty+task.time_penalty));

                sol_str.append(task_str);

                solution_tol_penalty += (task.pref_day_penalty+task.time_penalty);


            }
            String end_task_str = String.format("]," +
                                                "\"total_solution_penalty\": %s" +
                                                "}" +
                    (idx < SolutionPrinter.solutions_made ? "," : ""), solution_tol_penalty);
            sol_str.append(end_task_str);
            whole_json.append(sol_str);
        }
        String end_whole_json = String.format("]}");
        whole_json.append(end_whole_json);
        json_file = String.valueOf(whole_json);

        writeJson();
    }

    static void writeJson() {

        try (FileWriter fileWriter = new FileWriter("solutions.json")) {

            fileWriter.write(json_file);
            System.out.println("Json created");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
