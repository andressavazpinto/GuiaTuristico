package model;

import util.StatusConnectGuides;

public class ConnectGuides {
    private int idConnectGuides, idUser1, idUser2;
    private StatusConnectGuides status;

    public ConnectGuides() {
    }

    public ConnectGuides(int idConnectGuides, int idUser1, int idUser2, StatusConnectGuides status) {
        this.idConnectGuides = idConnectGuides;
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.status = status;
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

    public StatusConnectGuides getStatus() {
        return status;
    }

    public void setStatus(StatusConnectGuides status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ConnectGuides{" +
                "idConnectGuides=" + idConnectGuides +
                ", idUser1=" + idUser1 +
                ", idUser2=" + idUser2 +
                ", status=" + status +
                '}';
    }
}


