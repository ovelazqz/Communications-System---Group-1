package Server;
// idk if this right

import Common.Message; // access to Message Class
import java.net.ServerSocket;
import java.util.List;

import Client.Request;

// idk if this is right 
public class Server {
	private ServerSocket serverSocket;
	// Changed to User object, so a collection of Users each using a thread using a method below?
	private List<User> clientHandlers;
	private UserCollection userCollection;
	private boolean isRunning;
	
	
	
	public void start() {
		// add code 
	}
	
	public void stop() {
		// add code
	}
							// idk if this right 
	public void handleRequest(Request request, User handler) {
		//add code 
	}
	
	public void broadcast(Response response) {
		//add code 
	}
	
	public void registerClint(User handler) {
		//add code
	}
	
	public void removeClient(User handler) {
		//add code
	}

}
