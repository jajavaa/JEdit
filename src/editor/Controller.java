package editor;

import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.*;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import java.util.List;
import java.util.Scanner;

public class Controller {

    private String charset = "utf8";
    private String original = "";
    @FXML private GridPane window;
    @FXML private TextArea editor;
    @FXML private TextField file;
    @FXML private Label status;
    @FXML private ComboBox<String> lang;

    public void initialize() {
        lang.getItems().addAll("Java", "PHP", "JavaScript", "HTML", "CSS");
        file.setOnAction(event -> read());
        arguments();
    }

    private void arguments() {
        KeyCombination[] keyCombinations = {
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)
        };
        if(getArgs().indexOf("--no-combo") == -1) {
            window.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
                for (int j = 0; j < keyCombinations.length; j++) {
                    if (keyCombinations[j].match(event)) {
                        switch (j) {
                            case 0:
                                save();
                                break;
                            case 1:
                                find();
                                break;
                        }
                    }
                }
            });
        } else System.out.println("Combo off.");
        if(getArgs().indexOf("--no-save-prompt") == -1) {
            editor.textProperty().addListener((observable, oldValue, newValue) -> {
                if (original.equals(editor.getText()))
                    setNotEdited();
                else
                    setEdited();
            });
        } else System.out.println("Save prompt off.");
        int x;
        if((x = getArgs().indexOf("-f")) != -1 || (x = getArgs().indexOf("--file")) != -1) {
            read(getArgs().get(x+1));
            setStatus(getStatus() + " (from command line arguments)");
        }
        if((x = getArgs().indexOf("-c")) != -1 || (x = getArgs().indexOf("--charset")) != -1) {
            String charset = getArgs().get(x+1);
            if(Charset.isSupported(charset)) {
                setStatus("Charset: \"" + getArgs().get(x+1) + "\" is enabled.");
                this.charset = charset;
            }
            else setStatus(new UnsupportedCharsetException("\"" + charset + "\" is not supported."));
        }
    }

    @SuppressWarnings("unused")
    private void find(String text, boolean caseSensitive) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void find() {
        final Stage stage = new Stage();
        final VBox vBox = new VBox(10);
        final CheckBox caseSensitive = new CheckBox("Match Case");
        final TextField find = new TextField();
        final HBox hBox = new HBox(10);
        final Button button = new Button("Find");

        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(15));

        find.setOnAction(event -> find(find.getText(), caseSensitive.isSelected()));
        button.setOnAction(event -> find(find.getText(), caseSensitive.isSelected()));

        hBox.getChildren().addAll(caseSensitive, button);
        vBox.getChildren().addAll(find, hBox);

        stage.setScene(new Scene(vBox));
        stage.show();
    }

    public void save() {
        String files = home(file.getText());
        if (files != null) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(new File(files));
                fileWriter.write(editor.getText());
                setStatus("File saved: " + files);
                setNotEdited();
            } catch (IOException e) {
                setStatus(e);
            } finally {
                if(fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        setStatus(e);
                    }
                }
            }
        }
    }

    private void read(String path) {
        if(path != null && !path.equals("")) {
            StringBuilder sb = new StringBuilder();
            Scanner reader = null;
            try {
                reader = new Scanner(new File(home(path)), charset);
                while (reader.hasNextLine())
                    sb.append(reader.nextLine()).append("\n");
                editor.setText(sb.toString());
                file.setText(path);
                original = sb.toString();
                setStatus("File read: " + path);
            } catch (FileNotFoundException e) {
                setStatus(e);
            } finally {
                if(reader != null)
                    reader.close();
            }
        }
    }

    public void read() {
        if(file.getText() != null && !file.getText().equals("")) {
            StringBuilder sb = new StringBuilder();
            Scanner reader = null;
            try {
                reader = new Scanner(new File(home(file.getText())), charset);
                while (reader.hasNextLine())
                    sb.append(reader.nextLine()).append("\n");
                sb.setLength(sb.length() - 1);
                setTitle(home(file.getText()));
                editor.setText(sb.toString());
                original = sb.toString();
                setStatus("File read: " + file.getText());
            } catch (FileNotFoundException e) {
                setStatus(e);
            } finally {
                if (reader != null)
                    reader.close();
            }
        }
    }

    private String home(String url) {
        return url.startsWith("~") ? url.replaceFirst("[~]", System.getProperty("user.home")) : url;
    }

    private void setStatus(Exception e) {
        status.setText(e.getClass().getName() + ": " + e.getMessage());
        status.setTextFill(Color.RED);
    }

    private void setStatus(String message) {
        status.setText(message);
        status.setTextFill(Color.GREEN);
    }

    private String getStatus() {
        return status.getText();
    }

    private void setTitle(String file) {
        Main.setTitle(file);
    }

    private void setEdited() {
        Main.setEdited();
    }

    private void setNotEdited() {
        Main.setNotEdited();
    }

    private List<String> getArgs() {
        return Main.getArgs();
    }
}