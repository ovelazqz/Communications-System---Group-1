package Server;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Common.Message;

public class ChatManager implements Serializable{
	//Creates new instances of private or groupchat
	private static final long serialVersionUID = 1L;
	
	private final Map<String, PrivateChat> privateChats = new ConcurrentHashMap<>();
	private final Map<String, GroupChat> groupChats = new ConcurrentHashMap<>();
	
	private boolean modified = false;
	
	public ChatManager() {}
	//Create a new private chat for recipients
	public PrivateChat createPrivateChat(List<String> recipients) {
		if(recipients == null || recipients.size() < 2) {
			throw new IllegalArgumentException("Private Chat needs at least 2 recipients");
		}
		
		String name = String.join(", ", recipients);
		String rec = String.join(", ", recipients);
		
		PrivateChat chat = new PrivateChat(name, rec);
		privateChats.put(chat.getChatID(), chat);
		modified = true;
		return chat;
	}
	//Create a new group chat with a name and recipients
	public GroupChat createGroupChat(String chatName, List<String> recipients) {
	    if (chatName == null || chatName.isEmpty()) {
	        throw new IllegalArgumentException("Group Chat cannot be empty");
	    }

	    // ✅ prevent duplicate group names on the server
	    if (groupChats.containsKey(chatName)) {
	        throw new IllegalArgumentException("Group name already exists");
	    }

	    GroupChat chat = new GroupChat(chatName, 
	            recipients == null ? new ArrayList<>() : new ArrayList<>(recipients));
	    groupChats.put(chatName, chat);
	    modified = true;
	    return chat;
	}

	//Search for private chat by ID
	public PrivateChat getPrivateChat(String chatID) {
		return privateChats.get(chatID);
	}
	//Search for group chat by ID
	public GroupChat getGroupChat(String chatID) {
		return groupChats.get(chatID);
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
            p.addMessage(message);
            modified = true;
            return;
        }

        GroupChat g = groupChats.get(chatID);
        if (g != null) {
            g.addMessage(message);
            modified = true;
            return;
        }

        throw new NoSuchElementException("Chat not found: " + chatID);
		
	}
	
	public void removeChat(String chatID) {
		
		boolean removed = false;
        if (privateChats.remove(chatID) != null) removed = true;
        if (groupChats.remove(chatID) != null)   removed = true;
        if (removed) modified = true;
		
	}
	
	public List<Object> getAllChats(){
		
		List<Object> all = new ArrayList<>(privateChats.values());
        all.addAll(groupChats.values());
        return Collections.unmodifiableList(all);
		
	}
	
	public void save() {
		
		privateChats.values().forEach(PrivateChat::saveChat);
        groupChats.values().forEach(GroupChat::saveChat);
        modified = false;
		
	}
	
	
	public String toString() {

		StringBuilder sb = new StringBuilder();
	    String nl = System.lineSeparator();

	    for (PrivateChat pc : privateChats.values()) {
	        sb.append("[PRIVATE] ")
	          .append(pc.getChatID()).append(" | ")
	          .append(pc.getChatName()).append(" | ")
	          .append(pc.getRecipients()).append(nl);
	        sb.append(pc.getMessages()).append(nl).append(nl);
	    }

	    for (GroupChat gc : groupChats.values()) {
	        sb.append("[GROUP] ")
	          .append(gc.getChatID()).append(" | ")
	          .append(gc.getChatName()).append(" | ")
	          .append(gc.getRecipients()).append(nl);
	        sb.append(gc.getMessages()).append(nl).append(nl);
	    }

	    return sb.toString();
		
	}
	

	
	public List<GroupChat> getAllGroupChats() {
	    return new ArrayList<>(groupChats.values());
	}
	

}