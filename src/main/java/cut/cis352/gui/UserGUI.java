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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class UserGUI extends JFrame {

    private static final Logger LOG = LogManager.getLogger();

    private final Random random = new Random();
    private final Controller controller;
    private final JPanel productsPanel;
    private final CoinPanelManager coinManager;
    private ProductConfirmDialog selectedProductConfirmDialog;
    private ProductConfirmDialog dispenseProductConfirmDialog;
    private final NumberFormat moneyTextFormat = new DecimalFormat("#0.00");
    private final AdminGUI adminGUI;
    private final LoginDialog loginDialog = new LoginDialog(this) {
        @Override
        public void onAuthenticated() {
            LOG.info("Admin Authenticated");
//            adminGUI.setController(controller);
            adminGUI.rebuildCoinsPanel();
            adminGUI.rebuildProductsStoragePanel();
            adminGUI.setVisible(true);
            hid();
            loginDialog.setVisible(false);
            loginDialog.reset();


        }
    };

    public UserGUI(String title, Controller controller) throws HeadlessException {
        this.controller = controller;

        productsPanel = new JPanel();
        OnActionCallback onActionCallback = new OnActionCallback() {
            @Override
            public void onCoinInserted(int coin_id) {
                LOG.info("Inserted Coin id: " + coin_id + " with value " + controller.getCoinManager().getCoinsStorage().get(coin_id).getValue());

                if (controller.getCoinManager().checkCoin(controller.getCoinManager().getCoinsStorage().get(coin_id).getValue())) {
                    controller.getCoinManager().increaseCoinQuantity(coin_id);
                    if (controller.getCoinManager().getCurrentTransaction().onCoinInserted(controller.getCoinManager().getCoinsStorage().get(coin_id).getValue())) {
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
                LOG.warn("Transaction Cancelled");
                controller.getCoinManager().getCurrentTransaction().cancel();
                if (controller.getDriver().isConnected()) {

                    Transaction transaction = controller.getCoinManager().getCurrentTransaction();
                    try {
                        controller.getDriver().updateTransaction(
                                transaction.getId(),
                                null,
                                transaction.getMoneyInserted(),
                                transaction.getChange(),
                                true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        LOG.fatal("Failed Updating Transaction " + transaction.getId() + " to db");
                        System.exit(1);
                    }

                    LOG.info("Updated Transaction " + transaction.getId() + " to db");
                }
                LOG.info("Change: " + controller.getCoinManager().getCalculatedChangeCoins());
                selectedProductConfirmDialog.setProduct(null);
                coinManager.setVisible(false);
                productsPanel.setVisible(true);

            }
        };
        coinManager = new CoinPanelManager(controller.getCoinManager().getCoinsStorage(), onActionCallback);
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
                Transaction transaction = new Transaction(selectedProductConfirmDialog.getSelectedProduct().getId(), selectedProductConfirmDialog.getSelectedProduct().getPrice());
                controller.getCoinManager().setCurrentTransaction(transaction);

                if (controller.getDriver().isConnected()) {
                    try {
                        int id = controller.getDriver().insertTransaction(
                                selectedProductConfirmDialog.getSelectedProduct().getId(),
                                transaction.getStorage_id(),
                                controller.getVm_id(),
                                controller.getCoinManager().getCurrentTransaction().getMoneyInserted(),
                                controller.getCoinManager().getCurrentTransaction().getChange(),
                                controller.getDateFormat().format(controller.getCoinManager().getCurrentTransaction().getCreated()),
                                controller.getCoinManager().getCurrentTransaction().getCompleted() == null ? null : controller.getDateFormat().format(controller.getCoinManager().getCurrentTransaction().getCompleted()),
                                controller.getCoinManager().getCurrentTransaction().isCanceled()
                        );

                        if (id != -1) {
                            controller.getCoinManager().getCurrentTransaction().setId(id);
                        } else {
                            controller.getCoinManager().getCurrentTransaction().setId(new Random().nextInt());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        LOG.fatal("Failed Inserting Transaction " + transaction.getId() + " to db");
                        System.exit(1);
                    }

                    LOG.info("Inserted Transaction " + transaction.getId() + " to db");
                } else {

                    int i = random.nextInt();

                    while (controller.getTransactions().containsKey(i)) {
                        i = random.nextInt();
                    }

                    controller.getCoinManager().getCurrentTransaction().setId(i);
                    // insert in transactions + save to transactions file
                    controller.getTransactions().put(i, controller.getCoinManager().getCurrentTransaction());
                    LOG.info("Inserted Transaction " + transaction.getId() + " locally");

                    if (!controller.saveTransactions()) {
                        LOG.fatal("Failed saving Transactions locally");
                        System.exit(1);
                    } else {
                        LOG.info("Saved Transactions locally");
                    }
                }
            }
        };

        dispenseProductConfirmDialog = new ProductConfirmDialog(this, "Dispense Confirmation", "Please pickup your ") {
            @Override
            public void onCancel() {
                LOG.warn("Transaction Canceled");
                dispenseProductConfirmDialog.setProduct(null);
                controller.getCoinManager().getCurrentTransaction().cancel();
                controller.getCoinManager().getCurrentTransaction().setCompleted(new Date());
                LOG.info("Change: " + controller.getCoinManager().getCalculatedChangeCoins());
                Transaction transaction = controller.getCoinManager().getCurrentTransaction();
                transaction.setStorage_id(null);

                if (controller.getDriver().isConnected()) {

                    try {
                        controller.getDriver().updateTransaction(
                                transaction.getId(),
                                transaction.getStorage_id(),
                                transaction.getMoneyInserted(),
                                transaction.getChange(),
                                transaction.isCanceled());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        LOG.fatal("Failed Updating Transaction " + transaction.getId() + " to db");
                        System.exit(1);
                    }

                    LOG.info("Updated Transaction " + transaction.getId() + " to db");
                } else {
                    // update in transactions + save to transactions file
                    controller.getTransactions().replace(transaction.getId(), transaction);
                    LOG.info("Updated Transaction " + transaction.getId() + " locally");

                    if (!controller.saveTransactions()) {
                        LOG.fatal("Failed saving Transactions locally");
                        System.exit(1);
                    } else {
                        LOG.info("Saved Transactions locally");
                    }
                }
                selectedProductConfirmDialog.setProduct(null);
                dispenseProductConfirmDialog.setProduct(null);
                coinManager.setVisible(false);
                productsPanel.setVisible(true);

            }

            @Override
            public void onConfirm() {
                LOG.info("Change: " + controller.getCoinManager().getCalculatedChangeCoins());
                controller.getDispenser().dispense(selectedProductConfirmDialog.getSelectedProduct().getName());
                controller.getDispenser().close();
                controller.getCoinManager().getCurrentTransaction().setCompleted(new Date());
                controller.getCoinManager().getCurrentTransaction().setStorage_id(controller.decreaseStorage(selectedProductConfirmDialog.getSelectedProduct().getId()));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Transaction transaction = controller.getCoinManager().getCurrentTransaction();
                if (controller.getDriver().isConnected()) {

                    if (transaction.getStorage_id() != null) {

                        try {
                            controller.getDriver().updateTransaction(
                                    transaction.getId(),
                                    transaction.getStorage_id(),
                                    transaction.getMoneyInserted(),
                                    transaction.getChange(),
                                    transaction.isCanceled());
                        } catch (SQLException e) {
                            e.printStackTrace();
                            LOG.fatal("Failed saving Transactions in db");
                            System.exit(1);
                        }
                        LOG.info("Saved Transactions to db");
                    }
                } else {
                    // update in transactions + save to transactions file
                    controller.getTransactions().replace(transaction.getId(), transaction);
                    if (!controller.saveTransactions()) {
                        System.exit(1);
                    }
                }

                selectedProductConfirmDialog.setProduct(null);
                dispenseProductConfirmDialog.setProduct(null);
                coinManager.setVisible(false);
                productsPanel.removeAll();
                rebuildProductsPanel();
                revalidate();
                doLayout();
                productsPanel.setVisible(true);
            }
        };


        setTitle(title);
        setEnabled(true);
        setSize(800, 800);
        setLocation(500, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 800));
        setLayout(new GridBagLayout());
        setResizable(false);
        this.adminGUI = new AdminGUI("Admin Dashboard", controller, this);
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
        rebuildProductsPanel();
        add(productsPanel, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;

        add(coinManager, constraints);

    }

    public void rebuildProductsPanel() {

        productsPanel.removeAll();
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.BLACK), "Please select a product");
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        productsPanel.setBorder(titledBorder);
        productsPanel.setLayout(new GridLayout(2, 5, 5, 5));

        HashMap<String, Integer> quantities = new HashMap<>();

        controller.getStorage().values().forEach((productStorage) -> {
            if (quantities.containsKey(controller.getProducts().get(productStorage.getProduct()).getName())) {
                int current_quantity = quantities.get(controller.getProducts().get(productStorage.getProduct()).getName());
                quantities.replace(controller.getProducts().get(productStorage.getProduct()).getName(), current_quantity + productStorage.getQuantity());
            } else {
                quantities.put(controller.getProducts().get(productStorage.getProduct()).getName(), productStorage.getQuantity());
            }

        });

        controller.getProducts().forEach((id, product) -> {

            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(UserGUI.class.getResourceAsStream("p" + product.getId() + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
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

            if (quantities.get(product.getName()) != null && quantities.get(product.getName()) > 0) {
                b.setEnabled(true);
                b.setVisible(true);
                b.addActionListener(prodActionListener);
                b.setToolTipText(String.valueOf(moneyTextFormat.format(product.getPrice())) + " " + controller.getVmProperties().getProperty("vm.currency"));
                productsPanel.add(b);
            }

        });
        productsPanel.setBackground(Color.lightGray);
        this.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK), "admin");
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK), "admin");
        this.getRootPane().getActionMap().put("admin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginDialog.setVisible(true);

            }
        });
    }

    public void showGui() {
        setVisible(true);
    }

    private final ActionListener prodActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Selected Product: " + controller.getProducts().get(Integer.parseInt(e.getActionCommand())).getName());
            selectedProductConfirmDialog.setProduct(controller.getProducts().get(Integer.parseInt(e.getActionCommand())));
            coinManager.getInsertMoneyLabel().setText("You have selected " + selectedProductConfirmDialog.getSelectedProduct().getName() + ". Please insert: " + moneyTextFormat.format(selectedProductConfirmDialog.getSelectedProduct().getPrice()) + controller.getVmProperties().getProperty("vm.currency"));
            selectedProductConfirmDialog.setVisible(true);
        }
    };

    public LoginDialog getLoginDialog() {
        return loginDialog;
    }

    private void hid() {
        this.setVisible(false);
    }
}
