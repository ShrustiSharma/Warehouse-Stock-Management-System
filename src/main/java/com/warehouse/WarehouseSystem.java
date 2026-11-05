
package com.warehouse;

import com.warehouse.ui.MainFrame;
import javax.swing.SwingUtilities;

public class WarehouseSystem {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame();
            frame.setVisible(true);

        });
    }
}
