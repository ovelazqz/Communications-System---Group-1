package Server;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//import enumerates.STATUS;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final STATUS status;                    // SUCCESS | ERROR | UNAUTHORIZED
    private final Map<String, Object> payload;      // arbitrary response data

    //Create a response with a status and an optional message.
    //The message (if non-null) is stored under the "message" key in the payload.
    
    public Response(STATUS status, String message) {
        if (status == null) throw new IllegalArgumentException("status cannot be null");
        this.status = status;
        this.payload = new HashMap<>();
        if (message != null) {
            this.payload.put("message", message);
        }
    }

    //Add an arbitrary key/value to the payload.
    public void addData(String key, Object value) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key must not be null or empty");
        }
        payload.put(key, value);
    }

    //Retrieve a value from the payload by key.
    public Object getData(String key) {
        return payload.get(key);
    }

    // Unmodifiable view of the full payload.
    public Map<String, Object> getPayload() {
        return Collections.unmodifiableMap(payload);
    }

    public STATUS getStatus() {
        return status;
    }

    //Override
    public String toString() {
        return "Response{status=" + status + ", payload=" + payload + "}";
    }
    
}
