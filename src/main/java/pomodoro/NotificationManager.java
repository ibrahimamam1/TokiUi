package pomodoro;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.controlsfx.control.Notifications;

public class NotificationManager {
    File soundFile;
    Media media;

    MediaPlayer mediaPlayer;

    NotificationManager() {
        soundFile = new File("src/main/resources/pomodoro/notif.wav");
        media = new Media(soundFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

    }

    public void playNotif(String text) {
        Notifications.create().title("Toki").text(text).showWarning();
        mediaPlayer.play();
    }


}
