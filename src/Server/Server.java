package Server;
// idk if this right

import Common.Message; // access to Message Class
import java.net.ServerSocket;
import java.util.List;

// idk if this is right 
public class Server {
	private ServerSocket serverSocket;
	private List<ClientHandler> clientHandlers;
	private UserCollection userCollection;
	private boolean isRunning;
	
	
	
	public void start() {
		// add code 
	}
	
	public void stop() {
		// add code
	}
							// idk if this right 
	public void handleRequest(Request request, ClinetHandler handler) {
		//add code 
	}
	
	public void broadcast(Response response) {
		//add code 
	}
	
	public void registerClint(ClientHandler handler) {
		//add code
	}
	
	public void removeClient(ClientHandler handler) {
		//add code
	}

}
