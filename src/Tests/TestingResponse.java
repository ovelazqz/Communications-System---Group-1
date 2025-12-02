package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Server.Response;
import Common.STATUS;

class TestingResponse {

    @Test
    void constructor_storesStatusAndMessage() {
        Response r = new Response(STATUS.SUCCESS, "OK");

        assertEquals(STATUS.SUCCESS, r.getStatus());
        assertEquals("OK", r.getData("message"));
    }

    @Test
    void constructor_allowsNullMessage_noMessageKeyStored() {
        Response r = new Response(STATUS.ERROR, null);

        assertNull(r.getData("message"));
        // payload map should not contain "message" key at all
        assertFalse(r.getPayload().containsKey("message"));
    }

    @Test
    void constructor_nullStatus_throwsException() {
        assertThrows(IllegalArgumentException.class,
                     () -> new Response(null, "msg"));
    }

    @Test
    void addData_storesAndRetrievesValue() {
        Response r = new Response(STATUS.SUCCESS, "base");

        r.addData("key1", 123);
        r.addData("key2", "value");

        assertEquals(123, r.getData("key1"));
        assertEquals("value", r.getData("key2"));
    }

    @Test
    void addData_nullOrEmptyKey_throwsException() {
        Response r = new Response(STATUS.SUCCESS, "test");

        assertThrows(IllegalArgumentException.class,
                     () -> r.addData(null, "x"));
        assertThrows(IllegalArgumentException.class,
                     () -> r.addData("", "x"));
    }

    @Test
    void getPayload_isUnmodifiable() {
        Response r = new Response(STATUS.SUCCESS, "test");
        r.addData("k", "v");

        var payload = r.getPayload();
        assertThrows(UnsupportedOperationException.class,
                     () -> payload.put("another", "value"));
    }

    @Test
    void toString_containsStatusAndPayload() {
        Response r = new Response(STATUS.SUCCESS, "hello");
        r.addData("k", "v");

        String s = r.toString();

        assertTrue(s.contains("SUCCESS"));
        assertTrue(s.contains("payload"));
        assertTrue(s.contains("message"));
    }
}
