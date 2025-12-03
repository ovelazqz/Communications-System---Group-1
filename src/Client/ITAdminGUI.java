package Client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Client.*;
import Common.Message;


public class ITAdminGUI extends EmployeeGUI {

	public ITAdminGUI(Client client, String username) {
		super(client, username);
		
		
	}
	
	
	public void createUI() {
		super.createUI();
		
		frame.setTitle("/chat. - " + username + " [ADMIN]");
		JButton manageUsersButton = new JButton("Manage Users");
	    JButton chatLogsButton = new JButton("View Chat Logs");
	    
	    
	    manageUsersButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
	    chatLogsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
	    
	    manageUsersButton.addActionListener(e -> manageUser(null, null, null, null, client));
	    chatLogsButton.addActionListener(e -> openChatLogs());
	    
	    
	    buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(new JSeparator());
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(manageUsersButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(chatLogsButton);
    
	}
    
	
	public void manageUser(JTextField usernameField, JTextField passwordField, JComboBox<String> roleCombo, JTextArea userListArea, Client client) {
			
			String username = usernameField.getText().trim();
			String password = passwordField.getText().trim();
			String role = (String) roleCombo.getSelectedItem();
			
			// Validation
			if (username.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
			return;
			}
			
			if (password.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
			return;
			}
			
			if (password.length() < 5) {
			JOptionPane.showMessageDialog(null, "Password must be at least 5 characters", "Error", JOptionPane.ERROR_MESSAGE);
			return;
			}
			
			try {
			
			Map<String, Object> payload = new HashMap<>();
			payload.put("username", username);
			payload.put("password", password);
			payload.put("role", role);
			
			Message msg = new Message("manage_user", "manage_user", payload);
			client.getOutput().writeObject(msg);
			client.getOutput().flush();
			
			JOptionPane.showMessageDialog(null, "User '" + username + "' added/updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
			
			// Clear fields
			usernameField.setText("");
			passwordField.setText("");
			roleCombo.setSelectedIndex(0);
			
			// Refresh user list
			refreshUserList(userListArea, client);
			
			} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error managing user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
}

		private void refreshUserList(JTextArea userListArea, Client client) {
			try {
				// Send request to get updated user list
				Map<String, Object> payload = new HashMap<>();
				Message msg = new Message("get_all_users", "get_all_users", payload);
				
				client.getOutput().writeObject(msg);
				client.getOutput().flush();
			
				// Display loading message
				userListArea.setText("Loading user list...");
				
			} catch (IOException e) {
			userListArea.setText("Error loading user list: " + e.getMessage());
			}
		}
	    // Open Chat logs not working non functional
	    private void openChatLogs() {
	        JFrame logsFrame = new JFrame("Chat Logs");
	        logsFrame.setSize(600, 400);
	        logsFrame.setLocationRelativeTo(frame);
	        
	        JPanel mainPanel = new JPanel();
	        mainPanel.setLayout(new BorderLayout());
	        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        
	        // TODO: Add chat logs UI
	        JLabel placeholder = new JLabel("Empty");
	        placeholder.setFont(new Font("Arial", Font.BOLD, 14));
	        mainPanel.add(placeholder, BorderLayout.CENTER);
	        
	        logsFrame.add(mainPanel);
	        logsFrame.setVisible(true);
	    }

}
