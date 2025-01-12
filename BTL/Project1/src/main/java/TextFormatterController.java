package com.example.project1.Demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TextFormatterController {
    // Lưu trữ các stop words tiếng Việt
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            // Vietnamese stop words
            "và", "là", "của", "cho", "này", "kia", "ở", "tại", "vào", "để",
            "theo", "từ", "với", "trên", "dưới", "như", "lại", "hay", "hoặc",
            "thì", "nên", "tuy", "mà", "nhưng", "do", "vì", "bằng", "qua",
            "ra", "về", "đến", "xuống", "lên", "trong", "ngoài", "sau", "trước",
            "giữa", "cả", "đó", "khi", "rằng", "thế", "không", "có", "được",
            "việc", "nhà", "hết", "ta","từ","đến","lúc","để","nhằm","nếu",
            // English stop words
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "were", "will", "with"
    ));

    public List<String> formatText(List<String> originalLines, int maxLineLength) {
        List<String> formattedLines = new ArrayList<>();
        for (String paragraph : originalLines) {
            // Bỏ qua đoạn văn rỗng
            if (paragraph.trim().isEmpty()) continue;

            String[] words = paragraph.split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                // Xử lý từ quá dài
                if (word.length() > maxLineLength) {
                    if (!currentLine.isEmpty()) {
                        formattedLines.add(currentLine.toString().trim());
                        currentLine = new StringBuilder();
                    }
                    formattedLines.add(word);
                    continue;
                }

                // Logic định dạng dòng
                if (currentLine.length() + word.length() + 1 > maxLineLength) {
                    formattedLines.add(currentLine.toString().trim());
                    currentLine = new StringBuilder();
                }

                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }

            if (!currentLine.isEmpty()) {
                formattedLines.add(currentLine.toString().trim());
            }
        }

        return formattedLines;
    }

    /**
     * Trích xuất từ khóa từ các dòng đã được định dạng
     *
     * @param formattedLines Danh sách các dòng đã được định dạng
     * @return Bản đồ từ khóa với các dòng xuất hiện
     */
    public Map<String, Set<Integer>> extractKeywords(List<String> formattedLines) {
        Map<String, Set<Integer>> keywordIndex = new HashMap<>();

        for (int lineNum = 0; lineNum < formattedLines.size(); lineNum++) {
            String line = formattedLines.get(lineNum);
            String[] words = line.split("\\s+");

            for (String word : words) {
                String cleanWord = cleanWord(word);

                // Loại bỏ từ quá ngắn hoặc không phù hợp
                if (cleanWord.length() < 3 ||
                        cleanWord.isEmpty() ||
                        STOP_WORDS.contains(cleanWord)) {
                    continue;
                }

                keywordIndex.computeIfAbsent(cleanWord, k -> new TreeSet<>()).add(lineNum + 1);
            }
        }

        return keywordIndex;
    }public ObservableList<com.example.project1.Test.KeywordEntry> createKeywordEntries(Map<String, Set<Integer>> keywordIndex) {
        ObservableList<com.example.project1.Test.KeywordEntry> keywordEntries = FXCollections.observableArrayList();
        for (Map.Entry<String, Set<Integer>> entry : keywordIndex.entrySet()) {
            String lines = entry.getValue().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            // Add the occurrence count
            int occurrences = entry.getValue().size();
            keywordEntries.add(new KeywordEntry(entry.getKey(), lines, occurrences));
        }
        return keywordEntries;
    }
    public String readFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }

    public void saveFile(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    public void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public File showFileChooserOpen(Stage stage, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser.showOpenDialog(stage);
    }

    public File showFileChooserSave(Stage stage, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return fileChooser.showSaveDialog(stage);
    }

    public String cleanWord(String word) {
        return java.text.Normalizer
                .normalize(word, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }
}