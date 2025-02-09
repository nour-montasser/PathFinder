package org.example.pathfinder.Model;

import java.sql.Date;
import java.util.Objects;

public class ApplicationJob {
    private Long id_Application;
    private Long id_JobOffer;
    private Long id_User;
    private Date dateApplication;
    private String status;

    // Constructor
    public ApplicationJob(Long id_JobOffer, Long id_User, Date dateApplication, String status) {
        this.id_JobOffer = id_JobOffer;
        this.id_User = id_User;
        this.dateApplication = dateApplication;
        this.status = status;
    }

    // Getters and setters
    public Long getApplicationId() {
        return id_Application;
    }

    public void setApplicationId(Long id_Application) {
        this.id_Application = id_Application;
    }

    public Long getJobOfferId() {
        return id_JobOffer;
    }

    public void setJobOfferId(Long id_JobOffer) {
        this.id_JobOffer = id_JobOffer;
    }

    public Long getIdUser() {
        return id_User;
    }

    public void setIdUser(Long id_User) {
        this.id_User = id_User;
    }

    public Date getDateApplication() {
        return dateApplication;
    }

    public void setDateApplication(Date dateApplication) {
        this.dateApplication = dateApplication;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ApplicationJob{" +
                "id_Application=" + id_Application +
                ", id_JobOffer=" + id_JobOffer +
                ", id_User=" + id_User +
                ", dateApplication=" + dateApplication +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationJob that = (ApplicationJob) o;
        return Objects.equals(id_Application, that.id_Application) &&
                Objects.equals(id_JobOffer, that.id_JobOffer) &&
                Objects.equals(id_User, that.id_User) &&
                Objects.equals(dateApplication, that.dateApplication) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_Application, id_JobOffer, id_User, dateApplication, status);
    }
}
