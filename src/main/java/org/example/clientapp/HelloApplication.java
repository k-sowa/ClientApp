package org.example.clientapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view.fxml"));
        //Załadowanie pliku view.fxml
        stage.setScene(new Scene(fxmlLoader.load()));
        //Utworzenie sceny
        stage.setTitle("Hello World");
        stage.show();
        //Wyswietlenie aplikacji
    }
    public static void main(String[] args) {
        launch(args);
    }
}