package com.warehouse.model;

public class Product {

    private int productId;
    private String name;
    private String category;
    private int quantity;
    private int reorderLevel;
    private double price;

    public Product(int productId, String name, String category,
                   int quantity, int reorderLevel, double price) {

        this.productId = productId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public double getPrice() {
        return price;
    }
}