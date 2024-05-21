package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ToDoList extends Application {

    private ListView<String> listView;
    private TextField taskInput;
    private Button addButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To Do List");

        listView = new ListView<>();
        taskInput = new TextField();
        taskInput.setPromptText("Enter a new task");

        addButton = new Button("Add Task");

        VBox layout = new VBox(10, taskInput, addButton, listView);

        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

        addButton.setOnAction(e -> addTask());
    }

    private void addTask() {
        String taskDescription = taskInput.getText();
        if (!taskDescription.isEmpty()) {
            listView.getItems().add(taskDescription);
            taskInput.clear();
        }
    }
}
