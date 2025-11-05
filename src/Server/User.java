package Server;

import java.util.*;

public class User {
	private static int count = 0;
	private final String uniqueID;
	private String username;
	private String password;
	private USER_ROLE role;
	private List<String >chatAccess;
	private boolean modified;
	
	public User(String username, String password, String role) {
		// error checking for existing username, there will be a method in UserCollection for this
		// add here
		
		this.password = password;
		// error checking for role type
		// add here
		
		this.uniqueID = "user#" + ++count;
		this.modified = false;
	}
	
}
