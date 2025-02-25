package org.example.pathfinder.Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CV {
    private int idCV;
    private int userId; // id_user foreign key
    private String title;
    private String introduction;
    private String languages;
    private Timestamp dateCreation; // date_creation column
    private List<Experience> experiences; // List of associated experiences

    // Default constructor
    public CV() {
        this.experiences = new ArrayList<>(); // Initialize the experiences list
    }

    // Constructor with all fields
    public CV(int idCV, int userId, String title, String introduction, String languages, Timestamp dateCreation) {
        this.idCV = idCV;
        this.userId = userId;
        this.title = title;
        this.introduction = introduction;
        this.languages = languages;
        this.dateCreation = dateCreation;
        this.experiences = new ArrayList<>(); // Initialize the experiences list
    }

    // Constructor for creating a new CV (without ID or dateCreation)
    public CV(int userId, String title, String introduction, String languages) {
        this.userId = userId;
        this.title = title;
        this.introduction = introduction;
        this.languages = languages;
        this.dateCreation = new Timestamp(System.currentTimeMillis()); // Default to current timestamp
        this.experiences = new ArrayList<>(); // Initialize the experiences list
    }

    // Getters and Setters
    public int getIdCV() {
        return idCV;
    }

    public void setIdCV(int idCV) {
        this.idCV = idCV;
    }

    public int getUserId() {
        
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    // Add a single experience to the CV
    public void addExperience(Experience experience) {
        this.experiences.add(experience);
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CV cv)) return false;
        return idCV == cv.idCV &&
                userId == cv.userId &&
                Objects.equals(title, cv.title) &&
                Objects.equals(introduction, cv.introduction) &&
                Objects.equals(languages, cv.languages) &&
                Objects.equals(dateCreation, cv.dateCreation) &&
                Objects.equals(experiences, cv.experiences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCV, userId, title, introduction, languages, dateCreation, experiences);
    }

    // toString
    @Override
    public String toString() {
        return "CV{" +
                "idCV=" + idCV +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", introduction='" + introduction + '\'' +
                ", languages='" + languages + '\'' +
                ", dateCreation=" + dateCreation +
                ", experiences=" + experiences +
                '}';
    }
}
