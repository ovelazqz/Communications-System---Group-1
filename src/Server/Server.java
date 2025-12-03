package Server;
import Common.Message;
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
	    
	    System.out.println("DEBUG: ChatManager initialized");
	    System.out.println("DEBUG: Total chats loaded: " + chatManager.getAllChats().size());
	    
	    // Add shutdown hook to save chats when server closes
	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	        System.out.println("Server shutting down, saving chats...");
	        chatManager.saveAllChatsToDisk();
	    }));
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
	        chatManager.saveAllChatsToDisk();
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
	
	
	// Doesnt work
	public synchronized boolean sendMessageToUser(String username, Message message) {
	    for (ClientHandler handler : onlineClients.values()) {
	        if (handler.getUsername().equals(username)) {
	            try {
	                handler.sendMessage(message);
	                return true; // delivered
	            } catch (IOException e) {
	                System.out.println("Error sending message to " + username + ": " + e.getMessage());
	                return false;
	            }
	        }
	    }
	    // not online
	    System.out.println("User " + username + " is not online");
	    return false;
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
    	protected String sessionID;
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
	           } else if (type.equals("manage_user") && isLoggedIn) {
	        	   handManageUser(message);
	           } else if (type.equals("confirm connection")) {
	               handleConfirmConnection(message);
	           } else if (type.equals("get_user_list")) {
	        	    handleGetUserList(message);
	           } else {
	               sendError("Unknown message type");
	           }
	   	}
	   	
		private void handleCreatePrivateChat(Message message) {
		    String recipient = (String) message.getData("recipient");
		    
		    System.out.println("DEBUG: Creating private chat with recipient: " + recipient);
		    
		    if (recipient == null || recipient.isEmpty()) {
		        sendError("Recipient cannot be empty");
		        return;
		    }
		    
		    try {
		        List<String> participants = Arrays.asList(username, recipient);
		        PrivateChat chat = server.getChatManager().createPrivateChat(participants);
		        
		        String chatID = chat.getChatID();
		        
		        System.out.println("DEBUG: Created chat with ID: " + chatID);
		        
		        // Add chat access for both users
		        server.getUserCollection().addChatToUser(username, "Private_" + chatID);
		        server.getUserCollection().addChatToUser(recipient, "Private_" + chatID);
		        
		        Map<String, Object> payload = new HashMap<>();
		        payload.put("chatID", chatID);
		        payload.put("chatName", chat.getChatName());
		        payload.put("chatType", "private");
		        
		        Message response = new Message("response", "success", "create_private", payload);
		        sendMessage(response);
		        
		        System.out.println("Private chat created: " + chatID + " between " + username + " and " + recipient);
		        
		    } catch (Exception e) {
		        System.out.println("Error creating private chat: " + e.getMessage());
		        e.printStackTrace();
		        sendError("Error creating private chat: " + e.getMessage());
		    }
		}
	
	
		private void handleCreateGroupChat(Message message) {
		    String groupName = (String) message.getData("groupName");
		    
		    System.out.println("DEBUG: Creating group chat with name: " + groupName);
		    
		    if (groupName == null || groupName.isEmpty()) {
		        sendError("Group name cannot be empty");
		        return;
		    }
		    
		    try {
		        List<String> participants = new ArrayList<>();
		        participants.add(username);
		        
		        GroupChat chat = server.getChatManager().createGroupChat(groupName, participants);
		        
		        String chatID = chat.getChatID();
		        
		        System.out.println("DEBUG: Created group chat with ID: " + chatID);
		        
		        // Add chat access for the creator
		        server.getUserCollection().addChatToUser(username, "Group_" + chatID);
		        
		        Map<String, Object> payload = new HashMap<>();
		        payload.put("chatID", chatID);
		        payload.put("chatName", groupName);
		        payload.put("chatType", "group");
		        
		        Message response = new Message("response", "success", "create_group", payload);
		        sendMessage(response);
		        
		        System.out.println("Group chat created: " + chatID + " - " + groupName);
		        
		    } catch (Exception e) {
		        System.out.println("Error creating group chat: " + e.getMessage());
		        e.printStackTrace();
		        sendError("Error creating group chat: " + e.getMessage());
		    }
		}
	
	   	
	   	
	   	private void handleGetChatList(Message message) {
	   	    try {
	   	        
	   	        String[] userChats = server.getUserCollection().loadUserChats(username);
	   	        
	   	        Map<String, Object> payload = new HashMap<>();
	   	        payload.put("chats", userChats != null ? userChats : new String[0]);
	   	        Message response = new Message("response", "success", "get_chat_list", payload);
	   	        
	   	        sendMessage(response);
	   	        System.out.println("Chat list retrieved for user: " + username);
	   	        
	   	    } catch (Exception e) {
	   	        System.out.println("Error retrieving chat list: " + e.getMessage());
	   	        sendError("Error retrieving chat list: " + e.getMessage());
	   	    }
	   	}
	
	   	
	   	public void addParticipantToGroupChat(String groupName, String username) {
	   	    try {
	   	        GroupChat chat = server.getChatManager().findGroupChatByName(groupName);
	   	        if (chat != null) {
	   	            chat.addRecipient(username);
	   	            server.getUserCollection().addChatToUser(username, "Group_" + chat.getChatID());
	   	            System.out.println("Added " + username + " to group chat: " + groupName);
	   	        } else {
	   	            sendError("Group chat not found: " + groupName);
	   	        }
	   	    } catch (Exception e) {
	   	        System.out.println("Error adding participant: " + e.getMessage());
	   	        sendError("Error adding participant: " + e.getMessage());
	   	    }
	   	}
			private void handManageUser(Message message) {
				String username = (String) message.getData("username");
			    String password = (String) message.getData("password");
			    String role = (String) message.getData("role");
	
			    try {
			        // Validate input
			        if (username == null || username.trim().isEmpty()) {
			            sendError("Username cannot be empty");
			            return;
			        }
	
			        if (password == null || password.trim().isEmpty()) {
			            sendError("Password cannot be empty");
			            return;
			        }
	
			        if (password.length() < 5) {
			            sendError("Password must be at least 6 characters");
			            return;
			        }
	
			        if (role == null || role.trim().isEmpty()) {
			            sendError("Role must be specified");
			            return;
			        }
	
			        // Convert string to USER_ROLE enum
			        Common.USER_ROLE userRole;
			        try {
			            userRole = Common.USER_ROLE.valueOf(role.toUpperCase());
			        } catch (IllegalArgumentException e) {
			            sendError("Invalid role: " + role);
			            return;
			        }
	
	
			        // Add or modify user in collection
			        server.getUserCollection().addOrModifyUser(username, password, userRole);
	
			        // Save to file
			        server.getUserCollection().save();
			    } catch (Exception e) {
			        System.out.println("Error managing user: " + e.getMessage());
			        e.printStackTrace();
			        sendError("Error managing user: " + e.getMessage());
			    }
			}
			
		
			public synchronized String[] getAllUsernames() {
				return userCollection.getAllUsernames();
			}
	
			private void handleGetUserList(Message message) {
				String[] allUsernames = getAllUsernames();
			    
			    Map<String, Object> payload = new HashMap<>();
			    payload.put("users", allUsernames);
			    Message response = new Message("response", "success", "get_user_list", payload);
			    
			    try {
			        sendMessage(response);
			    } catch (IOException e) {
			        System.out.println("Error sending user list: " + e.getMessage());
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
			
			private void sendMessage(Message message) throws IOException {
				output.writeObject(message);
		        output.flush();
				
			}
			
			private void saveChatToFile(String chatID) {
			    try {
			        String chatsDir = "src/Server/Chats";
			        File directory = new File(chatsDir);
			        if (!directory.exists()) {
			            directory.mkdirs();
			            System.out.println("Created directory: " + chatsDir);
			        }
			        
			        PrivateChat privateChat = server.getChatManager().getPrivateChat(chatID);
			        if (privateChat != null) {
			            String filename = "PRIVATE_" + chatID + ".txt";
			            String filePath = chatsDir + "/" + filename;
			            saveMessagesToFile(filePath, privateChat.getMessages());
			            return;
			        }
			        
			        GroupChat groupChat = server.getChatManager().getGroupChat(chatID);
			        if (groupChat != null) {
			            String filename = "GROUP_" + chatID + ".txt";
			            String filePath = chatsDir + "/" + filename;
			            saveMessagesToFile(filePath, groupChat.getMessages());
			        }
			    } catch (Exception e) {
			        System.out.println("Error saving chat: " + e.getMessage());
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
			
		    private void handleSendMessage(Message message) {
		        String chatIdentifier = (String) message.getData("chatID");
		        String text = message.getText();
	
		        System.out.println("DEBUG: handleSendMessage called with chatID: " + chatIdentifier + ", text: " + text);
	
		        if (chatIdentifier == null || chatIdentifier.isEmpty()) {
		            sendError("Chat ID cannot be empty");
		            return;
		        }
	
		        if (text == null || text.isEmpty()) {
		            sendError("Message text cannot be empty");
		            return;
		        }
	
		        try {
		            message.setSender(username);
		            message.addData("chatID", chatIdentifier);
	
		            System.out.println("DEBUG: Checking if chat exists: " + chatIdentifier);
		            
		            boolean exists = server.getChatManager().chatExists(chatIdentifier);
		            System.out.println("DEBUG: Chat exists? " + exists);
		            
		            if (!exists) {
		                System.out.println("DEBUG: Chat not found, sending error");
		                sendError("Chat not found: " + chatIdentifier);
		                return;
		            }
	
		            System.out.println("DEBUG: Adding message to chat: " + chatIdentifier);
		            
		            server.getChatManager().addMessageToChat(chatIdentifier, message);
	
		            System.out.println("DEBUG: Message added successfully");
	
		            routeMessageToRecipients(chatIdentifier, message);
	
		            Map<String, Object> payload = new HashMap<>();
		            payload.put("messageID", message.getuID());
		            payload.put("status", "delivered");
		            payload.put("actualChatID", chatIdentifier);
		            Message response = new Message("response", "success", "send_msg", payload);
		            sendMessage(response);
	
		            System.out.println("Message saved to chat: " + chatIdentifier);
	
		        } catch (Exception e) {
		            System.out.println("Error sending message: " + e.getMessage());
		            e.printStackTrace();
		            sendError("Error sending message: " + e.getMessage());
		        }
		    }
			
			private void routeMessageToRecipients(String chatID, Message message) {
			    try {
			        PrivateChat privateChat = server.getChatManager().getPrivateChat(chatID);
			        if (privateChat != null) {
			            for (String recipient : privateChat.getRecipientList()) {
			                if (!recipient.equals(username)) {
			                    Message forwardMsg = message.copy();
			                    forwardMsg.addData("chatID", chatID);
			                    boolean delivered = server.sendMessageToUser(recipient, forwardMsg);
			                    
			                }
			            }
			            return;
			        }
	
			        GroupChat groupChat = server.getChatManager().getGroupChat(chatID);
			        if (groupChat != null) {
			            for (String participant : groupChat.getRecipientList()) {
			                if (!participant.equals(username)) {
			                    Message forwardMsg = message.copy();
			                    forwardMsg.addData("chatID", chatID);
			                    boolean delivered = server.sendMessageToUser(participant, forwardMsg);
			                }
			            }
			        }
			    } catch (Exception e) {
			        System.out.println("Error routing message: " + e.getMessage());
			    }
			}
	
			private void saveMessagesToFile(String filePath, String messagesText) {
			    try {
			        FileWriter writer = new FileWriter(filePath, false); // false = overwrite
			        writer.write(messagesText);
			        writer.close();
			        
			        System.out.println("Chat saved to: " + filePath);
			    } catch (IOException e) {
			        System.out.println("Error writing chat file: " + e.getMessage());
			    }
			}
			
	    }
}