package pomodoro;

public class Utility {
    static int focusCounter = 0;
    static int sceneNumber = 1;

    public static long convertTimeStringToNanoSeconds(String timeString) {
        String[] timeParts = timeString.split(":");

        // Convert minutes and seconds to integers
        int minutes = Integer.parseInt(timeParts[0]);
        int seconds = Integer.parseInt(timeParts[1]);

        // Calculate total number of seconds
        long totalSeconds = minutes * 60 + seconds;

        return (long) ((long)totalSeconds * 1e9);
    }
}
