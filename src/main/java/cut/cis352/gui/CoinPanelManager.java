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

import cut.cis352.coin.Coin;
import cut.cis352.coin.OnActionCallback;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class CoinPanelManager extends JPanel {

    private final HashMap<Integer, Coin> coinsStorage;
    private final JLabel insertMoneyLabel;
    private final JPanel moneyPanel;
    private final JLabel moneyInserted;
    private final JButton cancelTransactionButton;
    private final OnActionCallback callback;

    public CoinPanelManager(HashMap<Integer, Coin> coinsStorage, OnActionCallback callback) {
        this.coinsStorage = coinsStorage;
        this.callback = callback;
        insertMoneyLabel = new JLabel();
        moneyPanel = new JPanel(new GridBagLayout());
        moneyPanel.setBackground(Color.lightGray);
        cancelTransactionButton = new JButton("Cancel");
        setLayout(new GridLayout(2, 1, 1, 1));
        setBackground(Color.lightGray);
        setVisible(false);
        moneyInserted = new JLabel("0.00");
        moneyInserted.setFont(new Font("Arial", Font.BOLD, 50));
        moneyInserted.setForeground(Color.RED);
        moneyInserted.setBackground(Color.BLACK);
        moneyInserted.setOpaque(true);
        moneyInserted.setSize(200, 100);
        moneyInserted.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    public void build() {
        buildInsertMoneyPanel();
        add(buildCoinsPanel());
        add(moneyPanel);
    }

    private JPanel buildCoinsPanel() {
        JPanel coinsPanel = new JPanel();
        GridBagConstraints constraints = new GridBagConstraints();

        coinsPanel.setBackground(Color.lightGray);
        coinsPanel.setLayout(new GridBagLayout());

        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;
        for (Map.Entry<Integer, Coin> pair : coinsStorage.entrySet()) {
            try {
                BufferedImage bufferedImageCoin = ImageIO.read(CoinPanelManager.class.getResourceAsStream("c" + pair.getKey() + ".png"));
                ImageIcon imageIconCoin = new ImageIcon(bufferedImageCoin);
                imageIconCoin.setImage(imageIconCoin.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                JButton b = new JButton();
                b.setBorder(new EmptyBorder(1, 1, 1, 1));
                b.setContentAreaFilled(false);
                b.setIcon(imageIconCoin);
                b.setActionCommand(String.valueOf(pair.getKey()));
                b.addActionListener(coinActionListener);
                coinsPanel.add(b, constraints);
                constraints.gridx++;
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

        }
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;
        JLabel insert = new JLabel("Insert Coins");
        insert.setFont(new Font("Serif", Font.BOLD, 20));
        coinsPanel.add(insert, constraints);
        return coinsPanel;
    }

    private void buildInsertMoneyPanel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 0.41;
        cancelTransactionButton.addActionListener(e -> callback.onTransactionCancel());

        moneyPanel.add(cancelTransactionButton, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;
        insertMoneyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        moneyPanel.add(insertMoneyLabel, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.5;
        constraints.weighty = 0.41;
        moneyPanel.add(moneyInserted, constraints);
    }

    private final ActionListener coinActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            callback.onCoinInserted(Integer.parseInt(e.getActionCommand()));
        }
    };

    public JLabel getInsertMoneyLabel() {
        return insertMoneyLabel;
    }

    public JLabel getMoneyInserted() {
        return moneyInserted;
    }
}
