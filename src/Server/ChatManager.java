package Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import Common.Message;

public class ChatManager implements Serializable {
	private static final long serialVersionUID = 1L;
    
    private final String chatsDir = "src/Server/Chats";
    private final Map<String, PrivateChat> privateChats = new ConcurrentHashMap<>();
    private final Map<String, GroupChat> groupChats = new ConcurrentHashMap<>();
    private boolean modified = false;
    
    public ChatManager() {
        loadChatsFromDisk();
    }
    
   
    public void loadChatsFromDisk() {
        File directory = new File(chatsDir);
        
        if (!directory.exists()) {
            System.out.println("Chats directory does not exist: " + chatsDir);
            directory.mkdirs();
            System.out.println("Created directory: " + chatsDir);
            return;
        }
        
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No chat files found to load");
            return;
        }
        
        for (File file : files) {
            loadChatFromFile(file);
        }
        System.out.println("Loaded " + files.length + " chat files from disk");
    }
    
    
    private void loadChatFromFile(File file) {
        try {
            String filename = file.getName();
            String chatID = filename.replace(".txt", "");
            String type = "";
            
            if (filename.startsWith("PRIVATE_")) {
                type = "PRIVATE";
                chatID = filename.replace("PRIVATE_", "").replace(".txt", "");
            } else if (filename.startsWith("GROUP_")) {
                type = "GROUP";
                chatID = filename.replace("GROUP_", "").replace(".txt", "");
            } else {
                return; 
            }
            
           
            if (type.equals("PRIVATE")) {
                PrivateChat chat = new PrivateChat(new ArrayList<>());
                chat.setChatID(chatID);
                privateChats.put(chatID, chat);
                System.out.println("Loaded private chat: " + chatID);
            } else if (type.equals("GROUP")) {
                GroupChat chat = new GroupChat("", new ArrayList<>());
                chat.setChatID(chatID);
                groupChats.put(chatID, chat);
                System.out.println("Loaded group chat: " + chatID);
            }
            
        } catch (Exception e) {
            System.out.println("Error loading chat file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
   
    public PrivateChat createPrivateChat(List<String> recipients) {
        if (recipients == null || recipients.size() < 2) {
            throw new IllegalArgumentException("Private Chat needs at least 2 recipients");
        }
        
        // Check if private chat already exists with these participants
        PrivateChat existing = findPrivateChatByParticipants(recipients);
        if (existing != null) {
            return existing;
        }
        
        PrivateChat chat = new PrivateChat(recipients);
        privateChats.put(chat.getChatID(), chat);
        setModified(true);
        
        // Save to disk immediately
        saveChatToDisk(chat.getChatID(), chat);
        
        return chat;
    }
    

    public GroupChat createGroupChat(String chatName, List<String> recipients) {
        if (chatName == null || chatName.isEmpty()) {
            throw new IllegalArgumentException("Group Chat cannot be empty");
        }
        
        // Check if group chat already exists with this name
        GroupChat existing = findGroupChatByName(chatName);
        if (existing != null) {
            return existing;
        }
        
        GroupChat chat = new GroupChat(chatName, recipients == null ? new ArrayList<>() : new ArrayList<>(recipients));
        groupChats.put(chat.getChatID(), chat);
        setModified(true);
        
        // Save to disk immediately didnt work :((((((
        saveChatToDisk(chat.getChatID(), chat);
        
        return chat;
    }
    
    
    public void saveChatToDisk(String chatID, Object chatObject) {
        try {
            File directory = new File(chatsDir);
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("Created directory: " + chatsDir);
            }
            
            String filename;
            String messagesText;
            
            if (chatObject instanceof PrivateChat) {
                PrivateChat pc = (PrivateChat) chatObject;
                filename = "PRIVATE_" + chatID + ".txt";
                messagesText = pc.getMessages();
                System.out.println("DEBUG: Saving private chat " + chatID + " with " + pc.getMessageCount() + " messages");
            } else if (chatObject instanceof GroupChat) {
                GroupChat gc = (GroupChat) chatObject;
                filename = "GROUP_" + chatID + ".txt";
                messagesText = gc.getMessages();
                System.out.println("DEBUG: Saving group chat " + chatID + " with " + gc.getMessageCount() + " messages");
            } else {
                return;
            }
            
            String filePath = chatsDir + File.separator + filename;
            
            if (messagesText == null || messagesText.isEmpty()) {
                System.out.println("WARNING: No messages to save for chat " + chatID);
                
                FileWriter writer = new FileWriter(filePath, false);
                writer.close();
            } else {
                FileWriter writer = new FileWriter(filePath, false);
                writer.write(messagesText);
                writer.close();
                System.out.println("DEBUG: Wrote " + messagesText.length() + " bytes to " + filePath);
            }
            
            System.out.println("Chat saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving chat to disk: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void saveAllChatsToDisk() {
        for (PrivateChat pc : privateChats.values()) {
            saveChatToDisk(pc.getChatID(), pc);
        }
        
        for (GroupChat gc : groupChats.values()) {
            saveChatToDisk(gc.getChatID(), gc);
        }
        
        System.out.println("All chats saved to disk");
    }
    
    
    public PrivateChat getPrivateChat(String chatID) {
        return privateChats.get(chatID);
    }
    
    
    public GroupChat getGroupChat(String chatID) {
        return groupChats.get(chatID);
    }
    
    
    public PrivateChat findPrivateChatByParticipants(List<String> participants) {
        if (participants == null || participants.size() < 2) return null;
        Set<String> target = new HashSet<>(participants);
        
        for (PrivateChat pc : privateChats.values()) {
            if (new HashSet<>(pc.getRecipientList()).equals(target)) {
                return pc;
            }
        }
        return null;
    }
    
    
    public GroupChat findGroupChatByName(String chatName) {
        if (chatName == null || chatName.isEmpty()) return null;
        
        for (GroupChat gc : groupChats.values()) {
            if (gc.getChatName().equals(chatName)) {
                return gc;
            }
        }
        return null;
    }
    
    
    public void addMessageToChat(String chatID, Message message) {
        if (chatID == null || chatID.isEmpty()) {
            throw new IllegalArgumentException("chatID cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }
        
        PrivateChat p = privateChats.get(chatID);
        if (p != null) {
            System.out.println("DEBUG: Adding message to private chat " + chatID);
            p.addMessage(message);
            setModified(true);
            saveChatToDisk(chatID, p);
            return;
        }
        
        GroupChat g = groupChats.get(chatID);
        if (g != null) {
            System.out.println("DEBUG: Adding message to group chat " + chatID);
            g.addMessage(message);
            setModified(true);
            saveChatToDisk(chatID, g);
            return;
        }
        
        throw new NoSuchElementException("Chat not found: " + chatID);
    }
    
    
    public boolean chatExists(String chatID) {
        return privateChats.containsKey(chatID) || groupChats.containsKey(chatID);
    }
    
    
    public void removeChat(String chatID) {
        boolean removed = false;
        if (privateChats.remove(chatID) != null) removed = true;
        if (groupChats.remove(chatID) != null) removed = true;
        if (removed) setModified(true);
    }
    
    
    public List<Object> getAllChats() {
        List<Object> all = new ArrayList<>(privateChats.values());
        all.addAll(groupChats.values());
        return Collections.unmodifiableList(all);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String nl = System.lineSeparator();
        
        for (PrivateChat pc : privateChats.values()) {
            sb.append("[PRIVATE] ")
                    .append(pc.getChatID()).append(" | ")
                    .append(pc.getChatName()).append(" | ")
                    .append(pc.getRecipientList()).append(nl);
            sb.append(pc.getMessages()).append(nl).append(nl);
        }
        
        for (GroupChat gc : groupChats.values()) {
            sb.append("[GROUP] ")
                    .append(gc.getChatID()).append(" | ")
                    .append(gc.getChatName()).append(" | ")
                    .append(gc.getRecipientList()).append(nl);
            sb.append(gc.getMessages()).append(nl).append(nl);
        }
        
        return sb.toString();
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}