package esprit.tn.guiproject.services;

import esprit.tn.guiproject.connection.DatabaseConnection;
import esprit.tn.guiproject.connection.service.ReservationAbonnement;
import esprit.tn.guiproject.connection.service.ICrud;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationAbonnementService implements ICrud<ReservationAbonnement> {

    private Connection connection;

    public ReservationAbonnementService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void ajouter(ReservationAbonnement r) throws SQLException {
        String sql = "INSERT INTO reservation_abonnement (id_utilisateur, id_abonement, date_reservation, etat, image_path) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, r.getUserId());
        ps.setInt(2, r.getAbonnementId());
        ps.setString(3, r.getDateReservation());
        ps.setString(4, r.getEtat());
        ps.setString(5, r.getImagePath());

        ps.executeUpdate();
        System.out.println("Réservation avec image ajoutée avec succès !");
    }

    @Override
    public void supprimer(ReservationAbonnement r) throws SQLException {
        String sql = "DELETE FROM reservation_abonnement WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, r.getId());
        ps.executeUpdate();
        System.out.println("Réservation supprimée !");
    }

    @Override
    public void modifier(ReservationAbonnement r) throws SQLException {
        String sql = "UPDATE reservation_abonnement SET id_utilisateur=?, id_abonement=?, date_reservation=?, etat=?, image_path=? WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, r.getUserId());
        ps.setInt(2, r.getAbonnementId());
        ps.setString(3, r.getDateReservation());
        ps.setString(4, r.getEtat());
        ps.setString(5, r.getImagePath());
        ps.setInt(6, r.getId());
        ps.executeUpdate();
        System.out.println("Réservation modifiée !");
    }

    @Override
    public List<ReservationAbonnement> getAll() throws SQLException {
        List<ReservationAbonnement> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation_abonnement";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            ReservationAbonnement r = new ReservationAbonnement();
            r.setId(rs.getInt("id"));
            r.setUserId(rs.getInt("id_utilisateur"));
            r.setAbonnementId(rs.getInt("id_abonement"));
            r.setDateReservation(rs.getString("date_reservation"));
            r.setEtat(rs.getString("etat"));
            r.setImagePath(rs.getString("image_path"));
            list.add(r);
        }

        return list;
    }

    public List<String> getAllUsers() throws SQLException {
        List<String> users = new ArrayList<>();
        String sql = "SELECT id, nom, prenom FROM utilisateur";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            users.add(rs.getInt("id") + " - " + rs.getString("nom") + " " + rs.getString("prenom"));
        }

        return users;
    }

    public String getUserName(int userId) throws SQLException {
        String sql = "SELECT nom, prenom FROM utilisateur WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("nom") + " " + rs.getString("prenom");
        }
        return "Utilisateur inconnu";
    }

    public String getAbonnementName(int abonnementId) throws SQLException {
        String sql = "SELECT nom FROM abonement WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, abonnementId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("nom");
        }
        return "Abonnement inconnu";
    }
}
