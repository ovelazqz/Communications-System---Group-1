package Common;

import java.io.*;
import java.util.Scanner;

public class UserCollection {

	// Data fields
	
	/** The current number of users in the array */
	private int numusers;
	
	/** The array to contain the users */
	private User[] userArray;
	
	/** The name of the data file that contains user data */
	private String sourceName = "src/Server/loginInfo/loginInfo.txt"; 
	
	
	private boolean modified;
	
	
	public UserCollection() {
		numusers = 0;
		userArray = new User[7];
		modified = false;
		
		
		loadData();
	}
	
	public String toString() {
		String users = "numusers = " + numusers + "\n" + "userArray.length = " + userArray.length + "\n";
		
		for (int i = 0; i < numusers; i++) {
			users += "userArray[" + i + "] = " +
	                userArray[i].getUniqueID() + "/" +
	                userArray[i].getUsername() + "/" +
	                userArray[i].getRole() + "\n";
		}

		return users;
	}


	public void addOrModifyUser(String username, String password, USER_ROLE role) {
	    if (!isValidUsername(username)) {
	        return;
	    }

	    // Check if user already exists
	    for (int i = 0; i < numusers; i++) {
	        if (userArray[i].getUsername().equals(username)) {
	            userArray[i].setPassword(password);
	            userArray[i].setRole(role);
	            modified = true;
	            return;
	        }
	    }

	    // Resize array if needed
	    if (numusers >= userArray.length) {
	        User[] upgradedArray = new User[userArray.length * 2];
	        for (int i = 0; i < userArray.length; i++) {
	            upgradedArray[i] = userArray[i];
	        }
	        userArray = upgradedArray;
	    }

	    // Create new user and find insertion point
	    User newUser = new User(username, password, role);
	    int index = 0;

	    while (index < numusers && userArray[index] != null && 
	           userArray[index].getUsername().compareTo(username) < 0) {
	        index++;
	    }

	    // Shift elements to make room
	    for (int i = numusers; i > index; i--) {
	        userArray[i] = userArray[i - 1];
	    }

	    userArray[index] = newUser;
	    numusers++;
	    modified = true;
	}

	public void removeUser(String username) {
	    for (int i = 0; i < numusers; i++) {
	        if (userArray[i] != null && userArray[i].getUsername().equals(username)) {
	            for (int j = i; j < numusers - 1; j++) {
	                userArray[j] = userArray[j + 1];
	            }
	            numusers--;
	            userArray[numusers] = null;
	            modified = true;
	            return;
	        }
	    }
	}
	
	public User getUser(String username) {
		for (int i = 0; i < numusers; i++) {
			if (userArray[i] != null && userArray[i].getUsername().equals(username)) {
				return userArray[i];
			}
		}
		return null;
	}

	
	public User getUserByID(String uniqueID) {
		for (int i = 0; i < numusers; i++) {
			if (userArray[i] != null && userArray[i].getUniqueID().equals(uniqueID)) {
				return userArray[i];
			}
		}
		return null;
	}

	
	public String[] getAllUsernames() {
		String[] usernames = new String[numusers];
		for (int i = 0; i < numusers; i++) {
			if (userArray[i] != null) {
				usernames[i] = userArray[i].getUsername();
			}
		}
		return usernames;
	}

	
	public boolean userExists(String username) {
		return getUser(username) != null;
	}

	
	public int getNumUsers() {
		return numusers;
	}

	
	public void loadData() {
	    
	    File myFile = new File(sourceName);

	    if (!myFile.exists()) {
	        System.out.println("File not found: " + sourceName);
	        return;
	    }

	    try {
	        Scanner scanner = new Scanner(myFile);

	        while (scanner.hasNextLine()) {
	            String data = scanner.nextLine().trim();

	            if (data.isEmpty()) continue;  // Skip empty lines

	            String[] parts = data.split(",");
	            if (parts.length == 4) {
//	            	String uID = parts[0].trim();
	                String username = parts[1].trim();
	                String password = parts[2].trim();
	                String roleStr = parts[3].trim();

	                // Skip empty roles
	                if (roleStr.isEmpty()) {
	                    continue;
	                }

	                try {
	                    // Convert string to USER_ROLE enum
	                    USER_ROLE role = USER_ROLE.valueOf(roleStr);
	                    addOrModifyUser(username, password, role);
	                } catch (IllegalArgumentException e) {
	                    System.out.println("Invalid role: " + roleStr);
	                }
	            }
	        }

	        scanner.close();
	    } catch (FileNotFoundException e) {
	        System.out.println("Error reading file: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Unexpected error loading data: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	
	public void save() {
	    if (modified) {
	        File file = new File(sourceName);
	        try {
	            if (file.createNewFile()) {
	                System.out.println("Created new file: " + sourceName);
	            }

	            FileWriter myWriter = new FileWriter(sourceName);
	            for (int i = 0; i < numusers; i++) {
	                if (userArray[i] != null) {
	                    myWriter.write(userArray[i].toString() + "\n");
	                }
	            }

	            myWriter.close();
	            modified = false;
	            System.out.println("Saved successfully");

	        } catch (IOException e) {
	            System.out.println("Error saving file: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	}

	
	public boolean isValidUsername(String username) {
	    // Check if username is empty
	    if (username == null || username.trim().isEmpty()) {
	        return false;
	    }
	    
	    // Username is valid (already exists check is done in addOrModifyUser)
	    return true;
	}
}