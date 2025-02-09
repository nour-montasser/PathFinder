package org.example.pathfinder.Model;

import java.util.Objects;

public class CoverLetter {
    private Long id_CoverLetter;
    private Long id_App;
    private String content;
    private String subject;

    // Constructor
    public CoverLetter(Long id_App, String content, String subject) {
        this.id_App = id_App;
        this.content = content;
        this.subject = subject;
    }

    // Getters and setters
    public Long getIdCoverLetter() {
        return id_CoverLetter;
    }

    public void setIdCoverLetter(Long id_CoverLetter) {
        this.id_CoverLetter = id_CoverLetter;
    }

    public Long getIdApp() {
        return id_App;
    }

    public void setIdApp(Long id_App) {
        this.id_App = id_App;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "CoverLetter{" +
                "id_CoverLetter=" + id_CoverLetter +
                ", id_App=" + id_App +
                ", content='" + content + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoverLetter that = (CoverLetter) o;
        return Objects.equals(id_CoverLetter, that.id_CoverLetter) &&
                Objects.equals(id_App, that.id_App) &&
                Objects.equals(content, that.content) &&
                Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_CoverLetter, id_App, content, subject);
    }
}
