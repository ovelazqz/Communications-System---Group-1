package Server;

import java.io.Serializable;

public class Message implements Serializable{
    private static int count = 0;
    private int uID;
    protected String type;
    protected String status;
    protected String text;

    public Message() {
        this.type = "";
        this.status = "";
        this.text = "";
    }

    public Message(String type, String status, String text) {
        this.uID = ++count;
        setType(type);
        setStatus(status);
        setText(text);

    }

    public String getId() {
      return uID;
      
    }

    private void setType(String type) {
        this.type = type;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
    public String getText() {
        return text;
    }




} 
