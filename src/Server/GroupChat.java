package Server;

import java.io.Serializable;
import java.util.*;

import Common.Message;

public class GroupChat implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static int IDCount = 0;    // static counter
	private final int chatID;    //final unique ID for each chat
	private String chatName;    //name of chat
	private String recipients;
	private List<Message> messages;    //list of messages
	private boolean modified; 
	
	public GroupChat(String chatName, List<String> recipients) {
		this.chatID = ++IDCount;
		this.chatName = chatName;
		this.recipients = (recipients == null) 
				? new ArrayList<>(): new ArrayList<>(recipients));
		this.messages = new ArrayList<>();
		this.modified = false;
	}
	
	public String getChatID() {
		return String.valueOf(chatID);
	}
	
	public String getChatName() {
		return chatName;
	}
	
	public String getRecipients() {
		return recipients;
	}
	
	public String getMessages() {
		 StringBuilder sb = new StringBuilder();
		 for (int i = 0; i < messages.size(); i++) {
			 sb.append(messages.get(i).toString());
			 if (i < messages.size()-1) {
				 sb.append(System.lineSeparator());
			 }
		 }
		 return sb.toString();
	}
	
	public void setchatName(String name) {
		this.chatName = name;
		modified = true;
		
	}
	
	public void addRecipient(String user) {
		if (user == null) {
			throw new IllegalArgumentException("recipient cannot be null");
		}
		if(!recipients.contains(user)) {
			recipients.add(user);
		modified = true;
		}
		
	}
	
	public void addMessage(Message message) {
		if (message == null) {
			throw new IllegalArgumentException("message cannot be null");
		}
		messages.add(message);
		modified = true;
		
	}
	
	public void grantUserAccess() {
		modified = true;
	}
	
	public void saveChat() {
		modified = false;
	}

}