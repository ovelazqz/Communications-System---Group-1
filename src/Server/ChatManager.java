package Server;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
		PrivateChat chat = new PrivateChat(name, new ArrayList<>(recipients));
		privateChats.put(chat.getChatID(), chat);
		modified = true;
		return chat;
	}
	//Create a new group chat with a name and recipients
	public GroupChat createGroupChat(String chatName, List<String> recipients) {
		if(chatName == null || chatName.isEmpty()) {
			throw new IllegalArgumentException("Group Chat cannot be empty");
		}
		GroupChat chat = new GroupChat(chatName, recipients == null ? new ArrayList<>(): new ArrayList<>(recipients));
		groupChats.put(chat.getChatID(), chat);
		modified = true;
		return chat;
	}
	//Search for private chat by ID
	public PrivateChat getPrivateChat(String chatID) {
		return privateChats.get(chatID);
	}
	//Search for private chat by ID
	public GroupChat getGroupChat(String chatID) {
		return groupChats.get(chatID);
	}
	
	public void addMessageToChat(String chatID, Message message) {
		
	}
	
	public void removeChat(String chatID) {
		
	}
	
	public List<Object> getAllChats(){
		
	}
	
	public void save() {
		
	}
	
	
	
	
	

}