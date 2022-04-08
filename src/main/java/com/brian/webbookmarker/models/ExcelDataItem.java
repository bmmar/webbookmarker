package com.brian.webbookmarker.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class ExcelDataItem {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty title = new SimpleStringProperty("");
    private final SimpleStringProperty urlAddress = new SimpleStringProperty("");
    private final SimpleStringProperty description = new SimpleStringProperty("");
    private final SimpleStringProperty category = new SimpleStringProperty("");

    public ExcelDataItem() {
        this(0, "", "", "", "");
    }

    public ExcelDataItem(int id, String title, String urlAddress, String description, String category) {
        setId(id);
        setTitle(title);
        setUrlAddress(urlAddress);
        setDescription(description);
        setCategory(category);
    }

    public ExcelDataItem(List<String> item) {
        setId(Integer.parseInt(item.get(0)));
        setTitle(item.get(1));
        setUrlAddress(item.get(2));
        setDescription(item.get(3));
        setCategory(item.get(4));
    }


    public int getId() {
        return id.get();
    }

    public String getIdAsString() {
        return String.valueOf(id.get());
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getUrlAddress() {
        return urlAddress.get();
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress.set(urlAddress);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getCategory() {
        return category.get();
    }


    @Override
    public String toString() {
        return getTitle();
    }

}
