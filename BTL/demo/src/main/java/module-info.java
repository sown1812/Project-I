module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    exports app;
    opens app to javafx.fxml;
    exports app.textformatter;
    opens app.textformatter to javafx.fxml;
    exports app.UI;
    opens app.UI to javafx.fxml;
}