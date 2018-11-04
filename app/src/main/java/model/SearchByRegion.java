package model;

import util.StatusSearch;

public class SearchByRegion {
    private String city, country, name;
    private StatusSearch statusSearch;
    private int idUser;

    public SearchByRegion() {
    }

    public SearchByRegion(String city, String country, String name, StatusSearch statusSearch, int idUser) {
        this.city = city;
        this.country = country;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                ", name='" + name + '\'' +
                ", statusSearch=" + statusSearch +
                ", idUser=" + idUser +
                '}';
    }
}
