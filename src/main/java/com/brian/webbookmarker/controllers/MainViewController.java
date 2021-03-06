package com.brian.webbookmarker.controllers;

import com.brian.webbookmarker.models.ExcelData;
import com.brian.webbookmarker.models.ExcelDataItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class MainViewController {

    private Parent myParent;
    private Scene scene;
    private Stage stage;
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

                    titleTextField.textProperty().bind(bookmarkItem.titleProperty());
                    idTextField.textProperty().bind(bookmarkItem.idProperty().asString());
                    descriptionTextField.textProperty().bind(bookmarkItem.descriptionProperty());
                    urlTextField.textProperty().bind(bookmarkItem.urlProperty());
                    categoryTextField.textProperty().bind(bookmarkItem.categoryProperty());
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

    public void exitApp(ActionEvent actionEvent) {
        Stage stage = (Stage) titlesListView.getScene().getWindow();
        stage.close();
    }

    public void handleDelete(ActionEvent actionEvent) {
        int selectedIndex = titlesListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(mainVBox.getScene().getWindow());
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
            alert.initOwner(mainVBox.getScene().getWindow());
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
        } catch (IOException e) {
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

            // going to try using an extractor to save having to update everything
            // when editing...
            // also binding all these fields to item properties
            /*idTextField.setText(item.getIdAsString());
            titleTextField.setText(item.getTitle());
            urlTextField.setText(item.getUrlAddress());
            descriptionTextField.setText(item.getDescription());
            categoryTextField.setText(item.getCategory());*/
        }
        //titlesListView.refresh();
    }

    public void handleLaunch(ActionEvent actionEvent) throws IOException {
        int selectedIndex = titlesListView.getSelectionModel().getSelectedIndex();
        ExcelDataItem item;
        String address;
        if (selectedIndex >= 0) {
            item = ExcelData.getInstance().getAllData().get(selectedIndex);
            address = item.getUrlAddress();
            address = address.contains("https://") ? item.getUrlAddress() : "https://" + item.getUrlAddress();
            Desktop.getDesktop().browse(URI.create(address));
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainVBox.getScene().getWindow());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bookmark Selected");
            alert.setContentText("Please select a bookmark in the table.");

            alert.showAndWait();

        }
    }

    @FXML
    public void switchToCategoryView(ActionEvent event) throws IOException {
        myParent = FXMLLoader.load(getClass().getResource("/com/brian/webbookmarker/categoryView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(myParent);
        stage.setScene(scene);
        stage.show();
    }

    public void switchColors(ActionEvent event) {
        if (mainVBox.getStylesheets().toString().equalsIgnoreCase("[file:/C:/Users/bmmar/javafx/WebBookmarker/target/classes/ColorfulTheme.css]")) {
            mainVBox.getStylesheets().clear();
            mainVBox.getStylesheets().add("/DarkTheme.css");
        } else {
            mainVBox.getStylesheets().clear();
            mainVBox.getStylesheets().add("/ColorfulTheme.css");
        }
    }
}
