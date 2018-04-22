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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

class AdminGUI extends JFrame implements WindowListener, ActionListener {

    private final Controller controller;
    private final UserGUI userGUI;
    private final JPanel productsStoragePanel;
    private final JPanel coinsPanel;
    private final HashMap<String, String> storageWithIDs;
    private ProductRefillStorageControlDialog productRefillStorageControlDialog;
    private CoinRefillStorageControlDialog coinRefillStorageControlDialog;
    private EmptyProductStorageDialog emptyProductStorageDialog;
    private EmptyCoinStorageDialog emptyCoinStorageDialog;

    public AdminGUI(String title, Controller controller, UserGUI userGUI) throws HeadlessException {
        super(title);
        this.controller = controller;
        this.userGUI = userGUI;
        productsStoragePanel = new JPanel();
        storageWithIDs = new HashMap<>();
        productRefillStorageControlDialog = new ProductRefillStorageControlDialog(this);
        coinRefillStorageControlDialog = new CoinRefillStorageControlDialog(this);
        emptyCoinStorageDialog = new EmptyCoinStorageDialog(this);
        emptyProductStorageDialog = new EmptyProductStorageDialog(this);
        coinsPanel = new JPanel();
        setSize(1200, 800);
        setLocation(500, 200);
        setLayout(new GridBagLayout());
        setVisible(false);
        setResizable(false);
        addWindowListener(this);

        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("Storage");
        menu.setMnemonic(KeyEvent.VK_1);
        menuBar.add(menu);

        menuItem = new JMenuItem("Refill");
        menuItem.setName("PRefill");
        menuItem.setActionCommand("PRefill");
        menuItem.getAccessibleContext().setAccessibleDescription("Refill a Product Storage");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Empty");
        menuItem.setName("PEmpty");
        menuItem.setActionCommand("PEmpty");
        menuItem.getAccessibleContext().setAccessibleDescription("Empty a Product Storage");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuBar.add(menu);

        menu = new JMenu("Coins");
        menu.setMnemonic(KeyEvent.VK_2);
        menuBar.add(menu);

        menuItem = new JMenuItem("Refill");
        menuItem.setName("CRefill");
        menuItem.setActionCommand("CRefill");
        menuItem.getAccessibleContext().setAccessibleDescription("Refill a Coin Storage");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Empty");
        menuItem.setName("CEmpty");
        menuItem.setActionCommand("CEmpty");
        menuItem.getAccessibleContext().setAccessibleDescription("Empty a Coin Storage");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        setJMenuBar(menuBar);
        build();
    }


    private void build() {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;
        rebuildProductsStoragePanel();
        productsStoragePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Products Storage"));
        add(productsStoragePanel, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 2;
        rebuildCoinsPanel();
        coinsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Coins Storage"));
        add(coinsPanel, constraints);

    }


    public void rebuildProductsStoragePanel() {
        productsStoragePanel.removeAll();
        AtomicInteger i = new AtomicInteger(1);
        controller.getStorage().values().forEach(productStorage -> {
            storageWithIDs.put(String.valueOf(i.get()), productStorage.getId());
            String s = "Storage #" + String.valueOf(i.getAndAdd(1)) + "\n" +
                    "Product: " + controller.getProducts().get(productStorage.getProduct()).getName() + "\n" +
                    "Quantity: " + String.valueOf(productStorage.getQuantity()) + "\n" +
                    "Capacity: " + String.valueOf(productStorage.getCapacity());


            JTextArea temp = new JTextArea(s, 4, 17);
            temp.setAutoscrolls(false);
            temp.setForeground(Color.BLACK);
            temp.setLineWrap(true);
            temp.setEditable(false);
            temp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
            productsStoragePanel.add(temp);
        });


    }

    public void rebuildCoinsPanel() {
        coinsPanel.removeAll();
        controller.getCoinManager().getCoinsStorage().values().forEach(coin -> {

            String s = "Coin " + String.valueOf(coin.getValue()) + "\n" +
                    "Quantity: " + String.valueOf(coin.getQuantity());

            JTextArea temp = new JTextArea(s, 2, 10);
            temp.setAutoscrolls(false);
            temp.setForeground(Color.BLUE);
            temp.setLineWrap(true);
            temp.setEditable(false);
            temp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
            coinsPanel.add(temp);
        });
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        userGUI.setVisible(true);
        userGUI.rebuildProductsPanel();
        userGUI.revalidate();
        userGUI.doLayout();
        userGUI.getLoginDialog().setVisible(false);
        userGUI.getLoginDialog().dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "PRefill":
                productRefillStorageControlDialog = new ProductRefillStorageControlDialog(productRefillStorageControlDialog, controller, storageWithIDs);
                productRefillStorageControlDialog.setVisible(true);
                break;
            case "CRefill":
                coinRefillStorageControlDialog = new CoinRefillStorageControlDialog(coinRefillStorageControlDialog, controller);
                coinRefillStorageControlDialog.setVisible(true);
                break;
            case "PEmpty":
                emptyProductStorageDialog = new EmptyProductStorageDialog(emptyProductStorageDialog, controller, storageWithIDs);
                emptyProductStorageDialog.setVisible(true);
                break;
            case "CEmpty":
                emptyCoinStorageDialog = new EmptyCoinStorageDialog(emptyCoinStorageDialog, controller);
                emptyCoinStorageDialog.setVisible(true);
                break;
        }
    }
}
