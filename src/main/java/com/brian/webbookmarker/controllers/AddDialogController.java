package com.brian.webbookmarker.controllers;

import com.brian.webbookmarker.models.ExcelData;
import com.brian.webbookmarker.models.ExcelDataItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddDialogController implements Initializable {
    @FXML
    public ComboBox<String> categoryChoiceBox;
    @FXML
    private TextField titleField;
    @FXML
    private TextField urlAddressField;
    @FXML
    private TextField descriptionField;

    private final ObservableList<String> categories = FXCollections.observableArrayList();

    public void processResults() {
        String title = titleField.getText().trim();
        String url = urlAddressField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = categoryChoiceBox.getValue();
        ExcelData.getInstance().getCategories().add(category);
        ExcelData.incrementFinalEntryID();

        ExcelDataItem bookmark = new ExcelDataItem(ExcelData.getFinalEntryID(), title, url, description, category);
        ExcelData.getInstance().addItem(bookmark);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categories.addAll(ExcelData.getInstance().getCategories());
        categoryChoiceBox.setItems(categories);
        categoryChoiceBox.setEditable(true);
    }
}
