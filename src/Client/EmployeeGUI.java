package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// note to self: When creating group chat, user selects other user from drop down and adds (appends) to list of recipients. 
//               Once they click create, it sends the request over to server. 

public class EmployeeGUI {
	private JFrame frame;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel chatListPanel;
    private JPanel buttonsPanel;
    private JPanel messagesPanel;
    private JPanel inputPanel;
    
    public EmployeeGUI() {
        createUI();
    }

	public void createUI() {
		frame = new JFrame();
		frame.setTitle("/chat.");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(1100,800);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	int width = (int) (screenSize.width * 0.95);  // 95% of screen width
    	int height = (int) (screenSize.height * 0.95); // 95% of screen height
    	frame.setSize(width, height);
    	
    	frame.setLocationRelativeTo(null);
    	
    	// Main panel with layout set to BorderLayout
    	JPanel mainPanel = new JPanel();
    	mainPanel.setLayout(new BorderLayout());
    	
    	// Left Panel
    	leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension((int)(width * 0.25), height));
        leftPanel.setBackground(new Color(240, 240, 240));
        
        // Chat List Panel
        chatListPanel = new JPanel();
        chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
        chatListPanel.setBackground(new Color(240, 240, 240));
        JScrollPane chatListScroll = new JScrollPane(chatListPanel);
        chatListScroll.setBorder(BorderFactory.createTitledBorder("Chats"));
        leftPanel.add(chatListScroll, BorderLayout.CENTER);
        
        
        // Buttons Panel (will be used to include Administrator features later)
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(new Color(220,220,220));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton newChatButton = new JButton("New Chat");
        JButton deleteChatButton = new JButton("Delete Chat");
        JButton logOutButton = new JButton("Log Out");
        JButton filterButton = new JButton("Filter Chats");
        
        // Admin buttons added here:
        //
        //
        //
        //__________________________
        
        
        // action listeners
//        newChatButton.addActionListener(e -> createNewChat());
//        deleteChatButton.addActionListener(e -> deleteChat());
//        logOutButton.addActionListener(e -> logOut());
//        filterButton.addActionListener(filter());
        
        buttonsPanel.add(newChatButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(deleteChatButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(filterButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(logOutButton);
        
        
        leftPanel.add(buttonsPanel, BorderLayout.SOUTH);
		
	}
}
