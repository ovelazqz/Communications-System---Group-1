package Client;
import javax.swing.*;
import Common.Message;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;
public class EmployeeGUI implements UserInterface {
   protected Client client;
   protected String username;
   protected JFrame frame;
   private JPanel messagesPanel;
   private JPanel chatListPanel;
   protected JPanel buttonsPanel;
   private String selectedChat = null;
   private boolean listening = true;
    private String selectedChatID = null;
   private String[] availableUsers = new String[]{};
  
   private Map<String, StringBuilder> chatMessages = new HashMap<>();
  
   public EmployeeGUI(Client client, String username) {
       this.client = client;
       this.username = username;
       createUI();
       startMessageListener();
       fetchUserList();
   }
  
   public void createUI() {
       frame = new JFrame();
       frame.setTitle("/chat. - " + username + " [EMPLOYEE]");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
       int width = (int) (screenSize.width * 0.95);
       int height = (int) (screenSize.height * 0.95);
       frame.setSize(width, height);
       frame.setLocationRelativeTo(null);
       frame.setResizable(false);
      
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
       JButton logOutButton = new JButton("Log Out");
      
       newChatButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
       deleteChatButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
       logOutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      
       newChatButton.addActionListener(e -> createNewChat());
       deleteChatButton.addActionListener(e -> deleteChat());
       logOutButton.addActionListener(e -> logOut());
      
       buttonsPanel.add(newChatButton);
       buttonsPanel.add(Box.createVerticalStrut(5));
       buttonsPanel.add(deleteChatButton);
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
  
   public void handleIncomingMessage(Message message) {
   	SwingUtilities.invokeLater(() -> {
           if (message.getType().equals("receive_message")) {
               String sender = message.getSender();
               String text = message.getText();
               String chatID = (String) message.getData("chatID");
              
               if (chatID != null) {
                   addMessageToChat(chatID, sender + ": " + text, true);
               }
           } else if (message.getType().equals("response")) {
               String status = message.getStatus();
               String sessionID = message.getSessionID();
              
               if (sessionID != null && sessionID.contains("create_private") && status.equals("success")) {
                   String chatID = (String) message.getData("chatID");
                   String chatName = (String) message.getData("chatName");
                   Client.storeChatID(chatName + " (Private)", chatID);
                   System.out.println("Private chat created with ID: " + chatID);
                  
               } else if (sessionID != null && sessionID.contains("create_group") && status.equals("success")) {
                   String chatID = (String) message.getData("chatID");
                   String chatName = (String) message.getData("chatName");
                   Client.storeChatID(chatName + " (Group)", chatID);
                   System.out.println("Group chat created with ID: " + chatID);
                  
               } else if (sessionID != null && sessionID.equals("get_user_list") && status.equals("success")) {
                   availableUsers = (String[]) message.getData("users");
                   System.out.println("Fetched " + availableUsers.length + " users from server");
               }
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
      
       chatMessages.put(chatName, new StringBuilder());
   }
  
   private void selectChat(String chatName) {
   	selectedChat = chatName;
       selectedChatID = Client.chatNameToID.getOrDefault(chatName, chatName);
       messagesPanel.removeAll();
      
       // Try to load messages from file first
       loadChatHistory(selectedChatID);
      
       // Then load messages from memory
       if (chatMessages.containsKey(chatName)) {
           String[] messages = chatMessages.get(chatName).toString().split("\n");
           for (String msg : messages) {
               if (!msg.isEmpty()) {
                   boolean isOther = !msg.startsWith("You:");
                   addMessageToPanel(msg, isOther);
               }
           }
       }
      
       messagesPanel.revalidate();
       messagesPanel.repaint();
   }
  
  
   private void loadChatHistory(String chatID) {
       try {
           String filename = "chats/PRIVATE_" + chatID + ".txt";
           if (!Files.exists(Paths.get(filename))) {
               filename = "chats/GROUP_" + chatID + ".txt";
           }
          
           if (Files.exists(Paths.get(filename))) {
               String content = new String(Files.readAllBytes(Paths.get(filename)));
               String[] lines = content.split("\n");
               for (String line : lines) {
                   if (!line.trim().isEmpty()) {
                       boolean isOther = !line.startsWith("You:");
                       addMessageToPanel(line.trim(), isOther);
                   }
               }
               System.out.println("Loaded chat history from: " + filename);
           }
       } catch (IOException e) {
           System.out.println("Could not load chat history: " + e.getMessage());
       }
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
   	// Use fetched users instead of dummy data
       String recipient = (String) JOptionPane.showInputDialog(frame, "Select user:", "Private Chat",
               JOptionPane.QUESTION_MESSAGE, null, availableUsers,
               availableUsers.length > 0 ? availableUsers[0] : null);
      
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
            chatMessages.remove(selectedChat);
           
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
            selectedChatID = null;
            messagesPanel.removeAll();
            chatListPanel.revalidate();
            chatListPanel.repaint();
            messagesPanel.revalidate();
            messagesPanel.repaint();
        }
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
           client.sendChatMessage(selectedChatID, message);
           addMessageToChat(selectedChatID, "You: " + message, false);
           messageInput.setText("");
       } catch (IOException e) {
           JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
       }
   }
  
   private void addMessageToChat(String chatID, String message, boolean isOther) {
       String chatName = selectedChat;
      
       // Store message
       if (chatMessages.containsKey(chatName)) {
           chatMessages.get(chatName).append(message).append("\n");
       } else {
           chatMessages.put(chatName, new StringBuilder(message).append("\n"));
       }
      
       // Display if this chat is selected
       if (chatID.equals(selectedChatID)) {
           addMessageToPanel(message, isOther);
       }
   }
  
   private void addMessageToPanel(String message, boolean isOther) {
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
  
   private void fetchUserList() {
   	 try {
   	        client.requestUserList(null);
   	    } catch (IOException e) {
   	        System.out.println("Error requesting user list: " + e.getMessage());
   	    }
   }
  
   @Override
   public void processCommands() {
   }
}

