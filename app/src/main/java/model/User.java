package model;

import java.io.Serializable;

import util.Status;

/**
 * Created by Andressa on 13/05/2018.
 */

public class User implements Serializable {

    private int idUser;
    private String name;
    private String dateOfBirth;
    private String language;
    private String occupation;
    private String email;
    private String password;
    private int idLocalization;
    Status statusAccount;

    public User() {

    }

    public User(int idUser) {
        this.idUser = idUser;
    }

    public User(int idUser, String name, String dateOfBirth, String language, String occupation,
                String email, String password, int idLocalization, Status statusAccount) {
        this.idUser = idUser;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.language = language;
        this.occupation = occupation;
        this.email = email;
        this.password = password;
        this.idLocalization = idLocalization;
        this.statusAccount = statusAccount;
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
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

    public Status getStatusAccount() {
        return statusAccount;
    }

    public void setStatusAccount(Status statusAccount) {
        this.statusAccount = statusAccount;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", name='" + name + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", language='" + language + '\'' +
                ", occupation='" + occupation + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idLocalization=" + idLocalization +
                ", statusAccount=" + statusAccount +
                '}';
    }
}

