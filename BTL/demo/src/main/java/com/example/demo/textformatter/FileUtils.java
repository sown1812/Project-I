package com.example.demo.textformatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static String readFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString(); // trả về nội dung của tệp dưới dạng chuỗi
        }
    }

    public static void saveFile(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(content); // ghi nội dung chuỗi vào tệp
        }
    }

    public static File showFileChooser(Stage stage, boolean isSaveDialog) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll( // thêm bộ lọc
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return isSaveDialog ?
                fileChooser.showSaveDialog(stage)
              : fileChooser.showOpenDialog(stage);
    }
}
