package org.example.pathfinder.Model;

public class ApplicationService {
    private int idApplication;
    private double priceOffre;
    private int idUser;
    private String status;
    private int idService;

    public ApplicationService(int idApplication, double priceOffre, int idUser, String status, int idService) {
        this.idApplication = idApplication;
        this.priceOffre = priceOffre;
        this.idUser = idUser;
        this.status = status;
        this.idService = idService;
    }

    public int getIdApplication() { return idApplication; }
    public double getPriceOffre() { return priceOffre; }
    public int getIdUser() { return idUser; }
    public String getStatus() { return status; }
    public int getIdService() { return idService; }

    public void setPriceOffre(double priceOffre) { this.priceOffre = priceOffre; }
    public void setStatus(String status) { this.status = status; }
    public void setIdApplication(int idApplication) {
        this.idApplication = idApplication;
    }



    @Override
    public String toString() {
        return "ApplicationService{" +
                "idApplication=" + idApplication +
                ", priceOffre=" + priceOffre +
                ", idUser=" + idUser +
                ", status='" + status + '\'' +
                ", idService=" + idService +
                '}';
    }
}
