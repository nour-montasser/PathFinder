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
    private Timestamp lastViewed;
    private String formattedExperiences;
    private String formattedLanguages;
    private String formattedCertificates;
    private String username;
    private String userTitle; // New field for user-defined title
    private boolean favorite=false; // New field for favorite CV pinning

    public String getUsername() {
        return username;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Default constructor
    public CV() {
        this.experiences = new ArrayList<>(); // Initialize the experiences list
        this.languageList = new ArrayList<>(); // Initialize the languages list
        this.certificates = new ArrayList<>(); // Initialize the certificates list
    }

    // Constructor with all fields
    public CV(int idCV, int userId, String title, String introduction, String skills, Timestamp dateCreation, Timestamp lastViewed) {
        this.idCV = idCV;
        this.userId = userId;
        this.userTitle= title;
        this.title = title;
        this.introduction = introduction;
        this.skills = skills;
        this.dateCreation = dateCreation;
        this.lastViewed = lastViewed;
        this.experiences = new ArrayList<>();
        this.languageList = new ArrayList<>();
        this.certificates = new ArrayList<>();
    }
    // ✅ Add this constructor to CV.java
    public CV(int idCV, String userName, String title, String introduction, String skills, Timestamp dateCreation) {
        this.idCV = idCV;
        this.userTitle=title;
        this.title = title;
        this.username=userName;
        this.introduction = introduction;
        this.skills = skills;
        this.dateCreation = dateCreation;
    }
// ✅ Add these methods in CV.java



    // 🔹 Getter & Setter for Experiences
    public String getFormattedExperiences() {
        return formattedExperiences;
    }
    public void setFormattedExperiences(String formattedExperiences) {
        this.formattedExperiences = formattedExperiences;
    }

    // 🔹 Getter & Setter for Languages
    public String getFormattedLanguages() {
        return formattedLanguages;
    }
    public void setFormattedLanguages(String formattedLanguages) {
        this.formattedLanguages = formattedLanguages;
    }

    // 🔹 Getter & Setter for Certificates
    public String getFormattedCertificates() {
        return formattedCertificates;
    }
    public void setFormattedCertificates(String formattedCertificates) {
        this.formattedCertificates = formattedCertificates;
    }


    public CV(CV original) {
        this.idCV = 0; // Reset ID to ensure it's treated as a new entry
        this.userId = original.userId;
        this.userTitle=original.userTitle;
        this.title = original.title + " (Copy)"; // Append "(Copy)" to differentiate
        this.introduction = original.introduction;
        this.skills = original.skills;
        this.dateCreation = new Timestamp(System.currentTimeMillis()); // Set new creation date

        // Deep Copy Lists to avoid reference issues
        this.experiences = new ArrayList<>();
        for (Experience exp : original.experiences) {
            this.experiences.add(new Experience(exp)); // Assuming Experience has a copy constructor
        }

        this.languageList = new ArrayList<>();
        for (Language lang : original.languageList) {
            this.languageList.add(new Language(lang)); // Assuming Language has a copy constructor
        }

        this.certificates = new ArrayList<>();
        for (Certificate cert : original.certificates) {
            this.certificates.add(new Certificate(cert)); // Assuming Certificate has a copy constructor
        }
    }



    // Constructor for creating a new CV (without ID or dateCreation)
    public CV(int userId, String title, String introduction, String skills) {
        this.userId = userId;
        this.title = title;
        this.userTitle = title;
        this.introduction = introduction;
        this.skills = skills;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
        // Generate unique file name from introduction
        this.lastViewed = new Timestamp(System.currentTimeMillis());
        this.experiences = new ArrayList<>();
        this.languageList = new ArrayList<>();
        this.certificates = new ArrayList<>();
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


    public Timestamp getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(Timestamp lastViewed) {
        this.lastViewed = lastViewed;
    }


    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CV cv)) return false;
        return idCV == cv.idCV &&
                userId == cv.userId &&
                Objects.equals(title, cv.title) &&
                Objects.equals(userTitle, cv.userTitle) &&
                Objects.equals(introduction, cv.introduction) &&
                Objects.equals(skills, cv.skills) &&
                Objects.equals(dateCreation, cv.dateCreation) &&
                Objects.equals(experiences, cv.experiences) &&
                Objects.equals(languageList, cv.languageList) &&
                Objects.equals(certificates, cv.certificates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCV, userId,userTitle, title, introduction, skills, dateCreation, experiences, languageList, certificates);
    }

    // toString
    @Override
    public String toString() {
        return "CV{" +
                "idCV=" + idCV +
                ", userId=" + userId +
                ", UserTitle='" + userTitle + '\'' +
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
