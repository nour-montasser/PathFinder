package org.example.pathfinder.Model;


import java.util.Objects;

public class TestResult {
    private Long idResult;
    private Long idUser;
    private Long idTest;
    private Float result;
    private Boolean status;

    public TestResult() {}

    public TestResult(Long idResult, Long idUser, Long idTest, Float result, Boolean status) {
        this.idResult = idResult;
        this.idUser = idUser;
        this.idTest = idTest;
        this.result = result;
        this.status = status;
    }

    public Long getIdResult() {
        return idResult;
    }

    public void setIdResult(Long idResult) {
        this.idResult = idResult;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getIdTest() {
        return idTest;
    }

    public void setIdTest(Long idTest) {
        this.idTest = idTest;
    }

    public Float getResult() {
        return result;
    }

    public void setResult(Float result) {
        this.result = result;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestResult that)) return false;
        return Objects.equals(idResult, that.idResult) && Objects.equals(idUser, that.idUser) && Objects.equals(idTest, that.idTest) && Objects.equals(result, that.result) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idResult, idUser, idTest, result, status);
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "idResult=" + idResult +
                ", idUser=" + idUser +
                ", idTest=" + idTest +
                ", result=" + result +
                ", status=" + status +
                '}';
    }
}

