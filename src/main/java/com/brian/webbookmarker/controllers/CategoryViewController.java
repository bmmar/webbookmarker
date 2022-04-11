package com.brian.webbookmarker.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class CategoryViewController {
    @FXML
    private VBox catViewVBox;
    private Parent myParent;
    private Stage stage;
    private Scene scene;

    @FXML
    public void switchToMainView(ActionEvent event) throws IOException {
        myParent = FXMLLoader.load(getClass().getResource("/com/brian/webbookmarker/mainView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(myParent);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleNewBookmark(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(catViewVBox.getScene().getWindow());
        dialog.setTitle("Add new bookmark");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/brian/webbookmarker/addBookmark.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            AddDialogController controller = fxmlLoader.getController();
            controller.processResults();
        }
    }


}
