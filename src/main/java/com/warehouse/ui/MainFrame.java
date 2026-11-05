
package com.warehouse.ui;

import com.warehouse.dao.ProductDAO;
import com.warehouse.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {

    private final ProductDAO dao = new ProductDAO();

    private JTextField idField, nameField, categoryField;
    private JTextField quantityField, reorderField, priceField;

    private JTextField stockIdField, stockQtyField;

    private DefaultTableModel productModel, alertModel;

    private JTable productTable, alertTable;

    public MainFrame() {

        setTitle("Warehouse & Stock Management System");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Product Management", createProductPanel());
        tabs.addTab("Stock Management", createStockPanel());
        tabs.addTab("Reorder Alerts", createAlertPanel());

        add(tabs);

        refreshAll();
    }

    // PRODUCT MANAGEMENT PANEL
    private JPanel createProductPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));

        idField = new JTextField();
        idField.setEditable(false);

        nameField = new JTextField();
        categoryField = new JTextField();
        quantityField = new JTextField();
        reorderField = new JTextField();
        priceField = new JTextField();

        form.add(new JLabel("Product ID:"));
        form.add(idField);

        form.add(new JLabel("Product Name:"));
        form.add(nameField);

        form.add(new JLabel("Category:"));
        form.add(categoryField);

        form.add(new JLabel("Quantity:"));
        form.add(quantityField);

        form.add(new JLabel("Reorder Level:"));
        form.add(reorderField);

        form.add(new JLabel("Price:"));
        form.add(priceField);

        productModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Category",
                    "Quantity", "Reorder Level", "Price"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productModel);

        productTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()
                    && productTable.getSelectedRow() != -1) {

                int row = productTable.getSelectedRow();

                idField.setText(productModel.getValueAt(row, 0).toString());
                nameField.setText(productModel.getValueAt(row, 1).toString());
                categoryField.setText(productModel.getValueAt(row, 2).toString());
                quantityField.setText(productModel.getValueAt(row, 3).toString());
                reorderField.setText(productModel.getValueAt(row, 4).toString());
                priceField.setText(productModel.getValueAt(row, 5).toString());
            }
        });

        JButton addButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearFields());

        JPanel buttons = new JPanel();

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        return panel;
    }

    // STOCK MANAGEMENT PANEL
    private JPanel createStockPanel() {

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.setBorder(BorderFactory.createEmptyBorder(80, 150, 80, 150));

        stockIdField = new JTextField();
        stockQtyField = new JTextField();

        JButton stockInButton = new JButton("Stock In");
        JButton stockOutButton = new JButton("Stock Out");

        panel.add(new JLabel("Product ID:"));
        panel.add(stockIdField);

        panel.add(new JLabel("Quantity:"));
        panel.add(stockQtyField);

        panel.add(stockInButton);
        panel.add(stockOutButton);

        stockInButton.addActionListener(e -> stockIn());
        stockOutButton.addActionListener(e -> stockOut());

        return panel;
    }

    // REORDER ALERT PANEL
    private JPanel createAlertPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        alertModel = new DefaultTableModel(
                new String[]{"Product ID", "Name",
                    "Available Quantity", "Reorder Level"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        alertTable = new JTable(alertModel);

        JButton refreshButton = new JButton("Refresh Alerts");
        refreshButton.addActionListener(e -> loadLowStockProducts());

        panel.add(new JScrollPane(alertTable), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    // ADD PRODUCT
    private void addProduct() {

        try {

            Product product = readProduct(0);

            if (dao.addProduct(product)) {
                JOptionPane.showMessageDialog(this,
                        "Product added successfully!");

                clearFields();
                refreshAll();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // UPDATE PRODUCT
    private void updateProduct() {

        try {

            int id = Integer.parseInt(idField.getText());

            Product product = readProduct(id);

            if (dao.updateProduct(product)) {
                JOptionPane.showMessageDialog(this,
                        "Product updated successfully!");

                clearFields();
                refreshAll();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // DELETE PRODUCT
    private void deleteProduct() {

        try {

            int id = Integer.parseInt(idField.getText());

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this product?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                if (dao.deleteProduct(id)) {
                    JOptionPane.showMessageDialog(this,
                            "Product deleted successfully!");

                    clearFields();
                    refreshAll();
                }
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // STOCK IN
    private void stockIn() {

        try {

            int id = Integer.parseInt(stockIdField.getText());
            int qty = Integer.parseInt(stockQtyField.getText());

            if (dao.stockIn(id, qty)) {

                JOptionPane.showMessageDialog(this,
                        "Stock added successfully!");

                stockIdField.setText("");
                stockQtyField.setText("");

                refreshAll();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Product ID not found!");
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // STOCK OUT
    private void stockOut() {

        try {

            int id = Integer.parseInt(stockIdField.getText());
            int qty = Integer.parseInt(stockQtyField.getText());

            if (dao.stockOut(id, qty)) {

                JOptionPane.showMessageDialog(this,
                        "Stock issued successfully!");

                stockIdField.setText("");
                stockQtyField.setText("");

                refreshAll();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Product not found or insufficient stock!");
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // READ PRODUCT DETAILS
    private Product readProduct(int id) {

        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();

        if (name.isEmpty() || category.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name and category are required.");
        }

        int quantity = Integer.parseInt(quantityField.getText());
        int reorder = Integer.parseInt(reorderField.getText());
        double price = Double.parseDouble(priceField.getText());

        if (quantity < 0 || reorder < 0 || price < 0) {
            throw new IllegalArgumentException(
                    "Quantity, reorder level and price cannot be negative.");
        }

        return new Product(id, name, category,
                quantity, reorder, price);
    }

    // LOAD ALL PRODUCTS
    private void loadProducts() {

        try {

            productModel.setRowCount(0);

            List<Product> products = dao.getAllProducts();

            for (Product p : products) {

                productModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    p.getQuantity(),
                    p.getReorderLevel(),
                    p.getPrice()
                });
            }

        } catch (SQLException e) {
            showError(e);
        }
    }

    // LOAD LOW STOCK PRODUCTS
    private void loadLowStockProducts() {

        try {

            alertModel.setRowCount(0);

            List<Product> products = dao.getLowStockProducts();

            for (Product p : products) {

                alertModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getQuantity(),
                    p.getReorderLevel()
                });
            }

        } catch (SQLException e) {
            showError(e);
        }
    }

    // REFRESH TABLES
    private void refreshAll() {

        if (productModel != null) {
            loadProducts();
        }

        if (alertModel != null) {
            loadLowStockProducts();
        }
    }

    // CLEAR INPUT FIELDS
    private void clearFields() {

        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        quantityField.setText("");
        reorderField.setText("");
        priceField.setText("");

        productTable.clearSelection();
    }

    // DISPLAY ERRORS
    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
