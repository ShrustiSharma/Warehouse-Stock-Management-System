
package com.warehouse.dao;

import com.warehouse.db.DBConnection;
import com.warehouse.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 1. Add a new product
    public boolean addProduct(Product product) throws SQLException {

        String sql = "INSERT INTO products "
                + "(name, category, quantity, reorder_level, price) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setInt(4, product.getReorderLevel());
            ps.setDouble(5, product.getPrice());

            return ps.executeUpdate() > 0;
        }
    }

    // 2. Display all products
    public List<Product> getAllProducts() throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getInt("reorder_level"),
                    rs.getDouble("price")
                );

                products.add(product);
            }
        }

        return products;
    }

    // 3. Update an existing product
    public boolean updateProduct(Product product) throws SQLException {

        String sql = "UPDATE products SET name=?, category=?, "
                + "quantity=?, reorder_level=?, price=? "
                + "WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setInt(4, product.getReorderLevel());
            ps.setDouble(5, product.getPrice());
            ps.setInt(6, product.getProductId());

            return ps.executeUpdate() > 0;
        }
    }

    // 4. Delete a product
    public boolean deleteProduct(int productId) throws SQLException {

        String sql = "DELETE FROM products WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            return ps.executeUpdate() > 0;
        }
    }

    // 5. Stock In - Increase product quantity
    public boolean stockIn(int productId, int qty) throws SQLException {

        if (qty <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        String updateSQL =
                "UPDATE products SET quantity = quantity + ? "
                + "WHERE product_id = ?";

        String insertSQL =
                "INSERT INTO stock_transactions "
                + "(product_id, type, qty) VALUES (?, 'IN', ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                        con.prepareStatement(updateSQL)) {

                    ps.setInt(1, qty);
                    ps.setInt(2, productId);

                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(insertSQL)) {

                    ps.setInt(1, productId);
                    ps.setInt(2, qty);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // 6. Stock Out - Decrease product quantity
    public boolean stockOut(int productId, int qty) throws SQLException {

        if (qty <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        String updateSQL =
                "UPDATE products SET quantity = quantity - ? "
                + "WHERE product_id = ? AND quantity >= ?";

        String insertSQL =
                "INSERT INTO stock_transactions "
                + "(product_id, type, qty) VALUES (?, 'OUT', ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                        con.prepareStatement(updateSQL)) {

                    ps.setInt(1, qty);
                    ps.setInt(2, productId);
                    ps.setInt(3, qty);

                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                        con.prepareStatement(insertSQL)) {

                    ps.setInt(1, productId);
                    ps.setInt(2, qty);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // 7. Get products requiring reorder
    public List<Product> getLowStockProducts() throws SQLException {

        List<Product> lowStockProducts = new ArrayList<>();

        String sql =
                "SELECT * FROM products WHERE quantity <= reorder_level";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getInt("reorder_level"),
                    rs.getDouble("price")
                );

                lowStockProducts.add(product);
            }
        }

        return lowStockProducts;
    }
}
