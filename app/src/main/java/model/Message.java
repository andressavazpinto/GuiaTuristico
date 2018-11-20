package model;

import com.google.firebase.database.Exclude;

import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Message {
    private int idMessage, idUser;
    private String content, translation;
    private long dateTime;
    private String type;

    public Message() {
    }

    public Message(int idMessage, int idUser, String content, String translation, String type) {
        this.idUser = idUser;
        this.idMessage = idMessage;
        this.content = content;
        this.translation = translation;
        this.type = type;
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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
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

    public void setDateTime() {
        final String TIME_ZONE = "America/Sao_Paulo";
        TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE));
        DateTimeZone.setDefault(DateTimeZone.forID(TIME_ZONE));
        dateTime = new Date().getTime();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap();
        result.put("idMessage", idMessage);
        result.put("idUser", idUser);
        result.put("content", content);
        result.put("translation", translation);
        result.put("dateTime", dateTime);
        result.put("type", type);

        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idMessage=" + idMessage +
                ", idUser=" + idUser +
                ", content='" + content + '\'' +
                ", translation='" + translation + '\'' +
                ", dateTime=" + dateTime +
                ", type='" + type + '\'' +
                '}';
    }
}
