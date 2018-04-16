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

import cut.cis352.product.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class ProductConfirmDialog extends JDialog implements WindowListener, ActionListener {

    private final JButton confirm_btn;
    private final JButton cancel_btn;
    private final JLabel messageLabel;
    private Product selectedProduct;
    private final String message;

    public ProductConfirmDialog(Frame owner, String title, String message) {
        super(owner, title, true);

        this.message = message;
        this.confirm_btn = new JButton("Confirm");
        this.cancel_btn = new JButton("Cancel");
        this.messageLabel = new JLabel();
        setLocationRelativeTo(owner);
        setSize(300, 100);
        setVisible(false);

        cancel_btn.addActionListener(this);
        confirm_btn.addActionListener(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(messageLabel, cs);
        JPanel bp = new JPanel();
        bp.add(confirm_btn);
        bp.add(cancel_btn);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);
        addWindowListener(this);
        setResizable(false);
    }


    public void setProduct(Product product) {
        this.selectedProduct = product;
        if (product != null) {
            messageLabel.setText(message + selectedProduct.getName());
        }
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        onCancel();
        this.setVisible(false);
        this.dispose();
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

    public abstract void onCancel();

    public abstract void onConfirm();

    @Override
    public void actionPerformed(ActionEvent e) {
        this.setVisible(false);

        if (e.getSource().equals(cancel_btn)) {
            onCancel();
        }

        if (e.getSource().equals(confirm_btn)) {
            onConfirm();
        }
        this.dispose();
    }
}
