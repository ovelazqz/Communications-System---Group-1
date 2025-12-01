package Server;
import Common.Message;
import Common.REQUEST_TYPE;
import Common.User;
import Common.UserCollection;
import java.io.*;
import java.net.*;
import java.util.*;
public class Server {
	private ServerSocket serverSocket;
	private static final int PORT = 5050;
	private boolean isRunning;
	
	private Map<String, ClientHandler> onlineClients;
	private UserCollection userCollection;
	private ChatManager chatManager;
	
	public Server() throws IOException {
		this.serverSocket = new ServerSocket(PORT);
		this.onlineClients = new HashMap<>();
		this.userCollection = new UserCollection();
		this.chatManager = new ChatManager();
		this.isRunning = false;
		
	}
	
	public void start() {
		isRunning = true;
		
		System.out.println("Server started on port " + PORT);
		
		
		
		try {
			while (isRunning) {
				Socket clientSocket = serverSocket.accept();
				
				System.out.println("New Client Connected: " + clientSocket.getInetAddress());
				
				
				ClientHandler handler = new ClientHandler(clientSocket, this);
				new Thread(handler).start();
			}
		} catch (IOException e) {
			System.out.println("Server error: " + e.getMessage());	
		}
		
	}
	
	public void stop() {
		isRunning = false;
		try {
			serverSocket.close();
			System.out.println("Server Stopped!");
			
		} catch (IOException e) {
			System.out.println("Error Stopping Server: " + e.getMessage());
		}
	}
	
	
	
	public synchronized void registerClient(String sessionID, ClientHandler handler) {
		onlineClients.put(sessionID, handler);
		System.out.println("Client Registered: " + sessionID);
		System.out.println("Online Clients: " + onlineClients.size());
	}
	
	
	public synchronized void unregisterClient(String sessionID) {
		onlineClients.remove(sessionID);
		System.out.println("Client Unregistered: " + sessionID);
		System.out.println("Online Clients: " + onlineClients.size());
	}
	
	
	public synchronized boolean isUserOnline(String username) {
		return onlineClients.values().stream().anyMatch(handler -> handler.getUsername().equals(username));
		
	}
	
	
	public synchronized void sendMessageToUser(String username, Message message) {
       for (ClientHandler handler : onlineClients.values()) {
           if (handler.getUsername().equals(username)) {
               try {
                   handler.sendMessage(message);
               } catch (IOException e) {
                   System.out.println("Error sending message to " + username + ": " + e.getMessage());
               }
               return;
           }
       }
       System.out.println("User " + username + " is not online");
   }
	
	
	public synchronized void broadcastMessage(Message message, String excludeUser) {
       for (ClientHandler handler : onlineClients.values()) {
           if (!handler.getUsername().equals(excludeUser)) {
               try {
                   handler.sendMessage(message);
               } catch (IOException e) {
                   System.out.println("Error broadcasting message: " + e.getMessage());
               }
           }
       }
   }
	
	
	public UserCollection getUserCollection() {
       return userCollection;
   }
  
   public ChatManager getChatManager() {
       return chatManager;
   }
  
   public static void main(String[] args) {
       try {
           Server server = new Server();
           server.start();
       } catch (IOException e) {
           System.out.println("Failed to start server: " + e.getMessage());
       }
   }
  
  
  
   public class ClientHandler implements Runnable {
   	private Socket socket;
   	private ObjectOutputStream output;
   	private ObjectInputStream input;
   	private Server server;
   	private String username;
   	private String sessionID;
   	private boolean isLoggedIn;
   	
   	public ClientHandler(Socket socket, Server server) {
   		this.socket = socket;
   		this.server = server;
   		this.isLoggedIn = false;
   		this.username = null;
   		this.sessionID = null;
   		
   	}
   	
   	@Override
   	public void run() {
   		try {
   			output = new ObjectOutputStream(socket.getOutputStream());
   			input = new ObjectInputStream(socket.getInputStream());
   			
   			System.out.println("ClientHandler started for: " + socket.getInetAddress());
   			
   			
   			while (true) {
   				Message message = (Message) input.readObject();
   				
   				System.out.println("Received message type: " + message.getType());
   				
   				handleMessage(message);
   			}
   		} catch (IOException | ClassNotFoundException e) {
   			System.out.println("Error in ClientHandler" + e.getMessage());
   			cleanup();
   		}
   	}
   	
