package org.example.pathfinder.Model;

public class ApplicationService {
    private int idApplication;
    private double priceOffre;
    private String description;
    private String dateApplication;
    private String status;

    public ApplicationService(int idApplication, double priceOffre, String description, String dateApplication, String status) {
        this.idApplication = idApplication;
        this.priceOffre = priceOffre;
        this.description = description;
        this.dateApplication = dateApplication;
        this.status = status;
    }

    public int getIdApplication() { return idApplication; }
    public double getPriceOffre() { return priceOffre; }
    public String getDescription() { return description; }
    public String getDateApplication() { return dateApplication; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ApplicationService{" +
                "idApplication=" + idApplication +
                ", priceOffre=" + priceOffre +
                ", description='" + description + '\'' +
                ", dateApplication='" + dateApplication + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
