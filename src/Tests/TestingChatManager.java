package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import Server.ChatManager;
import Server.PrivateChat;
import Server.GroupChat;
import Common.Message;

class TestingChatManager {

	@Test
	void createPrivateChat_validRecipients_createsAndStoresChat() {
		ChatManager manager = new ChatManager();
		List<String> recipients = Arrays.asList("aaron", "brian");
		
		PrivateChat chat = manager.createPrivateChat(recipients);
		
		assertNotNull(chat);
		assertNotNull(chat.getChatID());
		assertEquals("aaron, brian", chat.getRecipients());
		
		assertSame(chat, manager.getPrivateChat(chat.getChatID()));
		
	}
	
	@Test
	void createPrivateChat_tooFewRecipients_throwsException() {
		ChatManager manager = new ChatManager();
		List<String> recipients = Collections.singletonList("aaron");
		
		assertThrows(IllegalArgumentException.class, 
				() -> manager.createPrivateChat(recipients));
	}
	
	@Test
	void createGroupChat_validData_createsAndStoresChat() {
		ChatManager manager = new ChatManager();
		List<String> recipients = Arrays.asList("aaron", "brian", "carlos");
		
		GroupChat chat = manager.createGroupChat("Friends", recipients);
		
		assertNotNull(chat);
		assertNotNull(chat.getChatID());
		assertEquals("Friends", chat.getChatName());
		assertSame(chat, manager.getGroupChat(chat.getChatID()));
		
	}
	
	@Test
	void createGroupChat_emptyName_throwsException() {
		ChatManager manager = new ChatManager();
		List<String> recipients = Arrays.asList("aaron", "brian");
		
		assertThrows(IllegalArgumentException.class,
				() -> manager.createGroupChat("", recipients));
		
	}
	

}
