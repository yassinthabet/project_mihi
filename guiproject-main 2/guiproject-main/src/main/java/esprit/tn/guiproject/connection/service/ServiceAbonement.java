package esprit.tn.guiproject.connection.service;

import esprit.tn.guiproject.connection.DatabaseConnection;

import esprit.tn.guiproject.entities.Abonement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceAbonement implements ICrud<Abonement> {

    private Connection cnx;

    public ServiceAbonement() {
        cnx = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void ajouter(Abonement a) throws Exception {
        String req = "INSERT INTO abonement (nom, description, prix, duree, type_abonement) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, a.getNom());
        ps.setString(2, a.getDescription());
        ps.setDouble(3, a.getPrix());
        ps.setInt(4, a.getDuree());
        ps.setString(5, a.getTypeAbonement());
        ps.executeUpdate();
        System.out.println("Abonement ajouté !");
    }

    @Override
    public void supprimer(Abonement a) throws Exception {
        String req = "DELETE FROM abonement WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, a.getId());
        ps.executeUpdate();
        System.out.println("Abonement supprimé !");
    }

    @Override
    public void modifier(Abonement a) throws Exception {
        String req = "UPDATE abonement SET nom = ?, description = ?, prix = ?, duree = ?, type_abonement = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, a.getNom());
        ps.setString(2, a.getDescription());
        ps.setDouble(3, a.getPrix());
        ps.setInt(4, a.getDuree());
        ps.setString(5, a.getTypeAbonement());
        ps.setInt(6, a.getId());
        ps.executeUpdate();
        System.out.println("Abonement modifié !");
    }

    @Override
    public List<Abonement> getAll() throws Exception {
        List<Abonement> list = new ArrayList<>();
        String req = "SELECT * FROM abonement";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Abonement a = new Abonement();
            a.setId(rs.getInt("id"));
            a.setNom(rs.getString("nom"));
            a.setDescription(rs.getString("description"));
            a.setPrix(rs.getDouble("prix"));
            a.setDuree(rs.getInt("duree"));
            a.setTypeAbonement(rs.getString("type_abonement"));
            a.setDateCreation(rs.getTimestamp("date_creation"));
            list.add(a);
        }

        return list;
    }
}
