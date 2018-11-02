package to;

import util.StatusChat;

public class ChatConnectTO {
    private int idChat;
    private int idConnectGuides;
    private int idUser1;
    private int idUser2;
    private StatusChat statusChat;

    public ChatConnectTO(int idChat, int idConnectGuides, int idUser1, int idUser2, StatusChat statusChat) {
        this.idChat = idChat;
        this.idConnectGuides = idConnectGuides;
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.statusChat = statusChat;
    }

    public ChatConnectTO() {
    }

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public int getIdConnectGuides() {
        return idConnectGuides;
    }

    public void setIdConnectGuides(int idConnectGuides) {
        this.idConnectGuides = idConnectGuides;
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

    public StatusChat getStatusChat() {
        return statusChat;
    }

    public void setStatusChat(StatusChat statusChat) {
        this.statusChat = statusChat;
    }

    @Override
    public String toString() {
        return "ChatConnectTO{" +
                "idChat=" + idChat +
                ", idConnectGuides=" + idConnectGuides +
                ", idUser1=" + idUser1 +
                ", idUser2=" + idUser2 +
                ", statusChat=" + statusChat +
                '}';
    }
}

