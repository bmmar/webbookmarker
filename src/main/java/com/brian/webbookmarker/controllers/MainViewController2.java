package com.brian.webbookmarker.controllers;

import com.brian.webbookmarker.models.ExcelData;
import com.brian.webbookmarker.models.ExcelDataItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class MainViewController2 {

    @FXML
    private TextField idTextField;
    @FXML
    private TextArea descriptionTextField;
    @FXML
    private TextField categoryTextField;
    @FXML
    private ListView<ExcelDataItem> titlesListView;
    @FXML
    private VBox mainVBox;

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField urlTextField;


    @FXML
    private GridPane mainGridPane;

    @FXML
    private TableView<ExcelDataItem> tableView;

    @FXML
    private void initialize() {
        titlesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    ExcelDataItem bookmarkItem = titlesListView.getSelectionModel().getSelectedItem();
                    idTextField.setText(bookmarkItem.getIdAsString());
                    titleTextField.setText(bookmarkItem.getTitle());
                    urlTextField.setText(bookmarkItem.getUrlAddress());
                    descriptionTextField.setText(bookmarkItem.getDescription());
                    categoryTextField.setText(bookmarkItem.getCategory());
                }
            }
        });

        titlesListView.setItems(ExcelData.getInstance().getAllData());
        titlesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        titlesListView.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleNewItem() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainVBox.getScene().getWindow());
        dialog.setTitle("Add new bookmark");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/brian/webbookmarker/addBookmark.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch (IOException e) {
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

    public void exitApp(ActionEvent actionEvent) {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }

    public void handleDelete(ActionEvent actionEvent) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(mainGridPane.getScene().getWindow());
            alert.setTitle("Confirm delete");
            alert.setHeaderText("Confirm deletion");
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
            ExcelData.getInstance().getAllData().remove(selectedIndex);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainGridPane.getScene().getWindow());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bookmark Selected");
            alert.setContentText("Please select a bookmark in the table.");

            alert.showAndWait();
        }
    }

    public void handleEdit(ActionEvent actionEvent) {
        int selectedIndex = titlesListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            ExcelDataItem item = titlesListView.getSelectionModel().getSelectedItem();
            ShowEditDialog(item);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainVBox.getScene().getWindow());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bookmark Selected");
            alert.setContentText("Please select a bookmark in the table.");

            alert.showAndWait();
        }
    }
    //  Here's how to pass data to another scene or in this case another dialog

    private void ShowEditDialog(ExcelDataItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/brian/webbookmarker/editBookmark.fxml"));

        dialog.initOwner(mainVBox.getScene().getWindow());
        dialog.setTitle("Edit bookmark");
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        EditDialogController controller = fxmlLoader.getController();
        controller.initData(item);  // the crucial method which gets the item's relevant fields and
        // uses them to populate the edit dialog

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller = fxmlLoader.getController();
            controller.processResults(item);
        }
        titlesListView.refresh();
    }

    public void handleLaunch(ActionEvent actionEvent) throws IOException {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        ExcelDataItem item;
        String address;
        if (selectedIndex >= 0) {
            item = ExcelData.getInstance().getAllData().get(selectedIndex);
            address = item.getUrlAddress();
            address = address.contains("https://") ? item.getUrlAddress() : "https://" + item.getUrlAddress();
            Desktop.getDesktop().browse(URI.create(address));
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainGridPane.getScene().getWindow());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bookmark Selected");
            alert.setContentText("Please select a bookmark in the table.");

            alert.showAndWait();

        }
    }

}
