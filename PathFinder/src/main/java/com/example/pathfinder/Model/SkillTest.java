package com.example.pathfinder.Model;


import java.util.Objects;

public class SkillTest {
    private Long idTest;
    private String title;
    private String description;
    private Long duration;
    private Long idJobOffer;
    private Long scoreRequired;

    public SkillTest() {}

    public SkillTest(Long idTest, String title, String description, Long duration, Long idJobOffer, Long scoreRequired) {
        this.idTest = idTest;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.idJobOffer = idJobOffer;
        this.scoreRequired = scoreRequired;
    }

    public Long getIdTest() {
        return idTest;
    }

    public void setIdTest(Long idTest) {
        this.idTest = idTest;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getIdJobOffer() {
        return idJobOffer;
    }

    public void setIdJobOffer(Long idJobOffer) {
        this.idJobOffer = idJobOffer;
    }

    public Long getScoreRequired() {
        return scoreRequired;
    }

    public void setScoreRequired(Long scoreRequired) {
        this.scoreRequired = scoreRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillTest that)) return false;
        return Objects.equals(idTest, that.idTest) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(duration, that.duration) && Objects.equals(idJobOffer, that.idJobOffer) && Objects.equals(scoreRequired, that.scoreRequired);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTest, title, description, duration, idJobOffer, scoreRequired);
    }

    @Override
    public String toString() {
        return "SkillTest{" +
                "idTest=" + idTest +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", idJobOffer=" + idJobOffer +
                ", scoreRequired=" + scoreRequired +
                '}';
    }
}