   	public void handleMessage(Message message) {
   		String type = message.getType();
   		
   		if (type.equals("login")) {
               handleLogin(message);
           } else if (type.equals("logout") && isLoggedIn) {
               handleLogout(message);
           } else if (type.equals("send_message") && isLoggedIn) {
               handleSendMessage(message);
           } else if (type.equals("create_private_chat") && isLoggedIn) {
               handleCreatePrivateChat(message);
           } else if (type.equals("create_group_chat") && isLoggedIn) {
               handleCreateGroupChat(message);
           } else if (type.equals("get_chat_list") && isLoggedIn) {
               handleGetChatList(message);
           } else if (type.equals("confirm connection")) {
               handleConfirmConnection(message);
           } else {
               sendError("Unknown message type");
           }
   	}
		private void sendError(String errorMessage) {
			 Map<String, Object> payload = new HashMap<>();
		        payload.put("error", errorMessage);
		        Message error = new Message("response", "failure", payload);
		        try {
		            sendMessage(error);
		        } catch (IOException e) {
		            System.out.println("Error sending error message: " + e.getMessage());
		        }
			
		}
		private void handleConfirmConnection(Message message) {
			Map<String, Object> payload = new HashMap<>();
			Message response = new Message("response", "success", "confirm", payload);
			
			try {
				sendMessage(response);
			} catch (IOException e) {
				System.out.println("Error cannot confirm connection: " + e.getMessage());
			}
			
		}
		private void handleGetChatList(Message message) {
			// Get all chats for this user
	        // to do: Implement fetching chat list
	       
	        Map<String, Object> payload = new HashMap<>();
	        Message response = new Message("response", "success", payload);
	        try {
	            sendMessage(response);
	        } catch (IOException e) {
	            System.out.println("Error sending chat list: " + e.getMessage());
	        }
			
		}
		private void handleCreateGroupChat(Message message) {
			
			// requires a recipients list *****************
			String groupName = (String) message.getData("groupName");
			
	       
	        // Create group chat
//	        String chatID = server.getChatManager().createGroupChat(groupName, username);
	       
	        // Send response
	        Map<String, Object> payload = new HashMap<>();
//	        payload.put("chatID", chatID);
	        Message response = new Message("response", "success", "create_group", payload);
	        try {
	            sendMessage(response);
	        } catch (IOException e) {
	            System.out.println("Error sending create group response: " + e.getMessage());
	        }
			
		}
		private void sendMessage(Message message) throws IOException {
			output.writeObject(message);
	        output.flush();
			
		}
		private void handleCreatePrivateChat(Message message) {
			String recipient = (String) message.getData("recipient");
			
			
			
//			String chatID = server.getChatManager().createPrivateChat(username, recipient);
			
			
			// send response
			
			Map<String, Object> payload = new HashMap<>();
//	        payload.put("chatID", chatID);
	        Message response = new Message("response", "success", payload);
	        try {
	            sendMessage(response);
	        } catch (IOException e) {
	            System.out.println("Error sending create chat response: " + e.getMessage());
	        }
			
		}
		private void handleSendMessage(Message message) {
			String chatID = (String) message.getData("chatID");
			String text = message.getText();
			
			
			server.getChatManager().addMessageToChat(chatID, message);
			
			
			// Send to recipient if Online
			// To do: implement message routing to recipient(s) ******************
			
			// Send confirmation to sender
			Map<String, Object> payload = new HashMap<>();
			payload.put("messageID", "msg");
			Message response = new Message("response", "success", payload);
			
			try {
				sendMessage(response);
			} catch (IOException e) {
				System.out.println("Error sending message response: " + e.getMessage());
			}
			
		}
		private void handleLogout(Message message) {
			if (isLoggedIn) {
				server.unregisterClient(sessionID);
				isLoggedIn = false;
				
				
				Message response = new Message("response", "success", "logout", new HashMap<>());
				
				try {
					sendMessage(response);
					
				} catch (IOException e ) {
					System.out.println("Error sending logout response: " + e.getMessage());
				}
			}
			
		}
		private void handleLogin(Message message) {
			String username = message.getSender();
			String password = message.getText();
			
			
			User user = server.getUserCollection().getUser(username);
			
			if (user != null && user.getPassword().equals(password)) {
				this.username = username;
				this.sessionID = user.getUniqueID();
				this.isLoggedIn = true;
				
				
				server.registerClient(sessionID, this);
				
				
				Map<String, Object> payload = new HashMap<>();
				payload.put("userID", user.getUniqueID());
				payload.put("username", username);
				payload.put("sessionID", sessionID);
				payload.put("userRole", user.getRole().toString());
				
				Message response = new Message("response", "success", "login", payload);
				
				try {
					sendMessage(response);
				} catch (IOException e) {
					System.out.println("Error sending login response: " + e.getMessage());
				}
			} else {
				sendError("Invalid username or password");
			}
			
		}
		
		private void cleanup() {
	        try {
	            if (isLoggedIn) {
	                server.unregisterClient(sessionID);
	            }
	            socket.close();
	        } catch (IOException e) {
	            System.out.println("Error during cleanup: " + e.getMessage());
	        }
	    }
	   
	    public String getUsername() {
	        return username;
	    }
	   
	    public String getSessionID() {
	        return sessionID;
	    }
	   
	    public boolean isLoggedIn() {
	        return isLoggedIn;
	    }
   }
  
  
	
}

