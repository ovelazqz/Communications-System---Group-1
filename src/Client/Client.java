package Client;

import Common.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
//    private GUI_STATE state;
//    private static boolean signedIn = false;
    private static boolean connected = false;
    private static final int CHARACTER_LIMIT = 200;
    private static Client instance;
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = Client.getInstance();
        UserInterface user = new LoginGUI(client);
        user.processCommands();
    }
    
    private Client() throws IOException, ClassNotFoundException {
        attemptConnection();
    }
    
    private void attemptConnection() throws IOException, ClassNotFoundException {
        try {
        	// socket = new Socket("[Your IP Address Here]", 5050);
        	// socket = new Socket("127.0.0.1", 5050); // This is only to self
            socket = new Socket("134.154.40.175", 5050);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            
            // Send initial connection message
            Message checkConnection = new Message("confirm connection");
            output.writeObject(checkConnection);
            output.flush();
            
            // Wait for server response
            Message response = (Message) input.readObject();
            
            if (response.getStatus().equals("success")) {
                connected = true;
                System.out.println("Connected to Server...");
            } else {
                connected = false;
                throw new IOException("Server rejected connection");
            }
        } catch (IOException e) {
            connected = false;
            System.out.println("Connection failed: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
            connected = false;
            System.out.println("Error reading response: " + e.getMessage());
            throw e;
        }
    }
    
    public static Client getInstance() throws IOException, ClassNotFoundException {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }
    
    public void setConnected(boolean connected) {
        Client.connected = connected;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public boolean checkMessageLength(String msg) {
        return msg.length() <= CHARACTER_LIMIT;
    }
    
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
    
    public ObjectOutputStream getOutput() {
        return output;
    }
    
    public ObjectInputStream getInput() {
        return input;
    }
    
    // Send login request to server
    public Message sendLoginRequest(String username, String password) throws IOException, ClassNotFoundException {
        Message loginMsg = new Message("login");
        loginMsg.setSender(username);
        loginMsg.setText(password);
        
        output.writeObject(loginMsg);
        output.flush();
        
        // Wait for response
        Message response = (Message) input.readObject();
        return response;
    }
    
    // Send logout request to server
    public void sendLogoutRequest() throws IOException {
        Message logoutMsg = new Message("logout");
        output.writeObject(logoutMsg);
        output.flush();
    }
    
    // Send create private chat request
    public void sendCreatePrivateChatRequest(String recipient) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("recipient", recipient);
        Message msg = new Message("create_private_chat", "create_private", payload);
        
        output.writeObject(msg);
        output.flush();
    }
    
    // Send create group chat request
    public void sendCreateGroupChatRequest(String groupName) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("groupName", groupName);
        Message msg = new Message("create_group_chat", "create_group", payload);
        
        output.writeObject(msg);
        output.flush();
    }
    
    // Send a message
    public void sendChatMessage(String chatID, String messageText) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("chatID", chatID);
        Message msg = new Message("send_message", "send_msg", payload);
        msg.setText(messageText);
        
        output.writeObject(msg);
        output.flush();
    }
}
