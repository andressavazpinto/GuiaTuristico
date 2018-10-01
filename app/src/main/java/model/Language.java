package model;

public class Language {
    private String language, name;

    public Language() {
    }

    public Language(String language, String name) {
        this.language = language;
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Language{" +
                "language='" + language + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
