package model;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private int idMessage, idUser;
    private String content;
    private long dateTime;
    private String type;

    public Message() {
    }

    public Message(int idMessage, int idUser, String content, String type) {
        this.idUser = idUser;
        this.idMessage = idMessage;
        this.content = content;
        this.type = type;
        dateTime = new Date().getTime();
    }

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public int getIdUser() { return idUser; }

    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap();
        result.put("idMessage", idMessage);
        result.put("idUser", idUser);
        result.put("content", content);
        result.put("dateTime", dateTime);
        result.put("type", type);

        return result;
    }
}
