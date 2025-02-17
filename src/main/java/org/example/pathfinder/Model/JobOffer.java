package org.example.pathfinder.Model;

import java.sql.Timestamp;
import java.util.Objects;

public class JobOffer {
    private Long id_Offer; /////////////////////why big int
    private Long id_User;
    private String title;
    private String description;
    private Timestamp datePosted;
    private String type;
    private Integer numberOfSpots;
    private String requiredEducation;
    private String requiredExperience;
    private String skills;

    // Constructor
    public JobOffer(Long idUser, String title, String description, String type, Integer numberOfSpots, String requiredEducation, String requiredExperience, String skills) {
        this.id_User = idUser;
        this.title = title;
        this.description = description;
        this.datePosted = new Timestamp(System.currentTimeMillis()); // default current timestamp
        this.type = type;
        this.numberOfSpots = numberOfSpots;
        this.requiredEducation = requiredEducation;
        this.requiredExperience = requiredExperience;
        this.skills = skills;
    }

    public JobOffer() {

    }

    // Getters and setters
    public Long getIdOffer() {
        return id_Offer;
    }

    public void setIdOffer(Long idOffer) {
        this.id_Offer = idOffer;
    }

    public Long getIdUser() {
        return id_User;
    }

    public void setIdUser(Long idUser) {
        this.id_User = idUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Timestamp datePosted) {
        this.datePosted = datePosted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNumberOfSpots() {
        return numberOfSpots;
    }

    public void setNumberOfSpots(Integer numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
    }

    public String getRequiredEducation() {
        return requiredEducation;
    }

    public void setRequiredEducation(String requiredEducation) {
        this.requiredEducation = requiredEducation;
    }

    public String getRequiredExperience() {
        return requiredExperience;
    }

    public void setRequiredExperience(String requiredExperience) {
        this.requiredExperience = requiredExperience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills( String skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "JobOffer{" +
                "idOffer=" + id_Offer +
                ", idUser=" + id_User +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", datePosted=" + datePosted +
                ", type='" + type + '\'' +
                ", numberOfSpots=" + numberOfSpots +
                ", requiredEducation='" + requiredEducation + '\'' +
                ", requiredExperience='" + requiredExperience + '\'' +
                ", skills=" + skills +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobOffer jobOffer = (JobOffer) o;
        return Objects.equals(id_Offer, jobOffer.id_Offer) &&
                Objects.equals(id_User, jobOffer.id_User) &&
                Objects.equals(title, jobOffer.title) &&
                Objects.equals(description, jobOffer.description) &&
                Objects.equals(datePosted, jobOffer.datePosted) &&
                Objects.equals(type, jobOffer.type) &&
                Objects.equals(numberOfSpots, jobOffer.numberOfSpots) &&
                Objects.equals(requiredEducation, jobOffer.requiredEducation) &&
                Objects.equals(requiredExperience, jobOffer.requiredExperience) &&
                Objects.equals(skills, jobOffer.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_Offer, id_User, title, description, datePosted, type, numberOfSpots, requiredEducation, requiredExperience, skills);
    }
}
