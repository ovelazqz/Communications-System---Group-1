package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import Client.Client;

class TestingClient {

    // To test the message length helper
    @Test
    void checkMessageLength_respectsCharacterLimit() throws IOException, ClassNotFoundException {
        Client client = Client.getInstance();  // server needs to run

        String exactly200 = "a".repeat(200);
        String over200    = "a".repeat(201);

        assertTrue(client.checkMessageLength(exactly200));
        assertFalse(client.checkMessageLength(over200));
    }

    // To test that client tries to connect and sets the connected flag
    @Test
    void clientConnectsToServer_setsConnectedTrue() throws IOException, ClassNotFoundException {
        Client client = Client.getInstance();  // calls attemptConnection()

        assertTrue(client.isConnected());
    }

    // To test that storeChatID doesn't crash and mapping is usable via sendChatMessage
    @Test
    void storeChatID_doesNotThrow() {
        assertDoesNotThrow(() -> Client.storeChatID("FriendsChat", "chat-123"));
    }
}
