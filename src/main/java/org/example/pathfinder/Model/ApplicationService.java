package org.example.pathfinder.Model;

public class ApplicationService {
    private int idApplication;
    private double priceOffre;
    private int idUser;
    private String status;
    private int idService;
    private int rating;
    private String name; // User's name

    public ApplicationService(int idApplication, double priceOffre, int idUser, String status, int idService,int Rating) {
        this.idApplication = idApplication;
        this.priceOffre = priceOffre;
        this.idUser = idUser;
        this.status = status;
        this.idService = idService;
        this.rating = rating;

    }

    public int getIdApplication() { return idApplication; }
    public double getPriceOffre() { return priceOffre; }
    public int getIdUser() { return idUser; }
    public String getStatus() { return status; }
    public int getIdService() { return idService; }
    public int getRating() { return rating; }

    public void setPriceOffre(double priceOffre) { this.priceOffre = priceOffre; }
    public void setStatus(String status) { this.status = status; }
    public void setIdApplication(int idApplication) {
        this.idApplication = idApplication;
    }
    public void setRating(int rating) { this.rating = rating; }



    @Override
    public String toString() {
        return "ApplicationService{" +
                "idApplication=" + idApplication +
                ", priceOffre=" + priceOffre +
                ", idUser=" + idUser +
                ", status='" + status + '\'' +
                ", idService=" + idService +
                ", rating=" + rating +
                '}';
    }
}
