package org.example.pathfinder.Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceOffre {
    private int id_service;
    private int id_user;
    private String title;
    private String description;
    private LocalDateTime date_posted;  // ✅ Stores timestamp in LocalDateTime
    private String field;
    private double price;

    private String required_education;
    private String skills;
    private String experience_level;  // ✅ New field for experience level
    private String duration;
    private String status;


    private List<ApplicationService> applications;

    public ServiceOffre() {
        this.applications = new ArrayList<>();
        this.date_posted = LocalDateTime.now();
        this.status = "Open";
    }

    public ServiceOffre(int id_user, int id_service, String title, String description, String field,
                        double price, String required_education, String skills, String experience_level, String duration,String status) {
        this.id_user = id_user;
        this.id_service = id_service;
        this.title = title;
        this.description = description;
        this.date_posted = LocalDateTime.now();  // ✅ Automatically sets the date
        this.field = field;
        this.price = price;

        this.required_education = required_education;
        this.skills = skills;
        this.experience_level = experience_level;

        this.duration = duration;
        this.status = status;
        this.applications = new ArrayList<>();
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_service() {
        return id_service;
    }

    public void setId_service(int id_service) {
        this.id_service = id_service;
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

    public LocalDateTime getDate_posted() {
        return date_posted;
    }

    // ✅ Converts LocalDateTime to Timestamp for SQL storage
    public Timestamp getDatePostedAsTimestamp() {
        return Timestamp.valueOf(date_posted);
    }

    public void setDate_posted(LocalDateTime date_posted) {
        this.date_posted = date_posted;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



    public String getRequired_education() {
        return required_education;
    }

    public void setRequired_education(String required_education) {
        this.required_education = required_education;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience_level() {
        return experience_level;
    }

    public void setExperience_level(String experience_level) {
        this.experience_level = experience_level;
    }

    public List<ApplicationService> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationService> applications) {
        this.applications = applications;
    }

    public void addApplication(ApplicationService application) {
        this.applications.add(application);
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceOffre that)) return false;
        return id_service == that.id_service &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(date_posted, that.date_posted) &&
                Objects.equals(field, that.field) &&
                Objects.equals(required_education, that.required_education) &&
                Objects.equals(skills, that.skills) &&
                Objects.equals(experience_level, that.experience_level) &&
                Objects.equals(status, that.status) &&

                Objects.equals(applications, that.applications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_service, title, description, date_posted, field, price,  required_education, skills, experience_level, status,applications);
    }

    @Override
    public String toString() {
        return "ServiceOffre{" +
                "id_service=" + id_service +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_posted=" + date_posted +
                ", field='" + field + '\'' +
                ", price=" + price +

                ", required_education='" + required_education + '\'' +
                ", skills='" + skills + '\'' +
                ", experience_level='" + experience_level + '\'' +  // ✅ New field added
                ", applications=" + applications +
                '}';
    }

    // ✅ Format date for UI display
    public String getFormattedDate() {
        if (date_posted == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return date_posted.format(formatter);
    }
}
