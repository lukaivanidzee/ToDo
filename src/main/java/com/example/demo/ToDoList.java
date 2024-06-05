package com.example.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
        styleButton(addTaskButton);

        Button viewTasksButton = new Button("View Tasks");
        viewTasksButton.setOnAction(e -> {
            viewTasks(primaryStage);
            primaryStage.setScene(viewTasksScene);
        });
        styleButton(viewTasksButton);

        Label titleLabel = new Label("To Do List");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        VBox starterPage = new VBox(20, titleLabel, addTaskButton, viewTasksButton);
        starterPage.setAlignment(Pos.CENTER);
        starterPage.setStyle("-fx-background-color: lightblue;");
        welcomeScene = new Scene(starterPage, 400, 300);

        titleInput = new TextField();
        titleInput.setPromptText("Enter title");

        descriptionInput = new TextField();
        descriptionInput.setPromptText("Enter description");

        addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTask());
        styleButton(addButton);

        Button back1 = new Button("Back");
        back1.setOnAction(e -> primaryStage.setScene(welcomeScene));
        styleButton(back1);

        VBox addTaskLayout = new VBox(10, new Label("Title:"), titleInput, new Label("Description:"), descriptionInput, addButton, back1);
        addTaskLayout.setAlignment(Pos.CENTER);
        addTaskScene = new Scene(addTaskLayout, 400, 300);

        VBox viewTasksLayout = new VBox(10, new Label("Tasks:"), new ListView<>(), new Button("Back"));
        viewTasksLayout.setAlignment(Pos.CENTER);
        viewTasksScene = new Scene(viewTasksLayout, 400, 300);

        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void addTask() {
        String title = titleInput.getText().trim();
        String description = descriptionInput.getText().trim();
        if (!title.isEmpty() && !description.isEmpty()) {
            String insertSQL = "insert into task(task_title, task_description) values(?, ?)";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.executeUpdate();
                System.out.println("Task added: " + title + " " + description);
                titleInput.clear();
                descriptionInput.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void viewTasks(Stage primaryStage) {
        ObservableList<String> tasks = FXCollections.observableArrayList();
        String selectSQL = "select task_id, task_title, task_description, completed from task";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                int id = rs.getInt("task_id");
                String title = rs.getString("task_title");
                String description = rs.getString("task_description");
                boolean completed = rs.getBoolean("completed");
                String taskDisplay = id + ". " + title + " : " + description;
                if (completed) {
                    taskDisplay += " [Is Completed]";
                }
                tasks.add(taskDisplay);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ListView<String> taskListView = new ListView<>(tasks);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Button deleteButton = new Button("Delete Task");
        deleteButton.setOnAction(e -> {
            String selectedItem = taskListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int id = Integer.parseInt(selectedItem.split(":")[0]);
                deleteTask(id);
                tasks.remove(selectedItem);
            }
        });
        styleButton(deleteButton);

        Button completeButton = new Button("Mark as Completed");
        completeButton.setOnAction(e -> {
            String selectedItem = taskListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int id = Integer.parseInt(selectedItem.split(":")[0]);
                markTaskAsCompleted(id);
                viewTasks(primaryStage);
            }
        });
        styleButton(completeButton);

        Button back2 = new Button("Back");
        back2.setOnAction(e -> primaryStage.setScene(welcomeScene));
        styleButton(back2);

        VBox viewTasksLayout = new VBox(10, new Label("Tasks:"), taskListView, deleteButton, completeButton, back2);
        viewTasksLayout.setAlignment(Pos.CENTER);
        viewTasksScene.setRoot(viewTasksLayout);
    }

    private void deleteTask(int id) {
        String deleteSQL = "delete from task where task_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
            ps.setInt(1, id);
            System.out.println("Task :" + id + " deleted");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void markTaskAsCompleted(int id) {
        String updateSQL = "update task set completed = ? where task_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(updateSQL)) {
            ps.setBoolean(1, true);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("Task :" + id + " is completed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: darkslateblue; -fx-text-fill: white; -fx-font-size: 14px;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
