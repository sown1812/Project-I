package com.example.project1.Demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test extends Application {
    // Các thành phần giao diện
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TableView<com.example.project1.Test.<KeywordEntry> keywordTableView;
    private TextField maxLineLengthField;
    private Label statusLabel;
    private TextFormatterController controller;
    
    // Tạo giao diện
    private void createUI(Stage primaryStage) {
        controller = new TextFormatterController();

        // Layout chính
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Phần điều khiển ở trên
        HBox controlBox = new HBox(10);
        controlBox.setPadding(new Insets(10));

        // Nút chọn file
        Button selectFileButton = new Button("Chọn File");
        selectFileButton.setOnAction(e -> selectInputFile());

        // Trường nhập độ dài dòng tối đa
        Label maxLineLengthLabel = new Label("Độ dài dòng tối đa:");
        maxLineLengthField = new TextField("50");
        maxLineLengthField.setPrefWidth(50);

        // Nút định dạng
        Button formatButton = new Button("Định dạng");
        formatButton.setOnAction(e -> formatText());

        // Nút lưu file
        Button saveButton = new Button("Lưu File");
        saveButton.setOnAction(e -> saveOutputFile());

        controlBox.getChildren().addAll(
                selectFileButton,
                maxLineLengthLabel,
                maxLineLengthField,
                formatButton,
                saveButton
        );

        // Khu vực nhập và xuất
        SplitPane textAreaSplitPane = new SplitPane();

        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Nhập văn bản hoặc chọn file...");
        inputTextArea.getStyleClass().add("text-area");

        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setPromptText("Văn bản đã được định dạng sẽ hiển thị ở đây.");
        outputTextArea.getStyleClass().add("text-area");


        textAreaSplitPane.getItems().addAll(inputTextArea, outputTextArea);

        // Bảng từ khóa
        keywordTableView = new TableView<>();
        TableColumn<com.example.project1.Test.KeywordEntry, String> keywordColumn = new TableColumn<>("Từ khóa");
        keywordColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKeyword()));

        TableColumn<com.example.project1.Test.KeywordEntry, String> linesColumn = new TableColumn<>("Các dòng");
        linesColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLines()));

        TableColumn<com.example.project1.Test.KeywordEntry, Integer> occurrencesColumn = new TableColumn<>("Số lần xuất hiện");
        occurrencesColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getOccurrences()).asObject());
// Thiết lập chế độ điều chỉnh cột
        keywordTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        keywordTableView.getColumns().addAll(keywordColumn, linesColumn, occurrencesColumn);
        // Thanh trạng thái
        statusLabel = new Label("Sẵn sàng");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setPadding(new Insets(5));

        // Đặt các phần vào layout chính
        VBox centerBox = new VBox(10);
        centerBox.getChildren().addAll(controlBox, textAreaSplitPane, keywordTableView, statusLabel);

        root.setCenter(centerBox);

        // Tạo scene
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/Style.css").toExternalForm());
        primaryStage.setTitle("Công cụ Định dạng Văn bản");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Chọn file đầu vào
    private void selectInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn File Văn Bản");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Đọc nội dung file
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();

                // Đặt nội dung vào input text area
                inputTextArea.setText(content.toString());
                statusLabel.setText("Đã chọn file: " + selectedFile.getName());
            } catch (IOException e) {
                controller.showAlert("Lỗi", "Không thể đọc file", e.getMessage());
            }
        }
    }

    // Định dạng văn bản
    private void formatText() {
        try {
            String inputText = inputTextArea.getText().trim();
            if (inputText.isEmpty()) {
                controller.showAlert("Thông báo", "Chưa có nội dung", "Vui lòng nhập văn bản hoặc chọn file.");
                return;
            }

            // Lấy độ dài dòng tối đa
            int maxLineLength = Integer.parseInt(maxLineLengthField.getText());

            // Chuẩn bị danh sách dòng từ input
            List<String> originalLines = Arrays.asList(inputTextArea.getText().split("\n"));

            // Định dạng văn bản
            List<String> formattedLines = controller.formatText(originalLines, maxLineLength);

            // Hiển thị văn bản đã định dạng
            outputTextArea.setText(String.join("\n", formattedLines));

            // Trích xuất từ khóa
            Map<String, Set<Integer>> keywordIndex = controller.extractKeywords(formattedLines);

            // Tạo danh sách từ khóa để hiển thị trong bảng
            ObservableList<KeywordEntry> keywordEntries = controller.createKeywordEntries(keywordIndex);

            // Cập nhật bảng từ khóa
            keywordTableView.setItems(keywordEntries);

            statusLabel.setText("Đã định dạng văn bản thành công!");
        } catch (NumberFormatException e) {
            controller.showAlert("Lỗi", "Độ dài dòng không hợp lệ", "Vui lòng nhập số nguyên.");
        } catch (Exception e) {
            controller.showAlert("Lỗi", "Lỗi định dạng văn bản", e.getMessage());
        }
    }
    // Lưu file đầu ra
    private void saveOutputFile() {
        if (outputTextArea.getText().isEmpty()) {
            controller.showAlert("Thông báo", "Chưa có nội dung", "Vui lòng định dạng văn bản trước khi lưu.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu File Văn Bản");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                writer.write(outputTextArea.getText());
                writer.close();

                statusLabel.setText("Đã lưu file: " + selectedFile.getName());
            } catch (IOException e) {
                controller.showAlert("Lỗi", "Không thể lưu file", e.getMessage());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        createUI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}