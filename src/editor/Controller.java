package editor;

import com.sun.istack.internal.Nullable;
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

import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    private String charset = "utf8";
    private String[] supportedLang = {
            "Java",
            "PHP",
            "JavaScript",
            "CSS",
            "HTML",
    };
    private String[] text = {
            "",
            "txt"
    };
    @FXML private GridPane window;
    @FXML private Label status;
    @FXML private Button read;
    @FXML private ComboBox<String> lang;
    @FXML private TabPane tabs;

    public void initialize() {
        lang.getItems().addAll(supportedLang);
        read.setOnAction(e -> read(null));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
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
        int x;
        if((x = getArgs().indexOf("-f")) != -1 || (x = getArgs().indexOf("--file")) != -1) {
            read(getArgs().get(x+1));
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
//        To be implemented.
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
        File file = new File("/home/konrad/results.txt");
        HTMLEditor editor = ((HTMLEditor)tabs.getSelectionModel().getSelectedItem().getContent());
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(stripHTMLTags(editor.getHtmlText()));
            setStatus("File saved: " + file.getAbsolutePath());
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

    public void saveAs() {

    }

    private void read(@Nullable String fileName) {
        File file;
        if(fileName == null)
            file = new FileChooser().showOpenDialog(getStage());
        else
            file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        Scanner reader = null;
        try {
            reader = new Scanner(file, charset);
            while (reader.hasNextLine())
                sb.append("\n").append(reader.nextLine());
            HTMLEditor editor = (HTMLEditor)addTab(file.getAbsolutePath()).getContent();
            editor.setHtmlText(sb.toString().replaceFirst("[\n]", ""));
            setLang(file.getName());
            if(fileName == null)
                setStatus("File read: " + file.getName());
            else
                setStatus("File read: " + file.getName() + " (from command line arguments)");
        } catch (FileNotFoundException e) {
            setStatus(e);
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public Tab addTab() {
        HTMLEditor editor = new HTMLEditor();
        Tab tab = new Tab("Untitled", editor);
        tab.setContent(editor);
        tabs.getTabs().add(tab);
        return tab;
    }

    private String stripHTMLTags(String htmlText) {

        Pattern pattern = Pattern.compile("<[^>]*>");
        Matcher matcher = pattern.matcher(htmlText);
        final StringBuffer sb = new StringBuffer(htmlText.length());
        while(matcher.find()) {
            matcher.appendReplacement(sb, " ");
        }
        matcher.appendTail(sb);
        return sb.toString().trim();

    }

    private Tab addTab(String title) {
        HTMLEditor editor = new HTMLEditor();
        Tab tab = new Tab(title, editor);
        tab.setContent(editor);
        tabs.getTabs().add(tab);
        return tab;
    }

    private void setLang(String path) {
        String[] ext = path.split("[.]");
        for(String lang: text) {
            if(lang.equalsIgnoreCase(ext[ext.length-1])) {
                this.lang.getSelectionModel().select("Plain");
                return;
            }
        }
        for(String lang: supportedLang) {
            if(lang.equalsIgnoreCase(ext[ext.length-1])) {
                this.lang.getSelectionModel().select(lang);
                return;
            }
        }
    }

    private void setStatus(Exception e) {
        status.setText(e.getClass().getName() + ": " + e.getMessage());
        status.setTextFill(Color.RED);
    }

    private void setStatus(String message) {
        status.setText(message);
        status.setTextFill(Color.GREEN);
    }

    private Stage getStage() {
        return Main.getStage();
    }

    private List<String> getArgs() {
        return Main.getArgs();
    }
}