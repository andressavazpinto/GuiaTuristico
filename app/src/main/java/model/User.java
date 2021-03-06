package model;

import java.text.DecimalFormat;

import util.StatusUser;

/**
 * Created by Andressa on 13/05/2018.
 */

public class User {

    private int idUser;
    private String name;
    private String dateOfBirth;
    private String language;
    private String email;
    private String password;
    private int idLocalization;
    private StatusUser statusAccount;
    private double score;

    public User() {
    }

    public User(int idUser) {
        this.idUser = idUser;
    }

    public User(int idUser, String name, String dateOfBirth, String language,
                String email, String password, int idLocalization, StatusUser statusAccount, double score) {
        this.idUser = idUser;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.language = language;
        this.email = email;
        this.password = password;
        this.idLocalization = idLocalization;
        this.statusAccount = statusAccount;
        this.score = score;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdLocalization() {
        return idLocalization;
    }

    public void setIdLocalization(int idLocalization) {
        this.idLocalization = idLocalization;
    }

    public StatusUser getStatusAccount() {
        return statusAccount;
    }

    public void setStatusAccount(StatusUser statusAccount) {
        this.statusAccount = statusAccount;
    }

    public double getScore() {
        return score;
    }

    public String getScoreS() {
        DecimalFormat two = new DecimalFormat("0.00");
        return two.format(getScore());
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", name='" + name + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", language='" + language + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idLocalization=" + idLocalization +
                ", statusAccount=" + statusAccount +
                ", score=" + score +
                '}';
    }
}

