package Common;

import java.util.*;

import Server.UserCollection;

public class User {
	private UserCollection userCollection;
	private static int count = 0;
	private final String uniqueID;
	private String username;
	private String password;
	private USER_ROLE role;
	private List<String >chatAccess;
	private boolean modified;
	
	public User(String username, String password, USER_ROLE role) {
		this.uniqueID = "user#" + ++count;
		if (userCollection.doesItExist(username)) {
			// Error account already exists
		} else {
			this.setUsername(username);
		}
		this.setPassword(password); 
		this.setRole(role); 		// error role check will occur in GUI
		this.setModified(false);
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public USER_ROLE getRole() {
		return role;
	}

	public void setRole(USER_ROLE role) {
		this.role = role;
	}

	public List<String > getChatAccess() {
		return chatAccess;
	}

	public void setChatAccess(List<String > chatAccess) {
		this.chatAccess = chatAccess;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	
	// if modified must save, method will go below
	
	
}
