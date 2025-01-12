package com.example.demo.textformatter;

import app.UI.AlertUtils;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextFormatterController {

    private final TextFormatter textFormatter = new TextFormatter();

    public void setViStopwordsFile(File file) {
        textFormatter.setViStopwordsFile(file);
    }

    public void setEnStopwordsFile(File file) {
        textFormatter.setEnStopwordsFile(file);
    }

    public List<String> formatText(List<String> originalLines, int maxLineLength) {
        return textFormatter.formatText(originalLines, maxLineLength);
    }
    public Map<String, Set<Integer>> extractKeywords(List<String> formattedLines) {
        return textFormatter.extractKeywords(formattedLines);
    }
    public ObservableList<KeywordEntry> createKeywordEntries(Map<String, Set<Integer>> keywordIndex) {
        return textFormatter.createKeywordEntries(keywordIndex);
    }
    public String readFile(File file) throws IOException {
        return FileUtils.readFile(file);
    }
    public void saveFile(File file, String content) throws IOException {
        FileUtils.saveFile(file, content);
    }
    public File showFileChooser(Stage stage, boolean isSaveDialog) {
        return FileUtils.showFileChooser(stage, isSaveDialog);
    }
    public void showAlert(String title, String header, String message) {
        AlertUtils.showAlert(title, header, message);
    }
    public void showErrorAlert(String title, String header, String message) {
        AlertUtils.showErrorAlert(title, header, message);
    }
}