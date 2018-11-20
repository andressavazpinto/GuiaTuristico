package model;

public class Localization {
    private int idLocalization;
    private double latitude, longitude;
    private String city, area, country;

    public Localization() {
    }

    public Localization(int idLocalization, double latitude, double longitude, String city, String area, String country) {
        this.idLocalization = idLocalization;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.area = area;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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
                ", area='" + area + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
