package org.example;

import java.util.ArrayList;
import java.util.Random;

public class TaskGenerator {

    // Method to generate random tasks for testing with duration and padding in 5-minute increments
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
