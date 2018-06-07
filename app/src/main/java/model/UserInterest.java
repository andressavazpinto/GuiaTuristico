package model;

/**
 * Created by Andressa on 31/05/2018.
 */

public class UserInterest {
    private int idUserInterest;
    private int idUser;
    private int idInterest;

    public UserInterest() {
    }

    public UserInterest(int idUserInterest, int idUser, int idInterest) {
        this.idUserInterest = idUserInterest;
        this.idUser = idUser;
        this.idInterest = idInterest;
    }

    public int getIdUserInterest() {
        return idUserInterest;
    }

    public void setIdUserInterest(int idUserInterest) {
        this.idUserInterest = idUserInterest;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdInterest() {
        return idInterest;
    }

    public void setIdInterest(int idInterest) {
        this.idInterest = idInterest;
    }

    @Override
    public String toString() {
        return "UserInterest{" + "idUserInterest=" + idUserInterest + ", idUser=" + idUser + ", idInterest=" + idInterest + '}';
    }
}
