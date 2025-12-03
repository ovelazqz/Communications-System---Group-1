package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import Server.GroupChat;
import Common.Message;

class TestingGroupChat {

    @Test
    void constructor_setsFieldsCorrectly() {
        List<String> recipients = Arrays.asList("aaron", "brian");
        GroupChat chat = new GroupChat("Friends", recipients);

        assertNotNull(chat.getChatID());
        assertEquals("Friends", chat.getChatName());
        // assuming getRecipients returns a comma-separated string
        assertEquals("aaron, brian", chat.getRecipients());
        assertEquals("", chat.getMessages()); // no messages yet
    }

    @Test
    void constructor_allowsEmptyRecipientsList() {
        GroupChat chat = new GroupChat("NoChatMembersYet", Collections.emptyList());

        assertNotNull(chat.getChatID());
        assertEquals("NoChatMembersYet", chat.getChatName());
        assertEquals("", chat.getRecipients()); // no recipients -> empty string
    }

    @Test
    void addMessage_appendsToMessages() {
        List<String> recipients = Arrays.asList("aaron", "brian");
        GroupChat chat = new GroupChat("Friends", recipients);

        // TODO: adjust to match your actual Message constructor
        Message m1 = new Message("aaron", "Hi everyone!", "2025-12-01 10:00:00");
        Message m2 = new Message("brian", "Hello!", "2025-12-01 10:01:00");

        chat.addMessage(m1);
        chat.addMessage(m2);

        List<Message> allMessages = chat.getMessages();
        assertTrue(allMessages.contains("Hi everyone!"));
        assertTrue(allMessages.contains("Hello!"));
        assertTrue(allMessages.contains(System.lineSeparator())); // joined with newlines
    }

    @Test
    void addMessage_null_throwsException() {
        GroupChat chat = new GroupChat("Friends", Arrays.asList("aaron", "brian"));

        assertThrows(IllegalArgumentException.class,
                     () -> chat.addMessage(null));
    }

    @Test
    void multipleGroupChats_haveDifferentIds() {
        GroupChat c1 = new GroupChat("Group1", Arrays.asList("a", "b"));
        GroupChat c2 = new GroupChat("Group2", Arrays.asList("c", "d"));

        assertNotEquals(c1.getChatID(), c2.getChatID());
    }

    @Test
    void saveChat_runsWithoutError() {
        GroupChat chat = new GroupChat("Friends", Arrays.asList("aaron", "brian"));

        assertDoesNotThrow(chat::saveChat);
    }
}
