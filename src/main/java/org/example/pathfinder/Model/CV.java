package org.example.pathfinder.Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CV {
    private int idCV;
    private int userId; // Foreign key to User
    private String title;
    private String introduction;
    private String skills; // Replaces languages
    private Timestamp dateCreation; // date_creation column
    private List<Experience> experiences; // List of associated experiences
    private List<Language> languageList; // List of associated languages
    private List<Certificate> certificates; // List of associated certificates

    // Default constructor
    public CV() {
        this.experiences = new ArrayList<>(); // Initialize the experiences list
        this.languageList = new ArrayList<>(); // Initialize the languages list
        this.certificates = new ArrayList<>(); // Initialize the certificates list
    }

    // Constructor with all fields
    public CV(int idCV, int userId, String title, String introduction, String skills, Timestamp dateCreation) {
        this.idCV = idCV;
        this.userId = userId;
        this.title = title;
        this.introduction = introduction;
        this.skills = skills;
        this.dateCreation = dateCreation;
        this.experiences = new ArrayList<>(); // Initialize the experiences list
        this.languageList = new ArrayList<>(); // Initialize the languages list
        this.certificates = new ArrayList<>(); // Initialize the certificates list
    }

    // Constructor for creating a new CV (without ID or dateCreation)
    public CV(int userId, String title, String introduction, String skills) {
        this.userId = userId;
        this.title = title;
        this.introduction = introduction;
        this.skills = skills;
        this.dateCreation = new Timestamp(System.currentTimeMillis()); // Default to current timestamp
        this.experiences = new ArrayList<>(); // Initialize the experiences list
        this.languageList = new ArrayList<>(); // Initialize the languages list
        this.certificates = new ArrayList<>(); // Initialize the certificates list
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

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
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

    public List<Language> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

    // Add a single language to the CV
    public void addLanguage(Language language) {
        this.languageList.add(language);
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    // Add a single certificate to the CV
    public void addCertificate(Certificate certificate) {
        this.certificates.add(certificate);
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
                Objects.equals(skills, cv.skills) &&
                Objects.equals(dateCreation, cv.dateCreation) &&
                Objects.equals(experiences, cv.experiences) &&
                Objects.equals(languageList, cv.languageList) &&
                Objects.equals(certificates, cv.certificates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCV, userId, title, introduction, skills, dateCreation, experiences, languageList, certificates);
    }

    // toString
    @Override
    public String toString() {
        return "CV{" +
                "idCV=" + idCV +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", introduction='" + introduction + '\'' +
                ", skills='" + skills + '\'' +
                ", dateCreation=" + dateCreation +
                ", experiences=" + experiences +
                ", languageList=" + languageList +
                ", certificates=" + certificates +
                '}';
    }
}
