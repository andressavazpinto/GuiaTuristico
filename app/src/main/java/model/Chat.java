package model;

import util.StatusChat;

public class Chat {
    int idUser1, idUser2;
    int idChat;
    StatusChat status;

    public Chat(int idUser1, int idUser2, int idChat, StatusChat status) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.idChat = idChat;
        this.status = status;
    }

    public Chat() {
    }

    public int getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(int idUser1) {
        this.idUser1 = idUser1;
    }

    public int getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(int idUser2) {
        this.idUser2 = idUser2;
    }

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public StatusChat getStatus() {
        return status;
    }

    public void setStatus(StatusChat status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "idUser1=" + idUser1 +
                ", idUser2=" + idUser2 +
                ", idChat=" + idChat +
                ", status=" + status +
                '}';
    }
}
