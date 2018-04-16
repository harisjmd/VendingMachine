package vendingmachine;

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
        setLayout(new FlowLayout());
        this.confirm_btn = new JButton("Confirm");
        this.cancel_btn = new JButton("Cancel");
        this.messageLabel = new JLabel();
        setLocationRelativeTo(owner);
        setSize(300, 100);
        setVisible(false);
        cancel_btn.addActionListener(this);
        confirm_btn.addActionListener(this);
        add(messageLabel);
        add(confirm_btn);
        add(cancel_btn);
        addWindowListener(this);
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
