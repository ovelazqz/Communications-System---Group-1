package Client;


import Common.*;
import Server.STATUS;

import java.io.*;
import java.net.*;

public class Client {
	
	// more attributes required
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private GUI_STATE state;
	private static boolean signedIn = false;
	private static boolean connected = false;
	private static final int CHARACTER_LIMIT = 200;
	private static Client instance;
	
	//private LoginGUI login;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		Client client = Client.getInstance();
		UserInterface user = new LoginGUI();
        user.processCommands();
		
		// needs to ensure it can only be executed once
	}
	
	
	
	private Client() throws IOException, ClassNotFoundException {
		attemptConnection();
		
		socket = new Socket("127.0.0.1", 5555);
		
		if (socket.isConnected()) {
	        System.out.println("Connected to Server...");
	    } else {
	        throw new IOException("Failed to connect to Server");
	    } // debugging prints will remove later
													  // need to add an if case to ensure connection was made
	}
	
	private void attemptConnection() throws IOException, ClassNotFoundException {
		try {
			socket = new Socket("127.0.0.1", 5555);
			
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			
			input = new ObjectInputStream(socket.getInputStream());
			
			
			Message checkConnection = new Message(/*REQUEST_TYPE.CONFIRM_CONNECTION Constructor for Message class has not been established*/);
			output.flush();
			
			Message response = (Message) input.readObject();
			
			if (response.getType().equals(REQUEST_TYPE.CONFIRM_CONNECTION) && response.getStatus().equals(STATUS.SUCCESS)) {
				connected = true;
				System.out.println("Connected to Server..."); // Debug
				
			} else {
				connected = false;
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Client getInstance() throws IOException, ClassNotFoundException { // Planned on using a Singleton here but did not work as intended... 
		if (instance == null) {														// will makes changes later.. so far works...
			instance = new Client();
		}
		
		return instance;
	}
	
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public boolean checkMessageLength(String msg) {
		if (msg.length() <= 200) {
			return true;
		}
		return false;
	}
	
	public void close() throws IOException {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
	
	
}
