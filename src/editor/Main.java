package editor;

import javafx.application.Application;

import javafx.application.Platform;

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
        final Image icon = new Image(getClass().getResourceAsStream("icon.jpg"));
        primaryStage.setTitle("JEditor");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("editor.fxml"))));
        primaryStage.setOnHidden(e -> Platform.exit());
        primaryStage.show();
        stage = primaryStage;
    }

    static Stage getStage() {
        return stage;
    }

    private static void setArgs(List<String> args1) {
        args = args1;
    }

    static List<String> getArgs() {
        return args;
    }
}