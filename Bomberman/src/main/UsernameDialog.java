package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UsernameDialog extends JDialog {
    private JTextField usernameField;
    private JButton submitButton;
    private boolean usernameValid = false;

    // Constructor with JFrame parent
    public UsernameDialog(JFrame parent) {
        super(parent, "Game Over", true);
        setupUI();
    }

    private void setupUI() {
        setLayout(new FlowLayout());
        add(new JLabel("Enter your username:"));
        usernameField = new JTextField(15);
        add(usernameField);
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> validateAndClose());
        add(submitButton);
        pack();
        setLocationRelativeTo(getParent());
    }

    private void validateAndClose() {
        String username = usernameField.getText().trim();
        if (!username.isEmpty()) {
            usernameValid = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username cannot be empty!");
        }
    }

    public String getUsername() {
        return usernameValid ? usernameField.getText().trim() : null;
    }
}