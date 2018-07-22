package model;

import java.io.Serializable;

public class Localization implements Serializable {
    public int idLocalization;
    public double latitude, longitude;
    public String city, uf, country;

    public Localization() {
    }

    public Localization(int idLocalization, double latitude, double longitude, String city, String uf, String country) {
        this.idLocalization = idLocalization;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.uf = uf;
        this.country = country;
    }

    public int getIdLocalization() {
        return idLocalization;
    }

    public void setIdLocalization(int idLocalization) {
        this.idLocalization = idLocalization;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Localization{" +
                "idLocalization=" + idLocalization +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", city='" + city + '\'' +
                ", uf='" + uf + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
