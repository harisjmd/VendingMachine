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

import cut.cis352.coin.OnActionCallback;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CoinPanelManager extends JPanel {

    private final JLabel insertMoneyLabel;
    private final JPanel moneyPanel;
    private final JLabel moneyInserted;
    private final JButton cancelTransactionButton;
    private final OnActionCallback callback;

    public CoinPanelManager(OnActionCallback callback) {
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
        moneyInserted.setSize(200,100);
        moneyInserted.setBorder(BorderFactory.createLineBorder(Color.RED,2));
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

        BufferedImage bufferedImage10c = null;
        BufferedImage bufferedImage20c = null;
        BufferedImage bufferedImage50c = null;
        BufferedImage bufferedImage1e = null;
        BufferedImage bufferedImage2e = null;
        try {
            bufferedImage10c = ImageIO.read(new File("10c.png"));
            bufferedImage20c = ImageIO.read(new File("20c.png"));
            bufferedImage50c = ImageIO.read(new File("50c.png"));
            bufferedImage1e = ImageIO.read(new File("1e.png"));
            bufferedImage2e = ImageIO.read(new File("2e.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert bufferedImage10c != null;
        assert bufferedImage20c != null;
        assert bufferedImage50c != null;
        assert bufferedImage1e != null;
        assert bufferedImage2e != null;

        ImageIcon imageIcon10c = new ImageIcon(bufferedImage10c);
        imageIcon10c.setImage(imageIcon10c.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        JButton b10c = new JButton();
        b10c.setBorder(new EmptyBorder(1, 1, 1, 1));
        b10c.setContentAreaFilled(false);
        b10c.setIcon(imageIcon10c);
        b10c.setActionCommand("0.10");
        b10c.addActionListener(coinActionListener);

        ImageIcon imageIcon20c = new ImageIcon(bufferedImage20c);
        imageIcon20c.setImage(imageIcon20c.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        JButton b20c = new JButton();
        b20c.setBorder(new EmptyBorder(1, 1, 1, 1));
        b20c.setContentAreaFilled(false);
        b20c.setIcon(imageIcon20c);
        b20c.setActionCommand("0.20");
        b20c.addActionListener(coinActionListener);

        ImageIcon imageIcon50c = new ImageIcon(bufferedImage50c);
        imageIcon50c.setImage(imageIcon50c.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        JButton b50c = new JButton();
        b50c.setBorder(new EmptyBorder(1, 1, 1, 1));
        b50c.setContentAreaFilled(false);
        b50c.setIcon(imageIcon50c);
        b50c.setActionCommand("0.50");
        b50c.addActionListener(coinActionListener);

        ImageIcon imageIcon1e = new ImageIcon(bufferedImage1e);
        imageIcon1e.setImage(imageIcon1e.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        JButton b1e = new JButton();
        b1e.setBorder(new EmptyBorder(1, 1, 1, 1));
        b1e.setContentAreaFilled(false);
        b1e.setIcon(imageIcon1e);
        b1e.setActionCommand("1.00");
        b1e.addActionListener(coinActionListener);

        ImageIcon imageIcon2e = new ImageIcon(bufferedImage2e);
        imageIcon2e.setImage(imageIcon2e.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        JButton b2e = new JButton();
        b2e.setBorder(new EmptyBorder(1, 1, 1, 1));
        b2e.setContentAreaFilled(false);
        b2e.setIcon(imageIcon2e);
        b2e.setActionCommand("2.00");
        b2e.addActionListener(coinActionListener);
        coinsPanel.add(b10c, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        coinsPanel.add(b20c, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        coinsPanel.add(b50c, constraints);
        constraints.gridx = 3;
        constraints.gridy = 0;
        coinsPanel.add(b1e, constraints);
        constraints.gridx = 4;
        constraints.gridy = 0;
        coinsPanel.add(b2e, constraints);
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
        cancelTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callback.onTransactionCancel();
            }
        });

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
            callback.onCoinInserted(Double.parseDouble(e.getActionCommand()));
        }
    };

    public JLabel getInsertMoneyLabel() {
        return insertMoneyLabel;
    }

    public JLabel getMoneyInserted() {
        return moneyInserted;
    }
}
