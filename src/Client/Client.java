package Client;

public class Client {
	
	private static int count = 0;
	private final int sessionID;
	// more attributes required
	
	
	// constructor not complete
	public Client() {
		this.sessionID = ++count;
		// sessionID should probably be created in the server?
		// upon successful connection, a sessionID is made and returned.
	}
	
	
}
