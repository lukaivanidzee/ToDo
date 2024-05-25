package com.example.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class ToDoList extends Application {

    private TextField titleInput;
    private TextField descriptionInput;
    private Button addButton;

    private Scene welcomeScene;
    private Scene addTaskScene;
    private Scene viewTasksScene;

    private static final String url = "jdbc:mysql://localhost:3306/todo";
    private static final String user = "root";
    private static final String password = "luka123@";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To Do List");

        Button addTaskButton = new Button("Add Task");
        addTaskButton.setOnAction(e -> primaryStage.setScene(addTaskScene));

        Button viewTasksButton = new Button("View Tasks");
        viewTasksButton.setOnAction(e -> {
            viewTasks(primaryStage);
            primaryStage.setScene(viewTasksScene);
        });

        VBox starterPage = new VBox(10, new Label("To Do List"), addTaskButton, viewTasksButton);
        welcomeScene = new Scene(starterPage, 300, 250);

        titleInput = new TextField();
        titleInput.setPromptText("Enter title");

        descriptionInput = new TextField();
        descriptionInput.setPromptText("Enter description");

        addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTask());

        Button back1 = new Button("Back");
        back1.setOnAction(e -> primaryStage.setScene(welcomeScene));

        VBox addTaskLayout = new VBox(10, new Label("Title:"), titleInput, new Label("Description:"), descriptionInput, addButton, back1);
        addTaskScene = new Scene(addTaskLayout, 300, 250);

        VBox viewTasksLayout = new VBox(10, new Label("Tasks:"), new ListView<>(), new Button("Back"));
        viewTasksScene = new Scene(viewTasksLayout, 300, 250);

        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void addTask() {
        String title = titleInput.getText().trim();
        String description = descriptionInput.getText().trim();
        if (!title.isEmpty() && !description.isEmpty()) {
            String insertSQL = "INSERT INTO task(task_title, task_description) VALUES(?, ?)";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.executeUpdate();
                titleInput.clear();
                descriptionInput.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void viewTasks(Stage primaryStage) {
        ObservableList<String> tasks = FXCollections.observableArrayList();
        String selectSQL = "select task_title, task_description from task";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                String title = rs.getString("task_title");
                String description = rs.getString("task_description");
                tasks.add(title + ": " + description);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ListView<String> taskListView = new ListView<>(tasks);

        Button back2 = new Button("Back");
        back2.setOnAction(e -> primaryStage.setScene(welcomeScene));

        VBox viewTasksLayout = new VBox(10, new Label("Tasks:"), taskListView, back2);
        viewTasksScene.setRoot(viewTasksLayout);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
