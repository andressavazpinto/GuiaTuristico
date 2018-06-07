package model;

/**
 * Created by Andressa on 31/05/2018.
 */

public class Interest {
    private int idInterest;
    private String name;

    public Interest() {
    }

    public Interest(int idInterest, String name) {
        this.idInterest = idInterest;
        this.name = name;
    }

    public int getIdInterest() {
        return idInterest;
    }

    public void setIdInterest(int idInterest) {
        this.idInterest = idInterest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Interest{" +
                "idInterest=" + idInterest +
                ", name='" + name + '\'' +
                '}';
    }
}
