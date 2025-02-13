package org.example.pathfinder.Model;

public class Language {
    private int idLanguage;
    private int cvId; // Foreign key to CV
    private String name;
    private String level;

    // Default constructor
    public Language() {}

    // Constructor with all fields
    public Language(int idLanguage, int cvId, String name, String level) {
        this.idLanguage = idLanguage;
        this.cvId = cvId;
        this.name = name;
        this.level = level;
    }
    public Language( int cvId, String name, String level) {

        this.cvId = cvId;
        this.name = name;
        this.level = level;
    }

    // Getters and Setters
    public int getIdLanguage() {
        return idLanguage;
    }

    public void setIdLanguage(int idLanguage) {
        this.idLanguage = idLanguage;
    }

    public int getCvId() {
        return cvId;
    }

    public void setCvId(int cvId) {
        this.cvId = cvId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Language{" +
                "idLanguage=" + idLanguage +
                ", cvId=" + cvId +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
