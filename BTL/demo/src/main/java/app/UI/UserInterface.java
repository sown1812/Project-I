package app.UI;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import app.textformatter.KeywordEntry;
import app.textformatter.TextFormatterController;

import java.io.*;
import java.util.*;

public class UserInterface extends Application {
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TableView<KeywordEntry> keywordTableView;
    private TextField maxLineLengthField;
    private Label statusLabel;
    private TextFormatterController controller;

    private void createUI(Stage primaryStage) {
        controller = new TextFormatterController();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox controlBox = new HBox(10);
        controlBox.setPadding(new Insets(10));

        Button selectFileButton = new Button("Chọn File");
        selectFileButton.setOnAction(e -> selectInputFile());

        Label maxLineLengthLabel = new Label("Độ dài dòng tối đa:");
        maxLineLengthField = new TextField("50");
        maxLineLengthField.setPrefWidth(50);

        Button formatButton = new Button("Định dạng");
        formatButton.setOnAction(e -> formatText());

        Button saveButton = new Button("Lưu File");
        saveButton.setOnAction(e -> saveOutputFile());

        controlBox.getChildren().addAll(
                selectFileButton,
                maxLineLengthLabel,
                maxLineLengthField,
                formatButton,
                saveButton
        );

        HBox mainContent = new HBox(10);
        mainContent.setPadding(new Insets(10));

        VBox leftSection = new VBox(10);
        leftSection.setPrefWidth(400);

        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Nhập văn bản hoặc chọn file...");
        inputTextArea.setPrefRowCount(8);
        inputTextArea.setMaxWidth(Double.MAX_VALUE);
        inputTextArea.setMaxHeight(Double.MAX_VALUE);

        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setPromptText("Văn bản đã được định dạng sẽ hiển thị ở đây.");
        outputTextArea.setPrefRowCount(8);
        outputTextArea.setMaxWidth(Double.MAX_VALUE);
        outputTextArea.setMaxHeight(Double.MAX_VALUE);

        leftSection.getChildren().addAll(inputTextArea, outputTextArea);

        VBox rightSection = new VBox(10);
        HBox.setHgrow(rightSection, Priority.ALWAYS);


        keywordTableView = new TableView<>();
        keywordTableView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        keywordTableView.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(keywordTableView, Priority.ALWAYS);

        TableColumn<KeywordEntry, String> keywordColumn = new TableColumn<>("Từ khóa");
        keywordColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKeyword()));

        TableColumn<KeywordEntry, String> linesColumn = new TableColumn<>("Các dòng");
        linesColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLines()));

        TableColumn<KeywordEntry, Integer> occurrencesColumn = new TableColumn<>("Số lần xuất hiện");
        occurrencesColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getOccurrences()).asObject());

        keywordTableView.getColumns().addAll(keywordColumn, linesColumn, occurrencesColumn);
        keywordTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        rightSection.getChildren().add(keywordTableView);

        mainContent.getChildren().addAll(leftSection, rightSection);

        statusLabel = new Label("Sẵn sàng");
        statusLabel.setPadding(new Insets(5));

        VBox finalLayout = new VBox(10);
        finalLayout.getChildren().addAll(controlBox, mainContent, statusLabel);

        root.setCenter(finalLayout);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Style.css")).toExternalForm());
        primaryStage.setTitle("Công cụ Định dạng Văn bản");
        primaryStage.setScene(scene);
        primaryStage.show();
        Image icon = new Image(getClass().getResourceAsStream("/Icon.png"));
        primaryStage.getIcons().add(icon);
    }

    private void selectInputFile() {
        File selectedFile = controller.showFileChooser(null, false);
        if (selectedFile != null) {
            try {
                String content = controller.readFile(selectedFile);
                inputTextArea.setText(content);
                statusLabel.setText("Đã chọn file: " + selectedFile.getName());
            } catch (IOException e) {
                controller.showErrorAlert("Lỗi", "Không thể đọc file", e.getMessage());
            }
        }
    }

    private void formatText() {
        try {
            String inputText = inputTextArea.getText().trim();
            if (inputText.isEmpty()) {
                controller.showAlert("Thông báo", "Chưa có nội dung", "Vui lòng nhập văn bản hoặc chọn file.");
                return;
            }

            int maxLineLength = Integer.parseInt(maxLineLengthField.getText());

            List<String> originalLines = Arrays.asList(inputText.split("\n"));

            List<String> formattedLines = controller.formatText(originalLines, maxLineLength);
            outputTextArea.setText(String.join("\n", formattedLines));

            Map<String, Set<Integer>> keywordIndex = controller.extractKeywords(formattedLines);

            ObservableList<KeywordEntry> keywordEntries = controller.createKeywordEntries(keywordIndex);

            keywordTableView.setItems(keywordEntries);

            statusLabel.setText("Đã định dạng văn bản thành công!");
        } catch (NumberFormatException e) {
            controller.showErrorAlert("Lỗi", "Độ dài dòng không hợp lệ", "Vui lòng nhập số nguyên.");
        } catch (Exception e) {
            controller.showErrorAlert("Lỗi", "Lỗi định dạng văn bản", e.getMessage());
        }
    }
    private void saveOutputFile() {
        if (outputTextArea.getText().isEmpty()) {
            controller.showAlert("Thông báo", "Chưa có nội dung", "Vui lòng định dạng văn bản trước khi lưu.");
            return;
        }

        File selectedFile = controller.showFileChooser(null, true);
        if (selectedFile != null) {
            try {
                controller.saveFile(selectedFile, outputTextArea.getText());
                statusLabel.setText("Đã lưu file: " + selectedFile.getName());
            } catch (IOException e) {
                controller.showErrorAlert("Lỗi", "Không thể lưu file", e.getMessage());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        createUI(primaryStage);
    }
}