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

public class EditDialogController implements Initializable {
    @FXML
    public ComboBox<String> categoryChoiceBox;
    @FXML
    private TextField titleField;
    @FXML
    private TextField urlAddressField;
    @FXML
    private TextField descriptionField;

    private final ObservableList<String> categories = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categories.addAll(ExcelData.getInstance().getCategories());
        categoryChoiceBox.setItems(categories);
        categoryChoiceBox.setEditable(true);
    }
    public void initData(ExcelDataItem item){
        titleField.setText(item.getTitle());
        urlAddressField.setText(item.getUrlAddress());
        descriptionField.setText(item.getDescription());
        categoryChoiceBox.getSelectionModel().select(item.getCategory());
    }

    public void processResults(ExcelDataItem item) {
        item.setTitle(titleField.getText().trim());
        item.setUrlAddress(urlAddressField.getText().trim());
        item.setDescription(descriptionField.getText().trim());
        item.setCategory(categoryChoiceBox.getValue());
    }
}
