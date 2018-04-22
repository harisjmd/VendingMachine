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

class CoinRefillStorageControlDialog extends JDialog implements ActionListener {

    private static final Logger LOG = LogManager.getLogger();


    private JComboBox<String> coinStorageList;
    private JTextField quantity;
    private Controller controller;
    private final JButton confirm_btn;
    private final JButton cancel_btn;
    private final AdminGUI adminGUI;

    public CoinRefillStorageControlDialog(AdminGUI owner) {
        super(owner);
        adminGUI = owner;
        cancel_btn = null;
        confirm_btn = null;
    }

    public CoinRefillStorageControlDialog(CoinRefillStorageControlDialog dialog, Controller controller) {
        super(dialog.getOwner(), "Coin Refill");
        adminGUI = dialog.adminGUI;
        this.controller = controller;
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
        coinStorageList = new JComboBox<>();
        controller.getCoinManager().getCoinsStorage().values().forEach(p -> coinStorageList.addItem(String.valueOf(p.getValue())));
        add(coinStorageList, cs);

        quantity = new JTextField();
        quantity.setColumns(10);
        cs.anchor = GridBagConstraints.CENTER;
        cs.gridx = 0;
        cs.gridy = 2;
        cs.weighty = 0.05;
        cs.weightx = 0.05;
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


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(confirm_btn)) {
            String coin = (String) coinStorageList.getSelectedItem();
            String q = quantity.getText();
            int coinID = -1;
            if (coin != null) {
                coinID = controller.getCoinManager().getCoinId(Double.parseDouble(coin));
                controller.getCoinManager().getCoinsStorage().get(coinID).setQuantity(controller.getCoinManager().getCoinsStorage().get(coinID).getQuantity() + Integer.parseInt(q.trim()));
                LOG.info("Coin refill of " + coin + " (" + coinID + ") with " + q + " coins");
            }

            adminGUI.rebuildCoinsPanel();
            adminGUI.revalidate();
            adminGUI.doLayout();


            //noinspection Duplicates
            if (controller.getDriver().isConnected() && coinID != -1) {
                try {
                    controller.getDriver().updateCoinQuantity(controller.getVm_id(), coinID, controller.getCoinManager().getCoinsStorage().get(coinID).getQuantity());
                } catch (SQLException e1) {
                    LOG.fatal("Failed Updating Coin storage for " + coin + " to db");
                    System.exit(1);
                }

                LOG.info("Updated Coin storage for " + coin + " to db");
            }
            if (!controller.getCoinManager().saveCoinsStorageLocal()) {
                System.exit(1);
            }


        }

        this.setVisible(false);
    }
}
