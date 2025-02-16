package org.example.pathfinder.Model;

import java.util.Objects;

public class Experience {
    private int idExperience;
    private int idCv; // Foreign key to CV table
    private String type;
    private String position;
    private String locationName;
    private String startDate; // Keeping as String for simplicity (use Date or LocalDate for stricter typing)
    private String endDate;
    private String description; // Newly added column for experience details

    // Constructors
    public Experience() {}

    public Experience(int idExperience, int idCv, String type, String position, String locationName, String startDate, String endDate, String description) {
        this.idExperience = idExperience;
        this.idCv = idCv;
        this.type = type;
        this.position = position;
        this.locationName = locationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }
    public Experience(Experience original) {
        this.idExperience = 0; // Reset ID to ensure it's treated as a new entry
        this.idCv = original.idCv; // Will be reassigned when copying the CV
        this.type = original.type;
        this.position = original.position;
        this.locationName = original.locationName;
        this.startDate = original.startDate;
        this.endDate = original.endDate;
        this.description = original.description;
    }


    public Experience(int idCv, String type, String position, String locationName, String startDate, String endDate, String description) {
        this.idCv = idCv;
        this.type = type;
        this.position = position;
        this.locationName = locationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    // Getters and Setters
    public int getIdExperience() {
        return idExperience;
    }

    public void setIdExperience(int idExperience) {
        this.idExperience = idExperience;
    }

    public int getIdCv() {
        return idCv;
    }

    public void setIdCv(int idCv) {
        this.idCv = idCv;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Experience experience)) return false;
        return idExperience == experience.idExperience &&
                idCv == experience.idCv &&
                Objects.equals(type, experience.type) &&
                Objects.equals(position, experience.position) &&
                Objects.equals(locationName, experience.locationName) &&
                Objects.equals(startDate, experience.startDate) &&
                Objects.equals(endDate, experience.endDate) &&
                Objects.equals(description, experience.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExperience, idCv, type, position, locationName, startDate, endDate, description);
    }

    // toString
    @Override
    public String toString() {
        return "Experience{" +
                "idExperience=" + idExperience +
                ", idCv=" + idCv +
                ", type='" + type + '\'' +
                ", position='" + position + '\'' +
                ", locationName='" + locationName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
