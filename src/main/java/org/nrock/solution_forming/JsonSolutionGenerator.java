package org.nrock.solution_forming;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates a JSON file named "solutions.json" from a collection of solutions.
 * The JSON file represents a list of solutions, each containing a list of tasks with
 * associated penalties.
 * The JSON file is written to the file system.
 */
public class JsonSolutionGenerator {

    public final int o = 0;
    static String json_file;


    /**
     * Generates a JSON string representing a list of solutions, where each solution
     * contains a list of tasks with their penalties. The JSON string is then written to
     * a file using the `writeJson` function.
     */
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
        String end_whole_json = "]}";
        whole_json.append(end_whole_json);
        json_file = String.valueOf(whole_json);

        writeJson();
    }

    /**
     * Writes a JSON file named "solutions.json" to the current directory using the
     * contents of the `json_file` variable. If the operation is successful, it prints a
     * success message to the console. If an error occurs, it catches the exception and
     * prints the error stack trace.
     */
    static void writeJson() {

        try (FileWriter fileWriter = new FileWriter("solutions.json")) {

            fileWriter.write(json_file);
            System.out.println("Json created");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
