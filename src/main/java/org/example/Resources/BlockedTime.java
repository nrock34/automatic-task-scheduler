package org.example.Resources;
import java.util.ArrayList;

public class BlockedTime {

    public static ArrayList<BlockedTime> blocked_times = new ArrayList<>();

    public int on_day;
    public int start_time;
    public int end_time;

    public BlockedTime (int d, int s, int e) {
        this.start_time = s;
        this.end_time = e;
        this.on_day = d;
        blocked_times.add(this);
    }

    public BlockedTime (int d) {
        this.start_time = 1;
        this.end_time = 288;
        this.on_day = d;
        blocked_times.add(this);
    }




}
