package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UsernameDialog extends JDialog {
    private JTextField usernameField;
    private JButton submitButton;
    private JLabel label;
    private boolean usernameValid = false;
    private Font defaultFont = new Font(UI.gameFont, Font.PLAIN, 10);

    public UsernameDialog(JFrame parent) {
        super(parent, "Game Over", true);
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new FlowLayout());
        label = new JLabel("Enter your username:");
        label.setFont(defaultFont);
        add(label);
        usernameField = new JTextField("Player");
        usernameField.setFont(defaultFont);
        usernameField = new JTextField(15);
        add(usernameField);
        submitButton = new JButton("Submit");
        submitButton.setFont(defaultFont);
        submitButton.addActionListener(e -> validateAndClose()); //when button clicked, run validateandclose
        add(submitButton);
        pack();
        setLocationRelativeTo(getParent());
    }
    
    public boolean getUserValid() {
    	return usernameValid;
    }
    
    private void validateAndClose() {//forces user to enter a username
    	String username = usernameField.getText().trim();
        if (!username.isEmpty()) {
            usernameValid = true;
            dispose();//close window
        } else {
            JOptionPane.showMessageDialog(this, "Username cannot be empty!");
        }
    }

    public String getUsername() {
        return usernameValid ? usernameField.getText().trim() : null; //if username valid then return username else return null
    }
}