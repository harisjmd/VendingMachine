/*
 * Copyright 2018 Charalampos Kozis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cut.cis352.gui;

import cut.cis352.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

class ProductRefillStorageControlDialog extends JDialog implements ActionListener {

    private static final Logger LOG = LogManager.getLogger();

    private JComboBox<String> productsList;
    private JComboBox<String> storageList;
    private JComboBox<Integer> quantity;
    private Controller controller;
    private HashMap<String, String> storageWithIDs;
    private final JButton confirm_btn;
    private final JButton cancel_btn;
    private final AdminGUI adminGUI;

    public ProductRefillStorageControlDialog(AdminGUI owner) {
        super(owner);
        adminGUI = owner;
        cancel_btn = null;
        confirm_btn = null;
    }

    public ProductRefillStorageControlDialog(ProductRefillStorageControlDialog dialog, Controller controller, HashMap<String, String> storageWithIDs) {
        super(dialog.getOwner(), "Product Refill");
        adminGUI = dialog.adminGUI;
        this.controller = controller;
        this.storageWithIDs = storageWithIDs;
        setSize(300, 180);
        setPreferredSize(new Dimension(200, 180));
        setLayout(new GridBagLayout());
        this.confirm_btn = new JButton("Confirm");
        this.cancel_btn = new JButton("Cancel");
        cancel_btn.addActionListener(this);
        confirm_btn.addActionListener(this);

        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.anchor = GridBagConstraints.PAGE_START;
        cs.gridx = 0;
        cs.gridy = 1;
        cs.weighty = 0.1;
        cs.weightx = 0.1;
        productsList = new JComboBox<>();
        storageList = new JComboBox<>();
        quantity = new JComboBox<>();

        controller.getProducts().values().forEach(p -> productsList.addItem(String.valueOf(p.getId()) + " - " + p.getName()));

        add(productsList, cs);

        cs.anchor = GridBagConstraints.CENTER;
        cs.gridx = 0;
        cs.gridy = 2;
        cs.weighty = 0.05;
        cs.weightx = 0.05;
        storageWithIDs.forEach((i, id) -> storageList.addItem("Storage #" + i));
        add(storageList, cs);
        storageList.addItemListener(e -> rebuildQuantity(storageWithIDs.get(((String) e.getItem()).split("#")[1])));
        cs.anchor = GridBagConstraints.CENTER;
        cs.gridx = 0;
        cs.gridy = 3;
        cs.weighty = 0.03;
        cs.weightx = 0.03;
        add(quantity, cs);
        cs.anchor = GridBagConstraints.PAGE_END;
        cs.gridx = 0;
        cs.gridy = 4;
        cs.weighty = 0.01;
        cs.weightx = 0.01;
        JPanel bp = new JPanel();
        bp.add(confirm_btn);
        bp.add(cancel_btn);
        add(bp, cs);
        setResizable(false);
        setLocationRelativeTo(dialog.getOwner());
        setVisible(false);

    }

    private void rebuildQuantity(String s) {
        quantity.removeAllItems();
        int c = controller.getStorage().get(s).getCapacity() - controller.getStorage().get(s).getQuantity();
        quantity.setVisible(c > 0);
        for (int i = 1; i <= c; i++) {
            quantity.addItem(i);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(confirm_btn)) {
            String selectedStorageId = ((String) Objects.requireNonNull(storageList.getSelectedItem())).split("#")[1];
            String storage = storageWithIDs.get(selectedStorageId);
            int product = Integer.parseInt(((String) Objects.requireNonNull(productsList.getSelectedItem())).split("-")[0].trim());
            int q = ((int) quantity.getSelectedItem());
            controller.getStorage().get(storage).refill(product, q);
            adminGUI.rebuildProductsStoragePanel();
            adminGUI.revalidate();
            adminGUI.doLayout();
            LOG.info("Product refill at storage #" + selectedStorageId + " - " + storage + " with " + String.valueOf(q) + " " + controller.getProducts().get(product).getName());

            //noinspection Duplicates
            if (controller.getDriver().isConnected()) {
                try {
                    controller.getDriver().updateStorage(storage, controller.getStorage().get(storage).getProduct(), controller.getStorage().get(storage).getQuantity());
                } catch (SQLException e1) {
                    LOG.fatal("Failed Updating Product storage " + storage + " to db");
                    System.exit(1);
                }

                LOG.info("Updated Product storage " + storage + " to db");
            }
            if (!controller.saveStorageLocal()) {
                System.exit(1);
            }

        }
        this.setVisible(false);

    }
}
