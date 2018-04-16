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

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
        pfPassword.addKeyListener(loginenter);
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
        authenticate();
    };

    private void authenticate(){
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
    }

    private final KeyListener loginenter = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_ENTER:{
                    authenticate();
                    break;
                }
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    public abstract void onAuthenticated();

    public void reset(){
        pfPassword.setText("");
    }
}
