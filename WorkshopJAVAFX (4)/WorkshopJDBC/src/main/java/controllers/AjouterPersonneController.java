package controllers;

import entities.Personne;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.ServicePersonne;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterPersonneController {

    @FXML
    private TextField ageTF;

    @FXML
    private TextField nomTF;

    @FXML
    private TextField prenomTF;

    @FXML
    void AjouterPersonne(ActionEvent event) {
        ServicePersonne servicePersonne = new ServicePersonne();
        Personne personne = new Personne(Integer.parseInt(ageTF.getText()),nomTF.getText(),prenomTF.getText());
        try {
            servicePersonne.ajouter(personne);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("perssonne a ete ajouté avec succes !");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PersonneInfo.fxml"));
            try {
                Parent parent = loader.load();
                PersonneInfo persInfo = loader.getController();
                persInfo.setAge(ageTF.getText());
                persInfo.setNom(nomTF.getText());
                persInfo.setPrenom(prenomTF.getText());
                nomTF.getScene().setRoot(parent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText(e.getMessage());
            alert.show();
        }

    }

    @FXML
    public void AfficherPersonne(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherPersonne.fxml"));
            nomTF.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
