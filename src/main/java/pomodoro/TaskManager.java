package pomodoro;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TaskManager {
    public static String tasks[];
    public int n_tasks = 0;

    public VBox taskContainer;
    AnchorPane rootNode;

    TaskManager(AnchorPane root , VBox container) {
        this.rootNode = root;
        tasks = new String[100];
        readTaskFromFile();
        this.taskContainer = container;
    }

    public void printTasks() {
        taskContainer.getChildren().clear();
        for(int i=0; i<n_tasks; i++) {
            HBox row = new HBox();
            row.setSpacing(10);
            row.setAlignment(Pos.CENTER);

            TextArea taskArea = new TextArea();
            taskArea.setText(tasks[i]);
            taskArea.setMaxWidth(200);
            taskArea.setEditable(false);
            taskArea.setWrapText(true);
            taskArea.setPrefRowCount(3);

            //set background Color Based on Current Scene
            if(Utility.sceneNumber == 1) {
                taskArea.setStyle(
                                "-fx-control-inner-background: #7AB793;" +
                                "-fx-text-fill: white;" +  // Set text color to white
                                "-fx-font-size: 15px;" +
                                "-fx-font-weight : bold;"
                );
            }
            else if(Utility.sceneNumber == 2) {
                taskArea.setStyle(
                                "-fx-control-inner-background: #9A92C4;" +
                                "-fx-text-fill: white;" +  // Set text color to white
                                "-fx-font-size: 15px;" +
                                "-fx-font-weight : bold;"
                );
            }
            else if(Utility.sceneNumber == 3) {
                taskArea.setStyle(
                                "-fx-control-inner-background: #975097;" +
                                "-fx-text-fill: white;" +  // Set text color to white
                                "-fx-font-size: 15px;" +
                                "-fx-font-weight : bold;"
                );
            }

            Button done = new Button();
            done.setText("Done");
            done.setCursor(Cursor.HAND);
            done.setStyle(
                    "-fx-background-color: #C54C4C;" +
                            "-fx-text-fill: white;" +  // Set text color to white
                            "-fx-font-size: 20px;"   // Set font size to 20px
            );

            done.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    //get  which task is this

                    Button source = (Button)(actionEvent.getSource());
                    HBox parent = (HBox) source.getParent();
                    TextArea text = (TextArea) parent.getChildren().get(0);

                    //search index in task array
                    int index = -1;
                    for(int i=0; i<n_tasks; i++) {
                        if(tasks[i].matches(text.getText())){
                            index = i;
                            break;
                        }
                    }
                    for(int i=index; i<n_tasks; i++) {
                        tasks[i] = tasks[i+1];
                    }
                    n_tasks--;
                    writeAlltaskToFile();
                    printTasks();
                }
            });

            row.getChildren().add(taskArea);
            row.getChildren().add(done);
            taskContainer.getChildren().add(row);
        }

        //print add tasks button
        FlowPane flexBox = new FlowPane();
        flexBox.setHgap(15);
        flexBox.setAlignment(Pos.CENTER);
        TextField addTask = new TextField("");
        addTask.setPromptText("Add new Task");
        if(Utility.sceneNumber == 1) {
            addTask.setStyle(
                            "-fx-control-inner-background: #7AB793;" +
                            "-fx-text-fill: white;" +  // Set text color to white
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight : bold;"
            );
        }
        else if(Utility.sceneNumber == 2) {
            addTask.setStyle(
                            "-fx-control-inner-background: #9A92C4;" +
                            "-fx-text-fill: white;" +  // Set text color to white
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight : bold;"
            );
        }
        else if(Utility.sceneNumber == 3) {
            addTask.setStyle(
                        "-fx-control-inner-background: #975097;" +
                        "-fx-text-fill: white;" +  // Set text color to white
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight : bold;"
            );
        }
        addTask.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();

                if(code == KeyCode.ENTER && addTask.getText().length() > 0) {
                        tasks[n_tasks] = addTask.getText();
                        n_tasks++;
                        addToTaskFile(addTask.getText());
                        printTasks();
                }
            }
        });

        Button btn = new Button();
        btn.setText("Add");
        btn.setCursor(Cursor.HAND);
        if(Utility.sceneNumber == 1) {
            btn.setStyle(

                            "-fx-text-fill: white;" +  // Set text color to white
                            "-fx-font-size: 20px;" +
                                    "-fx-background-color: #7AB793;"// Set font size to 20px
            );
        }
        else if(Utility.sceneNumber == 2) {
            btn.setStyle(
                            "-fx-background-color: #9A92C4;" +
                            "-fx-text-fill: white;" +  // Set text color to white
                            "-fx-font-size: 20px;"   // Set font size to 20px
            );
        }
        else if(Utility.sceneNumber == 3) {
            btn.setStyle(
                            "-fx-background-color: #975097;" +
                            "-fx-text-fill: white;" +  // Set text color to white
                            "-fx-font-size: 20px;"   // Set font size to 20px
            );
        }
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(addTask.getText().length() > 0) {
                    tasks[n_tasks] = addTask.getText();
                    n_tasks++;
                    addToTaskFile(addTask.getText());
                    printTasks();
                }

            }
        });
        flexBox.getChildren().add(addTask);
        flexBox.getChildren().add(btn);
        taskContainer.getChildren().add(flexBox);
    }

    public void readTaskFromFile(){
        String filePath = "task.txt";

        try {
            BufferedReader rd = new BufferedReader(new FileReader(filePath));
            String line;

            while((line = rd.readLine()) != null) {
                tasks[n_tasks] = line;
                n_tasks++;
            }
        }
        catch(FileNotFoundException  e) {
            createFile("task.txt");
        }
        catch(IOException e) {
            System.err.println("Error Reading Text File : " + e.getMessage());
        }
        System.out.println(n_tasks);
    }

    public void createFile(String filepath) {
        Path path = Paths.get(filepath);
        try{
            Files.createFile(path);
            System.out.println("File Created");
        }
        catch (IOException e) {
            System.err.println("Error creatin File : " + e.getMessage());
        }

    }

    public void addToTaskFile(String text) {
        String filePath = "task.txt";
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(filePath , true));
            wr.write(text);
            wr.newLine();
            wr.flush();
            System.out.println("Appended to file");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void writeAlltaskToFile() {
        String filePath = "task.txt";
        try{
            BufferedWriter wr = new BufferedWriter(new FileWriter(filePath , false));
            for(int i=0; i<n_tasks; i++) {
                wr.write(tasks[i]);
                wr.newLine();
            }
            wr.flush();
            System.out.println("Task Array written to File");
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
