package Common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int userCount = 0;
    
    private String uniqueID;
    private String username;
    private String password;
    private USER_ROLE role;
    private String[] chatAccess;
   
    
    
    public User(String username, String password, USER_ROLE role) {
        this.uniqueID = "USER_" + (++userCount);
        this.username = username;
        this.password = password;
        this.role = role;
        
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
    
    
    
    @Override
    public String toString() {
        return uniqueID + "," + username + "," + password + "," + role;
    }


	public String[] getChatAccess() {
		return chatAccess;
	}


	public void setChatAccess(String[] chatAccess) {
		this.chatAccess = chatAccess;
	}
}