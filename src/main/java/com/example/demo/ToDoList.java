package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ToDoList extends Application {

    private TextField titleInput;
    private TextField descriptionInput;
    private Button addButton;

    private static final String url = "jdbc:mysql://localhost:3306/todo";
    private static final String user = "root";
    private static final String password = "luka123@";


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To Do List");

        titleInput = new TextField();
        titleInput.setPromptText("Enter title");

        descriptionInput = new TextField();
        descriptionInput.setPromptText("Enter description");

        addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTask());

        VBox layout = new VBox(10, new Label("Title:"), titleInput, new Label("Description:"), descriptionInput, addButton);

        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setScene(scene);
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
}
