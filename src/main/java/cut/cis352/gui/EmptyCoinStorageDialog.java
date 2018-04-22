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

class EmptyCoinStorageDialog extends JDialog implements ActionListener {
    private static final Logger LOG = LogManager.getLogger();

    private JComboBox<String> coinStorageList;
    private Controller controller;
    private final JButton empty_btn;
    private final JButton cancel_btn;
    private final AdminGUI adminGUI;

    public EmptyCoinStorageDialog(AdminGUI owner) {
        super(owner);
        adminGUI = owner;
        cancel_btn = null;
        empty_btn = null;
    }

    public EmptyCoinStorageDialog(EmptyCoinStorageDialog dialog, Controller controller) {
        super(dialog.getOwner(), "Product Refill");
        adminGUI = dialog.adminGUI;
        this.controller = controller;
        setSize(300, 180);
        setPreferredSize(new Dimension(200, 180));
        setLayout(new GridBagLayout());
        this.empty_btn = new JButton("Empty");
        this.cancel_btn = new JButton("Cancel");
        cancel_btn.addActionListener(this);
        empty_btn.addActionListener(this);

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

        cs.anchor = GridBagConstraints.PAGE_END;
        cs.gridx = 0;
        cs.gridy = 4;
        cs.weighty = 0.01;
        cs.weightx = 0.01;
        JPanel bp = new JPanel();
        bp.add(empty_btn);
        bp.add(cancel_btn);
        add(bp, cs);
        setResizable(false);
        setLocationRelativeTo(dialog.getOwner());
        setVisible(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(empty_btn)) {
            String coin = (String) coinStorageList.getSelectedItem();
            int coinID = -1;

            if (coin != null) {
                coinID = controller.getCoinManager().getCoinId(Double.parseDouble(coin));
                controller.getCoinManager().getCoinsStorage().get(coinID).setQuantity(0);
                LOG.info("Emptied " + coin + " (" + coinID + ")");
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
