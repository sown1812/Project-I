package app.textformatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class TextFormatter {

    private static final Set<String> STOP_WORDS = loadStopWords();

    private static Set<String> loadStopWords() {
        Set<String> stopWords = new HashSet<>();
        // Danh sách tệp stop words
        String[] stopWordsFiles = {"/stopwords_english.txt"
                                 , "/stopwords_vietnamese.txt"};

        for (String fileName : stopWordsFiles) {
            try (InputStream inputStream = TextFormatter.class.getResourceAsStream(fileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stopWords.add(line.trim().toLowerCase());
                }
            } catch (IOException | NullPointerException e) {
                throw new RuntimeException("Failed to load stop words from file: " + fileName, e);
            }
        }

        return stopWords;
    }

    public List<String> formatText(List<String> originalLines, int maxLineLength) {
        List<String> formattedLines = new ArrayList<>(); // tạo danh sách lưu văn bản đã format
        for (String paragraph : originalLines) {
            if (paragraph.trim().isEmpty()) continue;

            String[] words = paragraph.split("\\s+");
            StringBuilder currentLine = new StringBuilder(); // xây dựng từng dòng
            for (String word : words) {
                if (word.length() > maxLineLength) {
                    if (!currentLine.isEmpty()) {
                        formattedLines.add(currentLine.toString().trim());
                        currentLine = new StringBuilder();
                    }
                    formattedLines.add(word);
                    continue;
                }
                if (currentLine.length() + word.length() + 1 > maxLineLength) {
                    formattedLines.add(currentLine.toString().trim()); // thêm vào formatted line
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

    public Map<String, Set<Integer>> extractKeywords(List<String> formattedLines) {
        Map<String, Set<Integer>> keywordIndex = new HashMap<>();

        for (int lineNum = 0; lineNum < formattedLines.size(); lineNum++) {
            String line = formattedLines.get(lineNum); // lấy dữ liệu của từng dòng
            String[] words = line.split("\\s+");

            for (String word : words) {
                String cleanWord = cleanWord(word);
                if ( STOP_WORDS.contains(cleanWord)) {
                    continue;
                }

                keywordIndex.computeIfAbsent(cleanWord, k -> new TreeSet<>()).add(lineNum + 1);
            }
        }

        return keywordIndex;
    }

    public ObservableList<KeywordEntry> createKeywordEntries(Map<String, Set<Integer>> keywordIndex) {
        ObservableList<KeywordEntry> keywordEntries = FXCollections.observableArrayList();
        for (Map.Entry<String, Set<Integer>> entry : keywordIndex.entrySet()) {
            String lines = entry.getValue().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            // Đếm số lần xuất hiện
            int occurrences = entry.getValue().size();
            keywordEntries.add(new KeywordEntry(entry.getKey(), lines, occurrences));
        }
        return keywordEntries;
    }

    private String cleanWord(String word) {
        return word.toLowerCase().replaceAll("[^\\p{L}\\p{N}]", "");
    }
}