package org.nrock.model_resources;
import org.nrock.PreModel;

import java.util.ArrayList;

public class BlockedTime {

    public static ArrayList<BlockedTime> blocked_times = new ArrayList<>();

    public int on_day;
    public int start_time;
    public int end_time;

    public static void adjustfortimeslot() {
        for (BlockedTime blockedTime : blocked_times) {
            blockedTime.start_time = blockedTime.start_time % PreModel.TIMESLOT_LENGTH >= 5 ? blockedTime.start_time / PreModel.TIMESLOT_LENGTH : 0;
            blockedTime.end_time = blockedTime.end_time % PreModel.TIMESLOT_LENGTH >= 5 ? blockedTime.end_time / PreModel.TIMESLOT_LENGTH : 0;
        }
    }

    public BlockedTime (int d, int s, int e) {
        this.start_time = s;
        this.end_time = e;
        this.on_day = d;
        blocked_times.add(this);
    }

    public BlockedTime (boolean forTask, int d, int s, int e) {
        this.start_time = s;
        this.end_time = e;
        this.on_day = d;
        if (!forTask) {
            blocked_times.add(this);
        }
    }

    public BlockedTime (int d) {
        this.start_time = 1;
        this.end_time = 1440;
        this.on_day = d;
        blocked_times.add(this);
    }

    public BlockedTime (boolean forTask, int d) {
        this.start_time = 1;
        this.end_time = 1440;
        this.on_day = d;
        if (!forTask) {
            blocked_times.add(this);
        }
    }




}
