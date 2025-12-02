package Client;

import javax.swing.*;
import Common.Message;
import java.awt.*;
import java.io.*;
import java.util.Map;

public class LoginGUI implements UserInterface {
    private Client client;
    private JFrame frame;
    private JLabel connectionStatus;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton loginButton;
    
    public LoginGUI(Client client) {
        this.client = client;
        createUI();
    }
    
    private void createUI() {
        frame = new JFrame();
        frame.setTitle("/chat.");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        // Set color based on connection
        if (client.isConnected()) {
            frame.setBackground(Color.GREEN);
        } else {
            frame.setBackground(Color.RED);
        }
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
//        // Top: Red image placeholder
//        JPanel imagePanel = new JPanel();
//        imagePanel.setBackground(Color.RED);
//        imagePanel.setPreferredSize(new Dimension(400, 100));
//        JLabel imageLabel = new JLabel("Image Placeholder");
//        imageLabel.setForeground(Color.WHITE);
//        imageLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        imagePanel.add(imageLabel);
//        mainPanel.add(imagePanel, BorderLayout.NORTH);
        
        
        // Center: Username and Password fields
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Connection Status
        connectionStatus = new JLabel("Connected to Server");
        connectionStatus.setFont(new Font("Arial", Font.BOLD, 14));
        connectionStatus.setForeground(client.isConnected() ? Color.GREEN : Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 50, 20, 50);
        centerPanel.add(connectionStatus, gbc);
        
        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 50, 0, 50);
        centerPanel.add(usernameLabel, gbc);
        
        // Username field
        usernameField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 50, 10, 50);
        centerPanel.add(usernameField, gbc);
        
        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 50, 0, 50);
        centerPanel.add(passwordLabel, gbc);
        
        // Password field
        passwordField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 50, 10, 50);
        centerPanel.add(passwordField, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom: Login button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        loginButton = new JButton("Log In");
        loginButton.setEnabled(client.isConnected());
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.addActionListener(e -> handleLogin());
        bottomPanel.add(loginButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter username and password", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Send login request to server
            Message response = client.sendLoginRequest(username, password);
            
            if (response.getStatus().equals("success")) {
//               
            	// Extract user role from response
                Map<String, Object> payload = response.getPayload();
                String userRole = (String) payload.get("userRole");
                
                JOptionPane.showMessageDialog(frame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                
                // Open appropriate GUI based on role
                openGUIByRole(username, userRole);
                
            } else {
                JOptionPane.showMessageDialog(frame, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openGUIByRole(String username, String userRole) {
    	if (userRole == null || userRole.isEmpty()) {
            userRole = "EMPLOYEE";  // Default role
        }
        
        if (userRole.equalsIgnoreCase("IT_ADMINISTRATOR")) {
            // Open Admin GUI
            new ITAdminGUI(client, username);
        } else {
            // Open Employee GUI (default for EMPLOYEE role)
            new EmployeeGUI(client, username);
        }
		
	}

	@Override
    public void processCommands() {
        // Handled by button listeners
    }

	@Override
	public void handleIncomingMessage(Message message) {
		// TODO Auto-generated method stub
		
	}
}
