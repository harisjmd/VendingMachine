package vendingmachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class AdminGUI extends JFrame implements WindowListener {

    private Controller controller;
    private final UserGUI userGUI;
    private JPanel productsStoragePanel;
    private JPanel coinsPanel;

    public AdminGUI(String title, Controller controller, UserGUI userGUI) throws HeadlessException {
        super(title);
        this.controller = controller;
        this.userGUI = userGUI;
        setSize(800, 800);
        setLocation(500, 200);
        setLayout(new GridBagLayout());
        setVisible(false);
        addWindowListener(this);
    }


    public void build() {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;
        buildProductsStoragePanel();
        add(productsStoragePanel, constraints);
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.anchor = GridBagConstraints.FIRST_LINE_END;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.01;
        constraints.weighty = 0.01;

        add(new JPanel(), constraints);
        userGUI.setVisible(false);
        setVisible(true);

    }

    private void buildProductsStoragePanel() {
        productsStoragePanel = new JPanel();
        productsStoragePanel.setSize(400, 400);
        productsStoragePanel.setPreferredSize(new Dimension(400, 400));
        productsStoragePanel.setBackground(Color.RED);
        GridBagLayout layout = new GridBagLayout();
        productsStoragePanel.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        controller.getStorage().forEach(productStorage -> {

            productsStoragePanel.add(new JLabel(String.valueOf(productStorage.getId()) + ": " + productStorage.getProduct().getName() + " - " + productStorage.getQuantity()), constraints);
            constraints.gridy++;
//            productsStoragePanel.add(temp);
        });


    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        userGUI.setVisible(true);
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

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
