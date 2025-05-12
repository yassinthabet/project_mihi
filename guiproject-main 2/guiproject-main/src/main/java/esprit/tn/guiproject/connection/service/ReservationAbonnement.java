package esprit.tn.guiproject.connection.service;

public class ReservationAbonnement {
    private int id;
    private int userId; // ID de l'utilisateur
    private int abonnementId; // ID de l'abonnement
    private String dateReservation;
    private String etat;
    private String imagePath;



    // Constructeurs
    public ReservationAbonnement() {
    }


    public ReservationAbonnement(int id, int userId, int abonnementId, String dateReservation, String etat) {
        this.id = id;
        this.userId = userId;
        this.abonnementId = abonnementId;
        this.dateReservation = dateReservation;
        this.etat = etat;
    }

    public ReservationAbonnement(int userId, int abonnementId, String dateReservation) {
        this.userId = userId;
        this.abonnementId = abonnementId;
        this.dateReservation = dateReservation;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAbonnementId() {
        return abonnementId;
    }

    public void setAbonnementId(int abonnementId) {
        this.abonnementId = abonnementId;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    @Override
    public String toString() {
        return "ReservationAbonnement{" +
                "id=" + id +
                ", userId=" + userId +
                ", abonnementId=" + abonnementId +
                ", dateReservation='" + dateReservation + '\'' +
                '}';
    }



    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;

}}
