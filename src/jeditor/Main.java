package jeditor;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;

import javafx.scene.image.Image;

import javafx.stage.Stage;

import java.io.IOException;

import java.util.List;

public class Main extends Application {

    private static Stage stage;
    private static List<String> args;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        setArgs(getParameters().getUnnamed());
        Image icon = new Image(getClass().getResourceAsStream("download.jpg"));
        primaryStage.setTitle("JEditor");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("editor.fxml"))));
        primaryStage.show();
        stage = primaryStage;
    }

    static void setTitle(String file) {
        stage.setTitle("JEditor - " + file);
    }

    static void setEdited() {
        if(!stage.getTitle().endsWith("*"))
            stage.setTitle(stage.getTitle() + "*");
    }

    static void setNotEdited() {
        if(stage.getTitle().endsWith("*"))
            stage.setTitle(stage.getTitle().replace("*", ""));
    }

    private static void setArgs(List<String> args1) {
        args = args1;
    }

    static List<String> getArgs() {
        return args;
    }
}