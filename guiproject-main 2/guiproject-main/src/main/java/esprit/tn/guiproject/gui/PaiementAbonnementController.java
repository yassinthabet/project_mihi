package esprit.tn.guiproject.gui;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import esprit.tn.guiproject.connection.DatabaseConnection;
import esprit.tn.guiproject.connection.service.ReservationAbonnement;
import esprit.tn.guiproject.services.ReservationAbonnementService;
import esprit.tn.guiproject.utils.EmailUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaiementAbonnementController {

    @FXML private Label recapLabel;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;

    private String selectedUser;
    private String selectedAbonnement;
    private String dateReservation;
    private String imagePath;
    private final ReservationAbonnementService service = new ReservationAbonnementService();
    private static final String STRIPE_SECRET_KEY = "sk_test_51RMG3yR4wmLjLi1gN7XLjUxM1G81ZlvEP95ijroDZylZ8Id7RxfShW1tncIE9I3PxZvShNyQgI2gBVBpvN007cod00xSWeVq5C";

    public void setReservationData(String user, String abonnement, String date, String imagePath) {
        this.selectedUser = user;
        this.selectedAbonnement = abonnement;
        this.dateReservation = date;
        this.imagePath = imagePath;
        updateRecapLabel();
    }

    private void updateRecapLabel() {
        try {
            // Extraire les informations
            String[] userParts = selectedUser.split(" - ");
            String[] abonnementParts = selectedAbonnement.split(" - ");
            
            // Récupérer les détails de l'abonnement depuis la base de données
            String query = "SELECT description, duree, type_abonement FROM abonement WHERE id = ?";
            PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
            ps.setInt(1, Integer.parseInt(abonnementParts[0]));
            ResultSet rs = ps.executeQuery();
            
            String description = "";
            int duree = 0;
            String type = "";
            
            if (rs.next()) {
                description = rs.getString("description");
                duree = rs.getInt("duree");
                type = rs.getString("type_abonement");
            }
            
            // Construire le récapitulatif
            StringBuilder recap = new StringBuilder();
            recap.append("Récapitulatif de votre commande :\n\n");
            recap.append("Utilisateur : ").append(userParts[1]).append("\n");
            recap.append("Abonnement : ").append(abonnementParts[1]).append("\n");
            recap.append("Description : ").append(description).append("\n");
            recap.append("Durée : ").append(duree).append(" mois\n");
            recap.append("Type : ").append(type).append("\n");
            recap.append("Date de réservation : ").append(dateReservation).append("\n");
            recap.append("Image : ").append(imagePath).append("\n");
            
            recapLabel.setText(recap.toString());
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la récupération des détails : " + e.getMessage());
        }
    }

    @FXML
    private void processPayment() {
        try {
            // Vérification des champs de paiement
            if (cardNumberField.getText().isEmpty() || expiryField.getText().isEmpty() || cvvField.getText().isEmpty()) {
                showAlert("Veuillez remplir tous les champs de paiement");
                return;
            }

            // Configuration de Stripe
            Stripe.apiKey = STRIPE_SECRET_KEY;

            // Extraction du prix de l'abonnement
            double prix = Double.parseDouble(selectedAbonnement.split("\\(")[1].split("€")[0].trim());
            int montantEnCentimes = (int) (prix * 100);

            // Création de l'intention de paiement
            Map<String, Object> params = new HashMap<>();
            params.put("amount", montantEnCentimes);
            params.put("currency", "eur");
            params.put("payment_method_types", List.of("card"));
            params.put("description", "Paiement abonnement: " + selectedAbonnement);

            PaymentIntent intent = PaymentIntent.create(params);

            // Créer la réservation
            String[] userParts = selectedUser.split(" - ");
            String[] abonnementParts = selectedAbonnement.split(" - ");
            
            ReservationAbonnement r = new ReservationAbonnement(
                Integer.parseInt(userParts[0]),
                Integer.parseInt(abonnementParts[0]),
                dateReservation
            );
            r.setEtat("Payé");
            r.setImagePath(imagePath);

            // Ajouter la réservation
            service.ajouter(r);
            
            // Envoyer l'email de confirmation
            String userEmail = userParts[2]; // L'email est le troisième élément après le split
            String subject = "Confirmation de paiement - SmartMove";
            String content = "Bonjour " + userParts[1] + ",\n\n" +
                    "Nous vous confirmons que votre paiement a été traité avec succès.\n\n" +
                    "Détails de votre abonnement :\n" +
                    "- Abonnement : " + abonnementParts[1] + "\n" +
                    "- Montant payé : " + prix + " €\n" +
                    "- Date de réservation : " + dateReservation + "\n\n" +
                    "Merci de votre confiance !\n\n" +
                    "Cordialement,\n" +
                    "L'équipe SmartMove";
            
            try {
                EmailUtil.sendEmail(userEmail, subject, content);
                System.out.println("Email de confirmation envoyé à " + userEmail);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            }
            
            showAlert("Paiement traité avec succès ! Montant: " + prix + "€");
            
            // Retourner à la fenêtre de sélection
            retourSelection();
            
        } catch (StripeException | SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du paiement : " + e.getMessage());
        }
    }

    @FXML
    private void retourSelection() {
        try {
            // Charger la fenêtre de sélection
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SelectionAbonnement.fxml"));
            Parent root = loader.load();
            
            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Sélection de l'Abonnement");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Fermer la fenêtre actuelle
            ((Stage) recapLabel.getScene().getWindow()).close();
            
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