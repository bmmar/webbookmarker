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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
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



    private Parent myParent;
    private Stage stage;
    private Scene scene;

    public void initialize() {
        ObservableList<String> catList;
        List<ExcelDataItem> fullTitlesList =  new ArrayList<>();
        ObservableList<ExcelDataItem> selectedList = FXCollections.observableArrayList();
        catList = FXCollections.observableArrayList();

        categoryListView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<? super String>) (observable, oldValue, newValue) -> {
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
                    idTextField.setText(bookmarkItem.getIdAsString());
                    titleTextField.setText(bookmarkItem.getTitle());
                    urlTextField.setText(bookmarkItem.getUrlAddress());
                    descriptionTextArea.setText(bookmarkItem.getDescription());
                    categoryTextField.setText(bookmarkItem.getCategory());

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
