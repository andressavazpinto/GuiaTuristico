package model;

import util.StatusSearch;

public class SearchByRegion {
    private String city, country;
    private StatusSearch statusSearch;
    private int idUser;

    public SearchByRegion() {
    }

    public SearchByRegion(String city, String country, StatusSearch statusSearch, int idUser) {
        this.city = city;
        this.country = country;
        this.statusSearch = statusSearch;
        this.idUser = idUser;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public StatusSearch getStatusSearch() {
        return statusSearch;
    }

    public void setStatusSearch(StatusSearch statusSearch) {
        this.statusSearch = statusSearch;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "SearchByRegion{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", statusSearch=" + statusSearch +
                ", idUser=" + idUser +
                '}';
    }
}
