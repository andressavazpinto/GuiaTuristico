package model;

public class Message {
    private int idMessage;
    private String content, dataHora;

    public Message() {
    }

    public Message(int idMessage, String content, String dataHora) {
        this.idMessage = idMessage;
        this.content = content;
        this.dataHora = dataHora;
    }

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }
}
