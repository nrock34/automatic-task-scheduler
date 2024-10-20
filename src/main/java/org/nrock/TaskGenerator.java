package org.nrock;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;


/**
 * Generates random tasks with various attributes such as duration, urgency, and due
 * dates. It offers two methods to generate tasks: one with a custom task creation
 * method and another with default task creation.
 */
public class TaskGenerator {

    // Method to generate random tasks for testing with duration and padding in 5-minute increments
    /**
     * Generates a specified number of tasks with random attributes, such as name, duration,
     * preferred start time, due time, urgency, and padding, and returns them as a list
     * of tasks.
     *
     * @param numTasks number of tasks to be generated.
     *
     * @param maxDuration maximum duration of a task in minutes, which is used to generate
     * random task durations in increments of 5 minutes.
     *
     * @param maxUrgency maximum value that the `urgency` attribute of a generated task
     * can take, with actual urgency values being randomly selected between 1 and this
     * maximum value.
     *
     * @param maxPadding maximum amount of padding in 5-minute increments that can be
     * randomly assigned to each task.
     *
     * @param method function that is used to create a new `Task` object based on the
     * randomly generated attributes.
     *
     * Apply functional decomposition to `method` if appropriate.
     *
     * @returns a list of tasks with various attributes generated randomly.
     *
     * The output is a list of tasks, where each task is an object with the following
     * attributes: name, id, duration, preferred start time, preferred start day, due
     * time, due day, urgency, padding, hard due date, and custom weight.
     */
    public static ArrayList<Task> generateTasks(int numTasks, int maxDuration, int maxUrgency, int maxPadding, Consumer10<String, Integer, Integer, Integer, Integer,
                                                                                                                            Integer, Integer, Integer, Integer,
                                                                                                                            Boolean, Boolean, Task> method) {
        ArrayList<Task> generatedTasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numTasks; i++) {
            // Generate random attributes for the task
            int id = i;
            boolean preferences = random.nextBoolean();

            String name = UUID.randomUUID().toString();
            boolean hasHardDueDate = random.nextBoolean();
            int duration = (random.nextInt(maxDuration / PreModel.TIMESLOT_LENGTH) + 1) * PreModel.TIMESLOT_LENGTH;  // Random duration in 5-minute increments
            int prefStart = preferences ? random.nextInt((1440 - duration) / PreModel.TIMESLOT_LENGTH) * PreModel.TIMESLOT_LENGTH : -1;  // Random preferred start time (in minutes)
            int prefStartDay = preferences ? random.nextInt(PreModel.DAYS_IN_ADAVANCE) : -1; // Random preferred start day within 28 days
            int dueTime = hasHardDueDate ? random.nextInt(1440 / PreModel.TIMESLOT_LENGTH) * PreModel.TIMESLOT_LENGTH : -1;  // Random due time or -1
            int dueDay = hasHardDueDate ? random.nextInt(PreModel.DAYS_IN_ADAVANCE) : -1;  // Random due day or -1
            int urgency = random.nextInt(maxUrgency) + 1;  // Random urgency between 1 and maxUrgency
            int padding = maxPadding > 0 ? (random.nextInt(maxPadding)) : 0;  // Random padding in 5-minute increments
              // Randomly assign hard due date
            boolean hasCustomWeight = random.nextBoolean();  // Randomly assign custom weight

            // Create a new task with the generated attributes
            generatedTasks.add(method.apply("" ,id, duration, prefStart, prefStartDay, dueTime, dueDay, urgency, padding, hasHardDueDate, hasCustomWeight));

            // Add the task to the list
            //generatedTasks.add(newTask);
        }

        return generatedTasks;
    }

    /**
     * Generates a specified number of tasks with random attributes, including duration,
     * preferred start time, due time, urgency, and padding. It also randomly assigns
     * hard due dates and custom weights to the tasks.
     *
     * @param numTasks number of tasks that are generated by the function.
     *
     * @param maxDuration maximum duration of a task in minutes, used to limit the random
     * duration generated for each task.
     *
     * @param maxUrgency upper limit for a random urgency value assigned to each task.
     *
     * @param maxPadding maximum possible random padding added to each task duration in
     * 5-minute increments.
     *
     * @returns an ArrayList of Task objects with randomly generated attributes.
     *
     * Contain a list of unique task IDs,
     * Each task has a duration in 5-minute increments.
     */
    public static ArrayList<Task> generateTasks(int numTasks, int maxDuration, int maxUrgency, int maxPadding) {
        ArrayList<Task> generatedTasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numTasks; i++) {
            // Generate random attributes for the task
            int id = i;
            boolean preferences = random.nextBoolean();

            boolean hasHardDueDate = random.nextBoolean();
            int duration = (random.nextInt(maxDuration / PreModel.TIMESLOT_LENGTH) + 1) * PreModel.TIMESLOT_LENGTH;  // Random duration in 5-minute increments
            int prefStart = preferences ? random.nextInt((1440 - duration) / PreModel.TIMESLOT_LENGTH) * PreModel.TIMESLOT_LENGTH : -1;  // Random preferred start time (in minutes)
            int prefStartDay = preferences ? random.nextInt(PreModel.DAYS_IN_ADAVANCE) : -1; // Random preferred start day within 28 days
            int dueTime = hasHardDueDate ? random.nextInt(1440 / PreModel.TIMESLOT_LENGTH) * PreModel.TIMESLOT_LENGTH : -1;  // Random due time or -1
            int dueDay = hasHardDueDate ? random.nextInt(PreModel.DAYS_IN_ADAVANCE) : -1;  // Random due day or -1
            int urgency = random.nextInt(maxUrgency) + 1;  // Random urgency between 1 and maxUrgency
            int padding = maxPadding > 0 ? (random.nextInt(maxPadding)) : 0;  // Random padding in 5-minute increments
            // Randomly assign hard due date
            boolean hasCustomWeight = random.nextBoolean();  // Randomly assign custom weight

            // Create a new task with the generated attributes
            Task newTask = new Task(id, duration, prefStart, prefStartDay, dueTime, dueDay, urgency, padding, hasHardDueDate, hasCustomWeight);

            // Add the task to the list
            generatedTasks.add(newTask);
        }

        return generatedTasks;
    }
}
