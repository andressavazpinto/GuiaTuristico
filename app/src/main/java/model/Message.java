package model;

public class Message {
    private int idMessage, idUser;
    private String content, dataHora;

    public Message() {
    }

    public Message(int idMessage, int idUser, String content, String dataHora) {
        this.idUser = idUser;
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

    public int getIdUser() { return idUser; }

    public void setIdUser(int idUser) { this.idUser = idUser; }

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
