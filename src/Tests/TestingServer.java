package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import Server.Server;

class TestingServer {

    @Test
    void constructor_initializesCollectionsAndManagers() throws IOException {
        Server server = new Server();

        assertNotNull(server.getUserCollection());
        assertNotNull(server.getChatManager());

        // Won't call start() here to avoid blocking on accept()
        server.stop(); // clean up the socket
    }

    @Test
    void registerAndUnregisterClient_updatesOnlineStatus() throws IOException {
        Server server = new Server();
        String uniqueID = "user-123";

        // initially offline
        assertFalse(server.isUserOnline(uniqueID));

        // register with null handler
        server.registerClient(uniqueID, null);
        assertTrue(server.isUserOnline(uniqueID));

        // unregister
        server.unregisterClient(uniqueID);
        assertFalse(server.isUserOnline(uniqueID));

        server.stop();
    }

    @Test
    void isUserOnline_returnsFalseForUnknownUser() throws IOException {
        Server server = new Server();

        assertFalse(server.isUserOnline("non-existent-id"));

        server.stop();
    }

    @Test
    void stop_doesNotThrow() throws IOException {
        Server server = new Server();

        assertDoesNotThrow(server::stop);
    }
}
