package Client;

import Common.Message;

public interface UserInterface {
	
	void processCommands();

	void handleIncomingMessage(Message message);
	
}
