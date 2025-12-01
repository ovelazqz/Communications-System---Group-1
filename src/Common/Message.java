package Common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

	private static int count = 0;
    
    private int uID;
    private final String type;  // Immutable 
    private String status;      // success, failure
    private String text;        // Message content
    private String sender;      // Who sent this message
    private String sessionID;   // Tracks request/response pairs
    private Map<String, Object> payload;  
    
    // CONSTRUCTOR 1: Basic message (login, logout, confirm connection)
    public Message(String type) {
    	this.uID = ++count;
        this.type = type;
        this.status = "";
        this.text = "";
        this.sender = "";
        this.sessionID = "";
        this.payload = new HashMap<>();
    }
    
    // CONSTRUCTOR 2: One-to-one message (direct messages between users)
    public Message(String type, String sender, String recipient) {
    	this.uID = ++count;
        this.type = type;
        this.status = "";
        this.text = "";
        this.sender = sender;
        this.sessionID = "";
        this.payload = new HashMap<>();
        this.payload.put("recipient", recipient);
    }
    
    // CONSTRUCTOR 3: Request with payload (client → server)
    public Message(String type, String sessionID, Map<String, Object> payload) {
    	this.uID = ++count;
        this.type = type;
        this.status = "";
        this.text = "";
        this.sender = "";
        this.sessionID = sessionID;
        this.payload = new HashMap<>(payload);
    }
    
    // CONSTRUCTOR 4: Response with status and payload (server → client) 
    public Message(String type, String status, String sessionID, Map<String, Object> payload) {
    	this.uID = ++count;
        this.type = type;
        this.status = status;
        this.text = "";
        this.sender = "";
        this.sessionID = sessionID;
        this.payload = new HashMap<>(payload);
    }
    
    
    
    
    public String getType() {
        return type;
    }
    

    public String getStatus() {
        return status;
    }
    

    public String getText() {
        return text;
    }
    
 
    public String getSender() {
        return sender;
    }
    
 
    public String getSessionID() {
        return sessionID;
    }
    

    public Map<String, Object> getPayload() {
        return payload;
    }
    

    public void setStatus(String status) {
        this.status = status;
    }
    

    public void setText(String text) {
        this.text = text;
    }
    

    public void setSender(String sender) {
        this.sender = sender;
    }
    

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
    
    public void setPayload(Map<String, Object> payload) {
        this.payload = new HashMap<>(payload);
    }
    

    public void addData(String key, Object value) {
        this.payload.put(key, value);
    }
    

    public Object getData(String key) {
        return this.payload.get(key);
    }
    

    public void removeData(String key) {
        this.payload.remove(key);
    }
    

    public boolean hasData(String key) {
        return this.payload.containsKey(key);
    }
    

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", sender='" + sender + '\'' +
                ", sessionID='" + sessionID + '\'' +
                ", text='" + text + '\'' +
                ", payload=" + payload +
                '}';
    }
    

    public Message copy() {
        Message copy = new Message(this.type);
        copy.setStatus(this.status);
        copy.setText(this.text);
        copy.setSender(this.sender);
        copy.setSessionID(this.sessionID);
        copy.setPayload(new HashMap<>(this.payload));
        return copy;
    }

	public int getuID() {
		return uID;
	}

	public void setuID(int uID) {
		this.uID = uID;
	}
}