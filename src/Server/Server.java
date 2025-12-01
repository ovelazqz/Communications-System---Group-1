package Server;
// idk if this right

import Common.Message; // access to Message Class
import Common.User;

import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList; // idk if we need it.

import Client.Request;

class Response { // add this idk if need or not
    private String text;
    public Response(String text) { this.text = text; }
    public String getText() { return text; }

// idk if this is right 
public class Server {
	private ServerSocket serverSocket;
	// Changed to User object, so a collection of Users each using a thread using a method below?
	// The User's ID will be their session ID, that way we can easily check if the user is online and if not then store message
	
	
	private List<User> clientHandlers;
	private UserCollection userCollection;
	private boolean isRunning;
	
	public Server() { // add this idk if need or not
		clientHandlers = new ArrayList<>();
		userCollection = new UserCollection();
		isRunning = false;
		
	}
	
	
	public void start() {
		// add code 
		System.out.println("server started.");
		isRunning = true;
	}
	
	public void stop() {
		// add code
		System.out.println("server stopped.");
		isRunning = false;
	}
							// idk if this right 
	public void handleRequest(Request request, User handler) {
		//add code 
		if (request == null || handler == null) return;
		// idk what type should like string or void
		 type = request.get(); //idk what get would be 
		 text = request.get();
		
		System.out.println("Handling request from: " + handler.getUsername());
		System.out.println("Request type: " + request.get());
		System.out.println("Request text: " + request.get());
		
		switch (type) {
		
		case "login":
			System.out.println(handler.getUsername() + "is trying to log in: " + text);
			break;
			
		case "send_message":
			System.out.println(handler.getUsername() + "send message: " + text);
		
		case "create_chat":
			System.out.println(handler.getUsername() + "wants to create chat: " +  text);
		
		case "logout":
			System.out.println(handler.getUsername() + "logged out.");
			
		default:
			System.out.println("unknown request type: " + type);
		}
	}
	
	public void broadcast(Response response) {
		//add code 
		if (response == null) return;
		
		for (User u : clientHandlers) {
			System.out.println("sending to " + u.getUsername() + ": " + response.get());
		}
	}
	
	public void registerClint(User handler) {
		//add code
		if (handler == null) return;
		
		clientHandlers.add(handler);
		System.out.println("user connected: " + handler.getUsername());
	}
	
	public void removeClient(User handler) {
		//add code
		if (handler == null) return;
		
		clientHandlers.remove(handler);
		System.out.println("user removed: " + handler.getUsername());
	}

}
