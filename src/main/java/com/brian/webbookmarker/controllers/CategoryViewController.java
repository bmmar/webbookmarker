package com.brian.webbookmarker.controllers;

import com.brian.webbookmarker.models.ExcelData;
import com.brian.webbookmarker.models.ExcelDataItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryViewController {

    @FXML
    private ListView<String> categoryListView;
    @FXML
    private ListView<ExcelDataItem> titlesListView;
    @FXML
    private VBox catViewVBox;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextField categoryTextField;
    @FXML
    private TextArea descriptionTextArea;


    private List<ExcelDataItem> fullTitlesList = new ArrayList<>();
    private final ObservableList<ExcelDataItem> selectedList = FXCollections.observableArrayList();

    public void initialize() {
        ObservableList<String> catList;
        catList = FXCollections.observableArrayList();

        categoryListView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<? super String>)
                (observable, oldValue, newValue) -> {
            if (newValue != null) {
                String catSelected = categoryListView.getSelectionModel().getSelectedItem();
                List<ExcelDataItem> allData = ExcelData.getInstance().getAllData();
                selectedList.clear();
                for (ExcelDataItem item : allData) {
                       if (item.getCategory().equals(catSelected)){
                           selectedList.add(item);
                       }
                }
                titlesListView.getSelectionModel().selectFirst();
            }
        });


        titlesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    ExcelDataItem bookmarkItem = titlesListView.getSelectionModel().getSelectedItem();
                    titleTextField.textProperty().bind(bookmarkItem.titleProperty());
                    idTextField.textProperty().bind(bookmarkItem.idProperty().asString());
                    descriptionTextArea.textProperty().bind(bookmarkItem.descriptionProperty());
                    urlTextField.textProperty().bind(bookmarkItem.urlProperty());
                    categoryTextField.textProperty().bind(bookmarkItem.categoryProperty());

                }
            }
        });

        ObservableSet<String> categories = ExcelData.getInstance().getCategories();
        catList.addAll(categories);
        categoryListView.setItems(catList);

        categoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        categoryListView.getSelectionModel().selectFirst();
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();


        fullTitlesList = ExcelData.getInstance().getAllData();
        selectedList.clear();
        for (ExcelDataItem title : fullTitlesList) {
            if (title.getCategory().equals(selectedCategory)) {
                selectedList.add(title);
            }
        }

        titlesListView.setItems(selectedList);
        titlesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        titlesListView.getSelectionModel().selectFirst();
    }

    @FXML
    public void switchToMainView(ActionEvent event) throws IOException {
        Parent myParent = FXMLLoader.load(getClass().getResource("/com/brian/webbookmarker/mainView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(myParent);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleNewBookmark() {
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

    public void handleLaunch(ActionEvent actionEvent) throws IOException {
        ExcelDataItem item = titlesListView.getSelectionModel().getSelectedItem();

        String address;
        if (item != null) {
            address = item.getUrlAddress();
            address = address.contains("https://") ? item.getUrlAddress() : "https://" + item.getUrlAddress();
            Desktop.getDesktop().browse(URI.create(address));
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(catViewVBox.getScene().getWindow());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bookmark Selected");
            alert.setContentText("Please select a bookmark in the table.");

            alert.showAndWait();

        }
    }


    public void exitApp(ActionEvent actionEvent) {
        Stage stage = (Stage) titlesListView.getScene().getWindow();
        stage.close();
    }

    public void handleDelete(ActionEvent actionEvent) {
        ExcelDataItem selectedItem = titlesListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(catViewVBox.getScene().getWindow());
            alert.setTitle("Confirm delete");
            alert.setHeaderText("Confirm deletion");
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
            ExcelData.getInstance().getAllData().remove(selectedItem);
            String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();

            fullTitlesList = ExcelData.getInstance().getAllData();
            selectedList.clear();
            for (ExcelDataItem title : fullTitlesList) {
                if (title.getCategory().equals(selectedCategory)) {
                    selectedList.add(title);
                }
            }

            titlesListView.setItems(selectedList);
            titlesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            titlesListView.getSelectionModel().selectFirst();

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(catViewVBox.getScene().getWindow());
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
            alert.initOwner(catViewVBox.getScene().getWindow());
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

        dialog.initOwner(catViewVBox.getScene().getWindow());
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


}
