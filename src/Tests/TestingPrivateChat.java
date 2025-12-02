package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Server.PrivateChat;
import Common.Message;

class TestingPrivateChat {

    @Test
    void constructor_setsFieldsCorrectly() {
        PrivateChat chat = new PrivateChat("Friends", "aaron, brian");

        assertNotNull(chat.getChatID());
        assertEquals("Friends", chat.getChatName());
        assertEquals("aaron, brian", chat.getRecipients());

        // no messages yet
        assertEquals("", chat.getMessages());
    }

    @Test
    void getMessages_returnsJoinedMessageStrings() {
        PrivateChat chat = new PrivateChat("Friends", "aaron, brian");

        // TODO: adjust this to your real Message constructor
        Message m1 = new Message("aaron", "Hi Brian!", "2025-12-01 10:00:00");
        Message m2 = new Message("brian", "Hi Aaron!", "2025-12-01 10:01:00");

        chat.addMessage(m1);
        chat.addMessage(m2);

        String all = chat.getMessages();

        assertTrue(all.contains("Hi Brian!"));
        assertTrue(all.contains("Hi Aaron!"));
        // Should contain a line break between them
        assertTrue(all.contains(System.lineSeparator()));
    }

    @Test
    void addMessage_null_throwsException() {
        PrivateChat chat = new PrivateChat("Friends", "aaron, brian");

        assertThrows(IllegalArgumentException.class,
                     () -> chat.addMessage(null));
    }

    @Test
    void multipleChats_haveDifferentIds() {
        PrivateChat c1 = new PrivateChat("Chat1", "a, b");
        PrivateChat c2 = new PrivateChat("Chat2", "c, d");

        assertNotEquals(c1.getChatID(), c2.getChatID());
    }

    @Test
    void saveChat_doesNotThrow() {
        PrivateChat chat = new PrivateChat("Friends", "aaron, brian");

        // Just ensure it runs without error for now
        assertDoesNotThrow(chat::saveChat);
    }
}

