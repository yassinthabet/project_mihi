package esprit.tn.guiproject.gui;

import esprit.tn.guiproject.connection.DatabaseConnection;
import esprit.tn.guiproject.connection.service.ReservationAbonnement;
import esprit.tn.guiproject.services.ReservationAbonnementService;
import esprit.tn.guiproject.utils.EmailUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupprimerAbonnementController {

    @FXML private TableView<ReservationAbonnement> tableView;
    @FXML private TableColumn<ReservationAbonnement, Integer> colId;
    @FXML private TableColumn<ReservationAbonnement, Integer> colUserId;
    @FXML private TableColumn<ReservationAbonnement, Integer> colAbonnementId;
    @FXML private TableColumn<ReservationAbonnement, String> colDate;
    @FXML private TableColumn<ReservationAbonnement, String> colEtat;

    private final ReservationAbonnementService service = new ReservationAbonnementService();
    private ObservableList<ReservationAbonnement> reservationList;
    private ReservationAbonnement selectedReservation;

    @FXML
    private void initialize() {
        try {
            afficherReservations();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    private void afficherReservations() {
        try {
            reservationList = FXCollections.observableArrayList(service.getAll());
            tableView.setItems(reservationList);
            colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
            colUserId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getUserId()).asObject());
            colAbonnementId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAbonnementId()).asObject());
            colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDateReservation()));
            colEtat.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEtat()));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des réservations : " + e.getMessage());
        }
    }

    @FXML
    private void remplirChamps(MouseEvent event) {
        selectedReservation = tableView.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void supprimerReservation() {
        if (selectedReservation == null) {
            showAlert("Veuillez sélectionner une réservation à supprimer");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                // Récupérer les informations de l'utilisateur et de l'abonnement avant la suppression
                String userEmail = getUserEmail(selectedReservation.getUserId());
                String abonnementInfo = getAbonnementInfo(selectedReservation.getAbonnementId());

                // Supprimer la réservation
                service.supprimer(selectedReservation);
                showAlert("Réservation supprimée avec succès !");
                
                // Envoyer l'email de confirmation
                if (userEmail != null && !userEmail.isEmpty()) {
                    String subject = "Confirmation de suppression de réservation";
                    String content = "Bonjour,\n\n" +
                            "Nous vous confirmons la suppression de votre réservation d'abonnement.\n\n" +
                            "Détails de la réservation supprimée :\n" +
                            abonnementInfo + "\n" +
                            "Date de réservation : " + selectedReservation.getDateReservation() + "\n\n" +
                            "Si vous n'êtes pas à l'origine de cette suppression, veuillez nous contacter immédiatement.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe SmartMove";
                    
                    try {
                        EmailUtil.sendEmail(userEmail, subject, content);
                        System.out.println("Email de confirmation de suppression envoyé à " + userEmail);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
                    }
                }
                
                afficherReservations();
                selectedReservation = null;
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    @FXML
    private void annulerSuppression() {
        if (selectedReservation == null) {
            showAlert("Veuillez sélectionner une réservation à annuler");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation d'annulation");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                // Mettre à jour l'état de la réservation
                selectedReservation.setEtat("Annulé");
                service.modifier(selectedReservation);
                
                // Récupérer les informations pour l'email
                String userEmail = getUserEmail(selectedReservation.getUserId());
                String abonnementInfo = getAbonnementInfo(selectedReservation.getAbonnementId());

                // Envoyer l'email de confirmation d'annulation
                if (userEmail != null && !userEmail.isEmpty()) {
                    String subject = "Confirmation d'annulation de réservation";
                    String content = "Bonjour,\n\n" +
                            "Nous vous confirmons l'annulation de votre réservation d'abonnement.\n\n" +
                            "Détails de la réservation annulée :\n" +
                            abonnementInfo + "\n" +
                            "Date de réservation : " + selectedReservation.getDateReservation() + "\n\n" +
                            "Si vous souhaitez réserver à nouveau, n'hésitez pas à nous contacter.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe SmartMove";
                    
                    try {
                        EmailUtil.sendEmail(userEmail, subject, content);
                        System.out.println("Email de confirmation d'annulation envoyé à " + userEmail);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
                    }
                }

                showAlert("Réservation annulée avec succès !");
                afficherReservations();
                selectedReservation = null;
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors de l'annulation : " + e.getMessage());
            }
        }
    }

    private String getUserEmail(int userId) {
        try {
            String query = "SELECT email FROM utilisateur WHERE id = ?";
            PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAbonnementInfo(int abonnementId) {
        try {
            String query = "SELECT nom, description, prix, duree FROM abonement WHERE id = ?";
            PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, abonnementId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "Nom : " + rs.getString("nom") + "\n" +
                       "Description : " + rs.getString("description") + "\n" +
                       "Prix : " + rs.getDouble("prix") + " €\n" +
                       "Durée : " + rs.getInt("duree") + " mois";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @FXML
    private void retourSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SelectionAbonnement.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Sélection de l'Abonnement");
            stage.setScene(new Scene(root));
            stage.show();
            
            ((Stage) tableView.getScene().getWindow()).close();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour à la sélection : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 