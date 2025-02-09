package org.example.pathfinder.Model;

import java.sql.Date; // Change to use java.sql.Date
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceOffre {
    private int id_service;
    private int id_user;
    private String title;
    private String description;
    private Date date_posted;  // Change to java.sql.Date
    private String field;
    private double price;
    private String required_experience;
    private String required_education;
    private String skills;
    private List<ApplicationService> applications;

    public ServiceOffre() {
        this.applications = new ArrayList<>();
    }

    public ServiceOffre(int id_user, int id_service, String title, String description, Date date_posted, String field,
                        double price, String required_experience, String required_education, String skills) {
        this.id_user = id_user;
        this.id_service = id_service;
        this.title = title;
        this.description = description;
        this.date_posted = date_posted;
        this.field = field;
        this.price = price;
        this.required_experience = required_experience;
        this.required_education = required_education;
        this.skills = skills;
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

    public Date getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(Date date_posted) {
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

    public String getRequired_experience() {
        return required_experience;
    }

    public void setRequired_experience(String required_experience) {
        this.required_experience = required_experience;
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

    public List<ApplicationService> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationService> applications) {
        this.applications = applications;
    }

    public void addApplication(ApplicationService application) {
        this.applications.add(application);
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
                Objects.equals(required_experience, that.required_experience) &&
                Objects.equals(required_education, that.required_education) &&
                Objects.equals(skills, that.skills) &&
                Objects.equals(applications, that.applications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_service, title, description, date_posted, field, price, required_experience, required_education, skills, applications);
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
                ", required_experience='" + required_experience + '\'' +
                ", required_education='" + required_education + '\'' +
                ", skills='" + skills + '\'' +
                ", applications=" + applications +
                '}';
    }
}
