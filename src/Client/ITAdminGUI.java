package Client;

import javax.swing.*;
import java.awt.*;


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
	    
	    manageUsersButton.addActionListener(e -> openManageUsers());
	    chatLogsButton.addActionListener(e -> openChatLogs());
	    
	    buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(new JSeparator());
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(manageUsersButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(chatLogsButton);
    
	}
    
	
    private void openManageUsers() {
        JFrame adminFrame = new JFrame("Manage Users");
        adminFrame.setSize(600, 400);
        adminFrame.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // TODO: Add user management UI
        JLabel placeholder = new JLabel("User Management Interface - To be implemented");
        placeholder.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(placeholder, BorderLayout.CENTER);
        
        adminFrame.add(mainPanel);
        adminFrame.setVisible(true);
    }
    
    // Open Chat logs
    private void openChatLogs() {
        JFrame logsFrame = new JFrame("Chat Logs");
        logsFrame.setSize(600, 400);
        logsFrame.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // TODO: Add chat logs UI
        JLabel placeholder = new JLabel("Chat Logs Viewer - To be implemented");
        placeholder.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(placeholder, BorderLayout.CENTER);
        
        logsFrame.add(mainPanel);
        logsFrame.setVisible(true);
    }

}
