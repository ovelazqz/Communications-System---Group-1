package Tests;

import static org.junit.jupiter.api.Assertions.*;

import Common.Message; 
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JUnitMessageClass {

    @BeforeEach
    void resetCounter() throws Exception {
        // Reset the private static count field
        var field = Message.class.getDeclaredField("count");
        field.setAccessible(true);
        field.setInt(null, 0);
    }

    @Test
    void testConstructor1_basicMessage() {
        Message msg = new Message("LOGIN");

        assertEquals("LOGIN", msg.getType());
        assertEquals("", msg.getStatus());
        assertEquals("", msg.getText());
        assertEquals("", msg.getSender());
        assertEquals("", msg.getSessionID());
        assertNotNull(msg.getPayload());
        assertTrue(msg.getPayload().isEmpty());

        assertEquals(1, msg.getuID());
    }

    @Test
    void testConstructor2_oneToOneMessage() {
        Message msg = new Message("DM", "Alice", "Bob");

        assertEquals("DM", msg.getType());
        assertEquals("Alice", msg.getSender());
        assertEquals("Bob", msg.getData("recipient"));
        assertNotNull(msg.getPayload());
        assertEquals(1, msg.getuID());
    }

    @Test
    void testConstructor3_requestWithPayload() {
        Map<String, Object> data = new HashMap<>();
        data.put("x", 10);

        Message msg = new Message("REQUEST", "session123", data);

        assertEquals("REQUEST", msg.getType());
        assertEquals("session123", msg.getSessionID());
        assertEquals(10, msg.getData("x"));
        assertEquals(1, msg.getuID());
    }

    @Test
    void testConstructor4_responseWithPayload() {
        Map<String, Object> data = new HashMap<>();
        data.put("result", "OK");

        Message msg = new Message("RESPONSE", "success", "sess45", data);

        assertEquals("RESPONSE", msg.getType());
        assertEquals("success", msg.getStatus());
        assertEquals("sess45", msg.getSessionID());
        assertEquals("OK", msg.getData("result"));
        assertEquals(1, msg.getuID());
    }

    @Test
    void testSettersAndGetters() {
        Message msg = new Message("TEST");

        msg.setStatus("success");
        msg.setText("Hello world");
        msg.setSender("Alice");
        msg.setSessionID("ABC123");

        assertEquals("success", msg.getStatus());
        assertEquals("Hello world", msg.getText());
        assertEquals("Alice", msg.getSender());
        assertEquals("ABC123", msg.getSessionID());
    }

    @Test
    void testPayloadOperations() {
        Message msg = new Message("PAYLOAD");

        msg.addData("key", 42);
        assertTrue(msg.hasData("key"));
        assertEquals(42, msg.getData("key"));

        msg.removeData("key");
        assertFalse(msg.hasData("key"));
    }

    @Test
    void testSetPayloadReplacesMap() {
        Message msg = new Message("SET");

        Map<String, Object> data = new HashMap<>();
        data.put("A", 1);

        msg.setPayload(data);
        assertEquals(1, msg.getData("A"));
    }

    @Test
    void testCopyMethod() {
        Message msg = new Message("COPY");
        msg.setStatus("OK");
        msg.setText("Hello");
        msg.setSender("John");
        msg.setSessionID("SID123");
        msg.addData("n", 5);

        Message cp = msg.copy();

        // Ensure fields copied
        assertEquals(msg.getType(), cp.getType());
        assertEquals(msg.getStatus(), cp.getStatus());
        assertEquals(msg.getText(), cp.getText());
        assertEquals(msg.getSender(), cp.getSender());
        assertEquals(msg.getSessionID(), cp.getSessionID());
        assertEquals(msg.getPayload(), cp.getPayload());

        // Ensure deep copy of payload
        cp.addData("extra", 999);
        assertFalse(msg.hasData("extra"));

        // Ensure different uID
        assertNotEquals(msg.getuID(), cp.getuID());
    }
}
