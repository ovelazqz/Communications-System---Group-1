

package Client;

import javax.swing.*;
import Common.Message;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

public class EmployeeGUI implements UserInterface {
    private Client client;
    protected String username;
    protected JFrame frame;
    private JPanel messagesPanel;
    private JPanel chatListPanel;
    protected JPanel buttonsPanel;
    private String selectedChat = null;
    private boolean listening = true;
    
    public EmployeeGUI(Client client, String username) {
        this.client = client;
        this.username = username;
        createUI();
        startMessageListener();
    }
    
    public void createUI() {
        frame = new JFrame();
        frame.setTitle("/chat. - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.95);
        int height = (int) (screenSize.height * 0.95);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Left Panel
        JPanel leftPanel = new JPanel();
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
        
        // Buttons Panel
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(new Color(220, 220, 220));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton newChatButton = new JButton("New Chat");
        JButton deleteChatButton = new JButton("Delete Chat");
        JButton filterButton = new JButton("Filter Chats");
        JButton logOutButton = new JButton("Log Out");
        
        newChatButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        deleteChatButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        filterButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logOutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        newChatButton.addActionListener(e -> createNewChat());
        deleteChatButton.addActionListener(e -> deleteChat());
        filterButton.addActionListener(e -> filterChats());
        logOutButton.addActionListener(e -> logOut());
        
        buttonsPanel.add(newChatButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(deleteChatButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(filterButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(logOutButton);
        
        leftPanel.add(buttonsPanel, BorderLayout.SOUTH);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        
        // Messages Panel
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Color.WHITE);
        JScrollPane messagesScroll = new JScrollPane(messagesPanel);
        messagesScroll.setBorder(BorderFactory.createTitledBorder("Messages"));
        rightPanel.add(messagesScroll, BorderLayout.CENTER);
        
        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(new Color(200, 200, 200));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField messageInput = new JTextField();
        messageInput.setPreferredSize(new Dimension(0, 40));
        inputPanel.add(messageInput, BorderLayout.CENTER);
        
        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 40));
        sendButton.addActionListener(e -> sendMessage(messageInput));
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        rightPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    private void startMessageListener() {
        new Thread(() -> {
            try {
                while (listening) {
                    Message message = (Message) client.getInput().readObject();
                    handleIncomingMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error reading message: " + e.getMessage());
            }
        }).start();
    }
    
    private void handleIncomingMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            if (message.getType().equals("receive_message")) {
                addMessage(message.getSender() + ": " + message.getText(), true);
            }
        });
    }
    
    private void addChatButton(String chatName) {
        JButton chatButton = new JButton(chatName);
        chatButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        chatButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        chatButton.addActionListener(e -> selectChat(chatName));
        chatListPanel.add(chatButton);
        chatListPanel.add(Box.createVerticalStrut(5));
        chatListPanel.revalidate();
        chatListPanel.repaint();
    }
    
    private void selectChat(String chatName) {
        selectedChat = chatName;
        messagesPanel.removeAll();
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }
    
    private void createNewChat() {
        String[] options = {"Private Chat", "Group Chat"};
        int choice = JOptionPane.showOptionDialog(frame, "What type of chat?", "Create Chat",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (choice == 0) {
            createPrivateChat();
        } else if (choice == 1) {
            createGroupChat();
        }
    }
    
    private void createPrivateChat() {
        List<String> users = new ArrayList<>();
        
        // dummy users
        users.add("User1");
        users.add("User2");
        users.add("User3");
        
        String recipient = (String) JOptionPane.showInputDialog(frame, "Select user:", "Private Chat",
                JOptionPane.QUESTION_MESSAGE, null, users.toArray(), users.get(0));
        
        if (recipient != null) {
            try {
                client.sendCreatePrivateChatRequest(recipient);
                addChatButton(recipient + " (Private)");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
            }
        }
    }
    
    private void createGroupChat() {
        String groupName = JOptionPane.showInputDialog(frame, "Enter group name:");
        if (groupName != null && !groupName.trim().isEmpty()) {
            try {
                client.sendCreateGroupChatRequest(groupName);
                addChatButton(groupName + " (Group)");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteChat() {
        if (selectedChat == null) {
            JOptionPane.showMessageDialog(frame, "Select a chat first");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(frame, "Delete " + selectedChat + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            for (int i = 0; i < chatListPanel.getComponentCount(); i++) {
                if (chatListPanel.getComponent(i) instanceof JButton) {
                    JButton btn = (JButton) chatListPanel.getComponent(i);
                    if (btn.getText().equals(selectedChat)) {
                        chatListPanel.remove(i);
                        break;
                    }
                }
            }
            selectedChat = null;
            chatListPanel.revalidate();
            chatListPanel.repaint();
        }
    }
    
    private void filterChats() {
        JOptionPane.showMessageDialog(frame, "Filter functionality coming soon");
    }
    
    private void logOut() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Log out?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                listening = false;
                client.sendLogoutRequest();
                frame.dispose();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
            }
        }
    }
    
    private void sendMessage(JTextField messageInput) {
        String message = messageInput.getText().trim();
        
        if (selectedChat == null) {
            JOptionPane.showMessageDialog(frame, "Select a chat first");
            return;
        }
        
        if (message.isEmpty()) {
            return;
        }
        
        try {
            client.sendChatMessage(selectedChat, message);
            addMessage("You: " + message, false);
            messageInput.setText("");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }
    
    private void addMessage(String message, boolean isOther) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JTextArea messageText = new JTextArea(message);
        messageText.setWrapStyleWord(true);
        messageText.setLineWrap(true);
        messageText.setEditable(false);
        messageText.setOpaque(true);
        
        if (isOther) {
            messageText.setBackground(new Color(220, 220, 220));
        } else {
            messageText.setBackground(new Color(135, 206, 250));
        }
        
        messagePanel.add(messageText, BorderLayout.CENTER);
        messagesPanel.add(messagePanel);
        messagesPanel.add(Box.createVerticalStrut(5));
        
        messagesPanel.revalidate();
        messagesPanel.repaint();
        
        JScrollPane parent = (JScrollPane) messagesPanel.getParent().getParent();
        if (parent != null) {
            parent.getVerticalScrollBar().setValue(parent.getVerticalScrollBar().getMaximum());
        }
    }
    
    @Override
    public void processCommands() {
    }
}