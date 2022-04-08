package com.brian.webbookmarker;

import com.brian.webbookmarker.models.ExcelData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppLauncher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Pane myPane = FXMLLoader.load(getClass().getResource
                ("mainView2.fxml"));
        Scene scene = new Scene(myPane);
        stage.setTitle("Bookmark app");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        try {
            ExcelData.getInstance().writeExcelFileWithHeaderFirstRow();
        } catch (Exception e) {
            System.out.println("Unable to save data: " + e.getMessage());
        }
    }

    @Override
    public void init() {
            ExcelData.getInstance().readExcelFileWithHeaderFirstRow("testRead.xlsx", true);
    }

}