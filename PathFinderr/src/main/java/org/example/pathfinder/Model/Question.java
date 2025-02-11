package org.example.pathfinder.Model;

import java.util.Objects;

public class Question {
    private Long idQuestion;
    private String question;
    private Long idTest;
    private String responses;
    private String correctResponse;
    private Integer score;

    public Question() {}

    public Question(Long idQuestion, String question, Long idTest, String responses, String correctResponse, Integer score) {
        this.idQuestion = idQuestion;
        this.question = question;
        this.idTest = idTest;
        this.responses = responses;
        this.correctResponse = correctResponse;
        this.score = score;
    }

    public Long getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(Long idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getIdTest() {
        return idTest;
    }

    public void setIdTest(Long idTest) {
        this.idTest = idTest;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public String getCorrectResponse() {
        return correctResponse;
    }

    public void setCorrectResponse(String correctResponse) {
        this.correctResponse = correctResponse;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question that)) return false;
        return Objects.equals(idQuestion, that.idQuestion) && Objects.equals(question, that.question) && Objects.equals(idTest, that.idTest) && Objects.equals(responses, that.responses) && Objects.equals(correctResponse, that.correctResponse) && Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuestion, question, idTest, responses, correctResponse, score);
    }

    @Override
    public String toString() {
        return "Question{" +
                "idQuestion=" + idQuestion +
                ", question='" + question + '\'' +
                ", idTest=" + idTest +
                ", responses='" + responses + '\'' +
                ", correctResponse='" + correctResponse + '\'' +
                ", score=" + score +
                '}';
    }
}

