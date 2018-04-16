package vendingmachine;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class LoginDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;

    public LoginDialog(Frame owner) {
        super(owner, "Admin Login");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;


        lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(login);
        btnCancel = new JButton("Cancel");
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
        setVisible(false);
    }

    private final ActionListener login = e -> {
        String pass = new String(pfPassword.getPassword());
        if (pass.equals("password")) {
            onAuthenticated();
        } else {
            JOptionPane.showMessageDialog(LoginDialog.this,
                    "Invalid Admin password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            // reset username and password
            pfPassword.setText("");
        }
    };

    public abstract void onAuthenticated();

    public void reset(){
        pfPassword.setText("");
    }
}
