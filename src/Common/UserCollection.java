package Common;

import java.io.*;
import java.util.Scanner;


public class UserCollection {

	// Data fields
	
	/** The current number of DVDs in the array */
	private int numusers;
	
	/** The array to contain the DVDs */
	private User[] userArray;
	
	/** The name of the data file that contains user data */
	private String sourceName = "/src/Server.loginInfo/logininfo.txt";
	
	/** Boolean flag to indicate whether the User collection was
	    modified since it was last saved. */
	private boolean modified;
	
	
	public UserCollection() {
		numusers = 0;
		userArray = new User[7];
		
		modified = false;
		
	}
	
	public String toString() {
		
		String users = "numusers = " + numusers + "\n" + "userArray.length = " + userArray.length + "\n";
		
		for (int i = 0; i < numusers; i++) {
			users += "dvdArray[" + i + "] = " +
	                userArray[i].getUniqueID() + "/" +
	                userArray[i].getUsername() + "/" +
	                userArray[i].getRole() + "\n";
		}


		return users;
	}

	public void addOrModifyUser(String username, String password, String role) {

		// Finish ****
		if (!isValidUsername(username)) {
			return; 
		}
		
		// must convert String role into appropriate type

		
		for (int i = 0; i < numusers; i++) {
			if (userArray[i].getUsername().compareTo(username) == 0) {
				userArray[i].setPassword(password);
	            userArray[i].setRole(role);
	            modified = true;
	            return;							
			}
		}
		

		

		if (numusers >= userArray.length) {
			User[] upgradedArray = new User[userArray.length * 2];
			for (int i = 0; i < userArray.length; i++) {
				upgradedArray[i] = userArray[i];
			}
			
			userArray = upgradedArray;
		}

		
		
		User newUser = new User(username, password, role);
		
		// now to find where exactly it will go
		int index = 0;
		
		while (index < numusers && userArray[index] != null && userArray[index].getUsername().compareTo(username) < 0) {
			index++;
		}
		
		
		for (int i = numusers; i > index; i--) {
			userArray[i] = userArray[i - 1];
		}
		
		
		userArray[index] = newUser; 
		numusers++;				
		modified = true;	 
	
	}
	
	public boolean isValidUsername(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeDVD(String username) {
		
		for (int i = 0; i < numusers; i++) {
			if (userArray[i] != null &&  userArray[i].getUsername().compareTo(username) == 0) {
				for (int j = i; j < numusers - 1; j++) {
					userArray[j] = userArray[j+1];
					}
			numusers--;
			userArray[numusers] = null;
			modified = true;
			return; 
				
			}
			
		}
		

	}
			
	
	public void loadData(String filename) {

		sourceName = filename;
		
		File myFile = new File(filename);
		
		if (!myFile.exists()) {
			return;
		}
		
	    
	    try {
	    	Scanner scanner = new Scanner(myFile);
	    	
	    	while (scanner.hasNextLine()) {
	    		String data = scanner.nextLine().trim();
	    		
	    		String[] parts = data.split(",");
	    	    if (parts.length == 3) {
	    	        String username = parts[0].trim();
	    	        String password = parts[1].trim();
	    	        String role = parts[2].trim();
	    	        
	    	        

	    	        addOrModifyUser(username, password, role);
	    		
	    		
	    	    }
	    	
	    	}
	    	
	    scanner.close();	
	    } catch (Exception e) { 
	    
	    }
	    

		
	}
	
	public void save() {

		if (modified) {
			File file = new File(sourceName);
			try {
	    		if(file.createNewFile()) {					
					}
	    		
	    		FileWriter myWriter = new FileWriter(sourceName); //Create a writer
				for (int i = 0; i < numusers; i++) {
					 myWriter.write(userArray[i].toString() + "\n");
				}
				
				myWriter.close(); //close the writer
				modified = false;
				
				
			} catch (IOException e1) {
			}	
		} else {
		}






	}

	// Additional private helper methods go here:


}