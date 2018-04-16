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
import cut.cis352.coin.OnActionCallback;
import cut.cis352.coin.Transaction;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

public class UserGUI extends JFrame {

    private final Controller controller;
    private JPanel productsPanel;
    private final CoinPanelManager coinManager;
    private ProductConfirmDialog selectedProductConfirmDialog;
    private ProductConfirmDialog dispenseProductConfirmDialog;
    private final NumberFormat moneyTextFormat = new DecimalFormat("#0.00");
    private final AdminGUI adminGUI;
    private final LoginDialog loginDialog = new LoginDialog(this) {
        @Override
        public void onAuthenticated() {
            System.out.println("Authenticated");
            adminGUI.setController(controller);
            adminGUI.build();
            loginDialog.setVisible(false);
            loginDialog.reset();


        }
    };

    public UserGUI(String title, Controller controller) throws HeadlessException {
        this.controller = controller;

        productsPanel = new JPanel();
        coinManager = new CoinPanelManager(onActionCallback);
        selectedProductConfirmDialog = new ProductConfirmDialog(this, "Confirm Product selection", "Your selected product is ") {
            @Override
            public void onCancel() {
                productsPanel.setVisible(true);
            }

            @Override
            public void onConfirm() {
                productsPanel.setVisible(false);
                coinManager.setVisible(true);
                coinManager.getMoneyInserted().setText("0.00");
                controller.getCoinManager().setCurrentTransaction(new Transaction(selectedProductConfirmDialog.getSelectedProduct().getPrice()));
            }
        };

        dispenseProductConfirmDialog = new ProductConfirmDialog(this, "Dispense Confirmation", "Please pickup your ") {
            @Override
            public void onCancel() {
                dispenseProductConfirmDialog.setProduct(null);
                controller.getCoinManager().getCurrentTransaction().cancel();
                System.out.println(controller.getCoinManager().getCalculatedChangeCoins());
                selectedProductConfirmDialog.setProduct(null);
                dispenseProductConfirmDialog.setProduct(null);
                coinManager.setVisible(false);
                productsPanel.setVisible(true);
            }

            @Override
            public void onConfirm() {
                System.out.println(controller.getCoinManager().getCalculatedChangeCoins());
                controller.getDispenser().dispense(selectedProductConfirmDialog.getSelectedProduct().getName());
                controller.getDispenser().close();
                controller.decreaseStorage(selectedProductConfirmDialog.getSelectedProduct().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                selectedProductConfirmDialog.setProduct(null);
                dispenseProductConfirmDialog.setProduct(null);
                coinManager.setVisible(false);
                productsPanel.removeAll();
                buildProductsPanel();
                productsPanel.setVisible(true);
            }
        };


        GridLayout gridLayout = new GridLayout();
        setTitle(title);
        setEnabled(true);
        setSize(800, 800);
        setLocation(500, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 800));
        setLayout(new GridBagLayout());
        setResizable(false);
        this.adminGUI = new AdminGUI("Admin Dashboard",controller,this);
    }

    public void build() {
        GridBagConstraints constraints = new GridBagConstraints();
        coinManager.build();
        getContentPane().setBackground(Color.lightGray);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;
        add(buildProductsPanel(), constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;

        add(coinManager, constraints);

    }

    private JPanel buildProductsPanel() {

        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.BLACK), "Please select a product");
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        productsPanel.setBorder(titledBorder);
        productsPanel.setLayout(new GridLayout(2, 5, 5, 5));

        HashMap<String, Integer> quantities = new HashMap<>();

        controller.getStorage().forEach((productStorage) -> {
            if (quantities.containsKey(productStorage.getProduct().getName())) {
                int current_quantity = quantities.get(productStorage.getProduct().getName());
                quantities.replace(productStorage.getProduct().getName(), current_quantity + productStorage.getQuantity());
            } else {
                quantities.put(productStorage.getProduct().getName(), productStorage.getQuantity());
            }

        });

        controller.getProducts().forEach((id, product) -> {

            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(new File(product.getName() + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert bufferedImage != null;
            ImageIcon imageIcon = new ImageIcon(bufferedImage);
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            JButton b = new JButton();
            b.setBorder(new EmptyBorder(1, 1, 1, 1));
            b.setContentAreaFilled(false);
            b.setIcon(imageIcon);
            b.setName(product.getName());
            b.setActionCommand(String.valueOf(product.getId()));

            if (quantities.get(product.getName()) > 0) {
                b.setEnabled(true);
                b.setVisible(true);
                b.addActionListener(prodActionListener);
                b.setToolTipText(String.valueOf(moneyTextFormat.format(product.getPrice())) + " €");
                productsPanel.add(b);
            }

        });
        productsPanel.setBackground(Color.lightGray);
        productsPanel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK), "admin");
        productsPanel.getActionMap().put("admin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getCoinManager().getCoins().values().forEach(coin -> {
                    System.out.println(coin.toString());
                });

                controller.getStorage().forEach(productStorage -> {
                    System.out.println(productStorage.toString());
                });
                loginDialog.setVisible(true);

            }
        });
        return productsPanel;
    }


    public void showGui() {
        setVisible(true);
    }

    private final ActionListener prodActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e.getActionCommand());
            selectedProductConfirmDialog.setProduct(controller.getProducts().get(Integer.parseInt(e.getActionCommand())));
            coinManager.getInsertMoneyLabel().setText("You have selected " + selectedProductConfirmDialog.getSelectedProduct().getName() + ". Please insert: " + moneyTextFormat.format(selectedProductConfirmDialog.getSelectedProduct().getPrice()) + "€");
            selectedProductConfirmDialog.setVisible(true);
        }
    };

    private final OnActionCallback onActionCallback = new OnActionCallback() {
        @Override
        public void onCoinInserted(double value) {
            System.out.println(value);
            controller.getCoinManager().increaseCoinQuantity(value);

            if (controller.getCoinManager().checkCoin(value)) {
                if (controller.getCoinManager().getCurrentTransaction().onCoinInserted(value)) {
                    coinManager.getMoneyInserted().setText(String.valueOf(moneyTextFormat.format(controller.getCoinManager().getCurrentTransaction().getMoneyInserted())));
                    controller.getDispenser().open();
                    dispenseProductConfirmDialog.setProduct(selectedProductConfirmDialog.getSelectedProduct());
                    dispenseProductConfirmDialog.setVisible(true);
                } else {
                    coinManager.getMoneyInserted().setText(String.valueOf(moneyTextFormat.format(controller.getCoinManager().getCurrentTransaction().getMoneyInserted())));
                }


            }
        }

        @Override
        public void onTransactionCancel() {
            System.out.println("Cancelled");
            controller.getCoinManager().getCurrentTransaction().cancel();
            System.out.println(controller.getCoinManager().getCalculatedChangeCoins());
            selectedProductConfirmDialog.setProduct(null);
            coinManager.setVisible(false);
            productsPanel.setVisible(true);

        }
    };

    public LoginDialog getLoginDialog() {
        return loginDialog;
    }
}
