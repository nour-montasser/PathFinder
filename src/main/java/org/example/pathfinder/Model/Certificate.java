package org.example.pathfinder.Model;

import java.sql.Date;
import java.util.Objects;

public class Certificate {
    private int idCertificate;
    private int idCv; // Foreign key to CV
    private String title;
    private String description; // New description field
    private String media;
    private String association; // Organization that issued the certificate
    private Date date; // Date of issuance

    // Default constructor
    public Certificate() {}

    // Constructor with all fields
    public Certificate(int idCertificate, int idCv, String title, String description, String media, String association, Date date) {
        this.idCertificate = idCertificate;
        this.idCv = idCv;
        this.title = title;
        this.description = description;
        this.media = media;
        this.association = association;
        this.date = date;
    }
    public Certificate(Certificate original) {
        this.idCertificate = 0; // Reset ID to ensure it's treated as a new entry
        this.idCv = original.idCv; // Will be reassigned when copying the CV
        this.title = original.title;
        this.description = original.description;
        this.media = original.media;
        this.association = original.association;
        this.date = new Date(original.date.getTime()); // Deep copy of the Date object
    }


    // Constructor without ID (For inserting new records)
    public Certificate(int idCv, String title, String description, String media, String association, Date date) {
        this.idCv = idCv;
        this.title = title;
        this.description = description;
        this.media = media;
        this.association = association;
        this.date = date;
    }

    // Getters and Setters
    public int getIdCertificate() {
        return idCertificate;
    }

    public void setIdCertificate(int idCertificate) {
        this.idCertificate = idCertificate;
    }

    public int getIdCv() {
        return idCv;
    }

    public void setIdCv(int idCv) {
        this.idCv = idCv;
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

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificate certificate)) return false;
        return idCertificate == certificate.idCertificate &&
                idCv == certificate.idCv &&
                Objects.equals(title, certificate.title) &&
                Objects.equals(description, certificate.description) &&
                Objects.equals(media, certificate.media) &&
                Objects.equals(association, certificate.association) &&
                Objects.equals(date, certificate.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCertificate, idCv, title, description, media, association, date);
    }

    // toString
    @Override
    public String toString() {
        return "Certificate{" +
                "idCertificate=" + idCertificate +
                ", idCv=" + idCv +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", media='" + media + '\'' +
                ", association='" + association + '\'' +
                ", date=" + date +
                '}';
    }
}
