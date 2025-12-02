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
	
	
	public synchronized boolean isUserOnline(String uniqueID) {
	    return onlineClients.containsKey(uniqueID);
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
	
	public synchronized void broadcast(Message message) {
	    for (ClientHandler handler : onlineClients.values()) {
	        try {
	            handler.sendMessage(message);
	        } catch (IOException e) {
	            System.out.println("Error broadcasting message: " + e.getMessage());
	        }
	    }
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
           }else if (type.equals("view_chat_logs") && isLoggedIn) {
        	    handleViewChatLogs(message);
           } 
           	else if (type.equals("get_chat_list") && isLoggedIn) {
               handleGetChatList(message);
           } else if (type.equals("confirm connection")) {
               handleConfirmConnection(message);
           } else {
               sendError("Unknown message type");
           }
   	}
   	
   	
 // Server.java – inside ClientHandler

   	private void handleViewChatLogs(Message message) {
   	    // Use ChatManager to build a full log string
   	    String logs = server.getChatManager().toString(); // This already lists all private + group chats

   	    Map<String, Object> payload = new HashMap<>();
   	    payload.put("logs", logs);

   	    // "chat_logs" is a sub-type so the client can recognize it
   	    Message response = new Message("response", "success", "chat_logs", payload);

   	    try {
   	        sendMessage(response);
   	    } catch (IOException e) {
   	        System.out.println("Error sending chat logs: " + e.getMessage());
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
		    String groupName = (String) message.getData("groupName");

		    List<String> recipients = new ArrayList<>();
		    recipients.add(username);

		    try {
		        GroupChat chat = server.getChatManager().createGroupChat(groupName, recipients);
		        String chatID = chat.getChatID();

		        Map<String, Object> payload = new HashMap<>();
		        payload.put("chatID", chatID);
		        payload.put("groupName", groupName);
		        payload.put("recipients", recipients);

		        Message response = new Message("response", "success", sessionID, payload);
		        sendMessage(response);

		        Message notify = new Message("new_group_chat", "", payload);
		        server.broadcast(notify);

		    } catch (IllegalArgumentException ex) {
		        // group name already exists, send an error back just to the creator
		        Map<String, Object> payload = new HashMap<>();
		        payload.put("error", ex.getMessage());
		        Message response = new Message("response", "error", sessionID, payload);
		        try {
		            sendMessage(response);
		        } catch (IOException e) {
		            System.out.println("Error sending create group error: " + e.getMessage());
		        }
		    } catch (IOException e) {
		        System.out.println("Error sending create group response: " + e.getMessage());
		    }
		}

		
		// Broadcast a message to ALL connected clients
		



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
		    String text   = message.getText();

		    if (chatID == null || chatID.isEmpty()) {
		        System.out.println("Missing chatID in send_message");
		        return;
		    }

		    // tag sender for history
		    message.setSender(username);

		    // save in server-side chat history
		    server.getChatManager().addMessageToChat(chatID, message);

		    // figure out recipients for this chat
		    List<String> recipients = new ArrayList<>();

		    // Try as group chat (your ChatManager stores group chats keyed by name)
		    GroupChat g = server.getChatManager().getGroupChat(chatID);
		    if (g != null) {
		        recipients.addAll(g.getRecipients());   // List<String> of usernames
		    } else {
		        // Try as private chat
		        PrivateChat p = server.getChatManager().getPrivateChat(chatID);
		        if (p != null) {
		            String recString = p.getRecipients(); // e.g. "user1, user2"
		            if (recString != null) {
		                for (String r : recString.split(",")) {
		                    String trimmed = r.trim();
		                    if (!trimmed.isEmpty()) {
		                        recipients.add(trimmed);
		                    }
		                }
		            }
		        }
		    }

		    // If we couldn't find chat or recipients, just stop
		    if (recipients.isEmpty()) {
		        System.out.println("No recipients for chatID=" + chatID);
		        return;
		    }

		    // Build message that will be sent to other clients
		    Message forward = new Message("receive_message");
		    forward.setText(text);
		    forward.setSender(username);
		    forward.getPayload().put("chatID", chatID);

		    // Send ONLY to users in that chat (and not back to sender)
		    for (ClientHandler handler : server.onlineClients.values()) {
		        String targetUser = handler.getUsername();
		        if (recipients.contains(targetUser) && !targetUser.equals(username)) {
		            try {
		                handler.sendMessage(forward);
		            } catch (IOException e) {
		                System.out.println("Error forwarding message: " + e.getMessage());
		            }
		        }
		    }

		    // Optional: send simple ACK back to sender
		    Map<String, Object> payload = new HashMap<>();
		    payload.put("messageID", "msg");
		    Message response = new Message("response", "success", payload);
		    try {
		        sendMessage(response);
		    } catch (IOException e) {
		        System.out.println("Error sending send_message response: " + e.getMessage());
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
		        // Check if user is already online using uniqueID
		        if (server.isUserOnline(user.getUniqueID())) {
		            sendError("User is already logged in from another location");
		            return;
		        }
		        
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

