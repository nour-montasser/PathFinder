package org.example.pathfinder.Model;

import java.sql.Date;

public class TestResult {
    private Long idResult;
    private Long idUser;
    private Long idTest;
    private int result; // Score obtained
    private Date date;
    private int status; // 1 = Passed, 0 = Failed (Stored as TINYINT in DB)

    // ✅ Constructors
    public TestResult() {}

    public TestResult(Long idUser, Long idTest, int result, Date date, int status) {
        this.idUser = idUser;
        this.idTest = idTest;
        this.result = result;
        this.date = date;
        this.status = status;
    }

    // ✅ Getters & Setters
    public Long getIdResult() { return idResult; }
    public void setIdResult(Long idResult) { this.idResult = idResult; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public Long getIdTest() { return idTest; }
    public void setIdTest(Long idTest) { this.idTest = idTest; }

    public int getResult() { return result; }
    public void setResult(int result) { this.result = result; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    // ✅ Helper methods for converting between int (DB) and String (Java)
    public String getStatusString() {
        return (status == 1) ? "Passed" : "Failed";
    }

    public void setStatusFromString(String status) {
        this.status = status.equalsIgnoreCase("Passed") ? 1 : 0;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "idResult=" + idResult +
                ", idUser=" + idUser +
                ", idTest=" + idTest +
                ", result=" + result +
                ", date=" + date +
                ", status='" + getStatusString() + '\'' +
                '}';
    }
}
