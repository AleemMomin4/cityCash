package com.assignment.citycash.model;

public class ItemModel {
    private String Id, Category_id, Name, Category_name, Description, Price, Qty, Image;
    private SortPropsModel sortPropsModel;

    public ItemModel(String id, String category_id, String name, String category_name,
                     String description, String price, String qty, String image) {
        Id = id;
        Category_id = category_id;
        Name = name;
        Category_name = category_name;
        Description = description;
        Price = price;
        Qty = qty;
        Image = image;
    }

    public String getId() {
        return Id;
    }

    public String getCategory_id() {
        return Category_id;
    }

    public String getName() {
        return Name;
    }

    public String getCategory_name() {
        return Category_name;
    }

    public String getDescription() {
        return Description;
    }

    public String getPrice() {
        return Price;
    }

    public String getQty() {
        return Qty;
    }

    public String getImage() {
        return Image;
    }

    public SortPropsModel getSortPropsModel() {
        return sortPropsModel;
    }

    public void setSortPropsModel(SortPropsModel sortPropsModel) {
        this.sortPropsModel = sortPropsModel;
    }
}
