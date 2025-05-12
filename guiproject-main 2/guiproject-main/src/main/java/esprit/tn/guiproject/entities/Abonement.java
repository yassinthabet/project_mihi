package esprit.tn.guiproject.entities;

import java.sql.Timestamp;

public class Abonement {

    private int id;
    private String nom;
    private String description;
    private double prix;
    private int duree;
    private String typeAbonement;
    private Timestamp dateCreation;

    public Abonement() {}

    public Abonement(int id, String nom, String description, double prix, int duree, String typeAbonement, Timestamp dateCreation) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.duree = duree;
        this.typeAbonement = typeAbonement;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public String getTypeAbonement() { return typeAbonement; }
    public void setTypeAbonement(String typeAbonement) { this.typeAbonement = typeAbonement; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return "Abonement{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", duree=" + duree +
                ", typeAbonement='" + typeAbonement + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
