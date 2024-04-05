package pomodoro;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextField timeArea;
    @FXML
    private Button startFocusCount;
    @FXML
    private Button startshortBreakCount;
    @FXML
    private Button startLongBreakCount;
    double startTime;
    double timeNow;
    double timeLeft = 0;
    double totalFocusTime;
    double totalShortBreakTime;
    double totalLongBreakTime;
    long pauseTime = 0;
    long resumeTime = 0;
    int flag = -1;
    ActionEvent anEvent;
    private final Object lock = new Object(); //used to  synchronise thread
    private volatile boolean isPaused = false;
    @FXML
    AnchorPane rootNode;
    TaskManager taskManager;
    NotificationManager notif;
    @FXML
    VBox taskContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskManager = new TaskManager(rootNode , taskContainer);
        notif = new NotificationManager();
        taskManager.printTasks();
    }

    Thread countdown = new Thread(  () -> {

        startTime = System.nanoTime(); //currentTime
        while (timeLeft > 0) {
            synchronized (lock) {
                if (isPaused) {
                    try {
                        Platform.runLater( () -> {
                            if(flag == 1)
                                startFocusCount.setText("Start");
                            else if(flag == 2)
                                startshortBreakCount.setText("Start");
                            else if((flag == 3))
                                startLongBreakCount.setText("Start");
                        });
                        lock.wait(); // Wait until notified to resume
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            timeNow = System.nanoTime();

            if(flag == 1)
            {
                if(pauseTime != 0 ) {
                    totalFocusTime += (resumeTime - pauseTime);
                    pauseTime = 0;
                    resumeTime = 0;
                }
                timeLeft = totalFocusTime - (timeNow - startTime);
            }

            if(flag == 2)
            {
                if(pauseTime != 0 ) {
                    totalShortBreakTime += (resumeTime - pauseTime);
                    pauseTime = 0;
                    resumeTime = 0;
                }
                timeLeft = totalShortBreakTime - (timeNow - startTime);
            }

            if(flag == 3)
            {
                if(pauseTime != 0 ) {
                    totalLongBreakTime += (resumeTime - pauseTime);
                    pauseTime = 0;
                    resumeTime = 0;
                }
                timeLeft = totalLongBreakTime - (timeNow - startTime);
            }


            long seconds = (long) (timeLeft / 1_000_000_000);
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;

            String text = String.format("%02d:%02d", minutes, remainingSeconds);
            Platform.runLater( ()-> {
                timeArea.setText(text);

                if(flag == 1)
                    startFocusCount.setText("Pause");
                else if(flag == 2)
                    startshortBreakCount.setText("Pause");
                else if((flag == 3))
                    startLongBreakCount.setText("Pause");
            });
            try {
                Thread.sleep(200); //  Avoid excessive UI updates
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        timeLeft = 0;

        if(flag == 1) {
            Utility.focusCounter++;
            if(Utility.focusCounter < 4)
            {
                Platform.runLater(() -> {
                    notif.playNotif("Time For a Short Break\n");
                    switchToShortBreak(anEvent);
                });
            }
            else
            {
                Platform.runLater(() -> {
                    notif.playNotif("Time for a Long Break\n");
                    switchToLongBreak(anEvent);
                });
            }
        }
        else if(flag == 2) {
            Platform.runLater(() -> {

                notif.playNotif("Time to Focus\n");
                switchToPomodoro(anEvent);
            });
        }
        else if(flag == 3) {
            Platform.runLater(() -> {
                Utility.focusCounter = 0;
                notif.playNotif("Time to Focus\n");
                switchToPomodoro(anEvent);
            });
        }

    } );

    public void startFocus(ActionEvent e) {
        if (timeLeft == 0) {
            totalFocusTime = Utility.convertTimeStringToNanoSeconds(timeArea.getText());
            timeLeft = totalFocusTime;
        }

        Button btn = (Button) (e.getSource());
        String status = btn.getText();


        if (status.matches("Start") && !isPaused) {
            anEvent = e;
            flag = 1;
            countdown.start();
        }
        else if (status.matches("Pause") && !isPaused) {
            synchronized (lock) {
                pauseTime = System.nanoTime();
                isPaused = true;
                lock.notifyAll(); // Notify the thread to pause
            }
        }
        else if(isPaused) {
            synchronized (lock) {
                resumeTime  = System.nanoTime();
                isPaused = false;
                lock.notifyAll(); // Notify the thread to pause
            }
        }
    }
    public void startShortBreak(ActionEvent e) {
        if (timeLeft == 0) {
            totalShortBreakTime = Utility.convertTimeStringToNanoSeconds(timeArea.getText());
            timeLeft = totalShortBreakTime;
        }

        Button btn = (Button)(e.getSource());
        String status = btn.getText();

        if (status.matches("Start") && !isPaused) {
            anEvent = e;
            flag = 2;
            countdown.start();
        }
        else if (status.matches("Pause") && !isPaused) {
            synchronized (lock) {
                pauseTime = System.nanoTime();
                isPaused = true;
                lock.notifyAll(); // Notify the thread to pause
            }
        }
        else if(isPaused) {
            synchronized (lock) {
                resumeTime  = System.nanoTime();
                isPaused = false;
                lock.notifyAll(); // Notify the thread to pause
            }
        }
    }
    public void startLongBreak(ActionEvent e) {

        if (timeLeft == 0) {
            totalLongBreakTime = Utility.convertTimeStringToNanoSeconds(timeArea.getText());
            timeLeft = totalLongBreakTime;
        }

        totalFocusTime = 300000000000D;
        timeLeft = totalFocusTime;

        Button btn = (Button)(e.getSource());
        String status = btn.getText();

        if (status.matches("Start") && !isPaused) {
            anEvent = e;
            flag = 3;
            countdown.start();
        }
        else if (status.matches("Pause") && !isPaused) {
            synchronized (lock) {
                pauseTime = System.nanoTime();
                isPaused = true;
                lock.notifyAll(); // Notify the thread to pause
            }
        }
        else if(isPaused) {
            synchronized (lock) {
                resumeTime  = System.nanoTime();
                isPaused = false;
                lock.notifyAll(); // Notify the thread to pause
            }
        }
    }

    private Parent root;
    private Stage stage;
    private Scene scene;
    public void switchToShortBreak(ActionEvent e) {
        try {
            Utility.sceneNumber = 2;
            root = FXMLLoader.load(getClass().getResource("scene2.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene =  new Scene(root ,  800 , 600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void switchToLongBreak(ActionEvent e) {
        try {
            Utility.sceneNumber = 3;
            root = FXMLLoader.load(getClass().getResource("scene3.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene =  new Scene(root , 800 , 600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void switchToPomodoro(ActionEvent e) {
        try {
            Utility.sceneNumber = 1;
            root = FXMLLoader.load(getClass().getResource("scene1.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene =  new Scene(root , 800 , 600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
