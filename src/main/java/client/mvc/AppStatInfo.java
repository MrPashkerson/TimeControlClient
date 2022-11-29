package client.mvc;

import java.time.Duration;
import java.time.Instant;

public class AppStatInfo {
    private String appName;
    private Instant appStartTime;
    private Instant appEndTime = null;
    private String timeElapsed = null;
    private long totalAppTimeElapsed = 0;

    AppStatInfo(String appName, Instant appStartTime) {
        this.appName = appName;
        this.appStartTime = appStartTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void setAppEndTime(Instant appEndTime) {
        this.appEndTime = appEndTime;
    }

    public long getTotalAppTimeElapsed() {
        return totalAppTimeElapsed;
    }

    public void setTotalAppTimeElapsed(long totalAppTimeElapsed) {
        this.totalAppTimeElapsed = totalAppTimeElapsed;
    }

    public String calcElapsedTimeInFormat() {
        Duration time = Duration.ofMillis(Long.parseLong(this.timeElapsed));
        long HH = time.toHours();
        long MM = time.toMinutes();
        long SS = time.toSeconds();
        return this.appName + "; " + String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public String calcElapsedTimeInMillis() {
        Duration timeElapsed = Duration.between(this.appStartTime, this.appEndTime);
        return Long.toString(timeElapsed.toMillis());
    }
}
