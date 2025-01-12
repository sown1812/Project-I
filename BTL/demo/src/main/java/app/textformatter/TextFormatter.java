package app.textformatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

public class TextFormatter {

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
            "to", "was", "were", "will", "with", "about", "above", "after",
            "again", "all", "also", "am", "any", "been", "before", "being",
            "below", "between", "both", "but", "can", "could", "did", "do",
            "does", "doing", "down", "during", "each", "few", "further", "had",
            "having", "her", "here", "hers", "herself", "him", "himself", "his",
            "how", "i", "if", "into", "me", "more", "most", "my", "myself",
            "no", "nor", "not", "now", "or", "other", "our", "ours", "ourselves",
            "out", "own", "same", "she", "should", "so", "some", "such", "than",
            "their", "theirs", "them", "themselves", "then", "there", "these",
            "they", "this", "those", "through", "too", "under", "until", "up",
            "very", "we", "what", "when", "where", "which", "while", "who",
            "whom", "why", "would", "you", "your", "yours", "yourself", "yourselves"
    ));

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