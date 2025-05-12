package esprit.tn.guiproject.gui;

import esprit.tn.guiproject.connection.DatabaseConnection;
import esprit.tn.guiproject.connection.service.ReservationAbonnement;
import esprit.tn.guiproject.services.ReservationAbonnementService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModifierAbonnementController {

    @FXML private ComboBox<String> userIdComboBox;
    @FXML private ComboBox<String> abonnementIdComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TableView<ReservationAbonnement> tableView;
    @FXML private TableColumn<ReservationAbonnement, Integer> colId;
    @FXML private TableColumn<ReservationAbonnement, Integer> colUserId;
    @FXML private TableColumn<ReservationAbonnement, Integer> colAbonnementId;
    @FXML private TableColumn<ReservationAbonnement, String> colDate;
    @FXML private TableColumn<ReservationAbonnement, String> colEtat;
    @FXML private ImageView imageView;

    private final ReservationAbonnementService service = new ReservationAbonnementService();
    private ObservableList<ReservationAbonnement> reservationList;
    private String imagePath;

    @FXML
    private void initialize() {
        try {
            chargerComboBoxes();
            afficherReservations();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    private void chargerComboBoxes() {
        try {
            // Charger les utilisateurs
            String queryUsers = "SELECT id, nom, email FROM utilisateur";
            PreparedStatement stmtUsers = DatabaseConnection.getInstance().getConnection().prepareStatement(queryUsers);
            ResultSet rsUsers = stmtUsers.executeQuery();
            
            ObservableList<String> userItems = FXCollections.observableArrayList();
            while (rsUsers.next()) {
                int id = rsUsers.getInt("id");
                String nom = rsUsers.getString("nom");
                String email = rsUsers.getString("email");
                userItems.add(id + " - " + nom + " - " + email);
            }
            
            Platform.runLater(() -> userIdComboBox.setItems(userItems));
            
            // Charger les abonnements
            String queryAbonnements = "SELECT id, nom, prix FROM abonement";
            PreparedStatement stmtAbonnements = DatabaseConnection.getInstance().getConnection().prepareStatement(queryAbonnements);
            ResultSet rsAbonnements = stmtAbonnements.executeQuery();
            
            ObservableList<String> abonnementItems = FXCollections.observableArrayList();
            while (rsAbonnements.next()) {
                int id = rsAbonnements.getInt("id");
                String nom = rsAbonnements.getString("nom");
                double prix = rsAbonnements.getDouble("prix");
                abonnementItems.add(id + " - " + nom + " (" + prix + " €)");
            }
            
            Platform.runLater(() -> abonnementIdComboBox.setItems(abonnementItems));
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des données : " + e.getMessage());
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
        ReservationAbonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                userIdComboBox.setValue(selected.getUserId() + " - " + service.getUserName(selected.getUserId()));
                abonnementIdComboBox.setValue(selected.getAbonnementId() + " - " + service.getAbonnementName(selected.getAbonnementId()));
                datePicker.setValue(java.time.LocalDate.parse(selected.getDateReservation()));
                
                if (selected.getImagePath() != null && !selected.getImagePath().isEmpty()) {
                    try {
                        Image image = new Image(new FileInputStream(selected.getImagePath()));
                        imageView.setImage(image);
                        imagePath = selected.getImagePath();
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors du chargement des données : " + e.getMessage());
            }
        }
    }

    @FXML
    private void importerImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                imageView.setImage(image);
                imagePath = selectedFile.getAbsolutePath();
            } catch (IOException e) {
                showAlert("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void resetImageTransformations() {
        imageView.setImage(null);
        imagePath = null;
    }

    @FXML
    private void sauvegarderModifications() {
        try {
            ReservationAbonnement selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Veuillez sélectionner une réservation à modifier");
                return;
            }

            String selectedUser = userIdComboBox.getValue();
            String selectedAbonnement = abonnementIdComboBox.getValue();
            
            if (selectedUser == null || selectedAbonnement == null) {
                showAlert("Veuillez sélectionner un utilisateur et un abonnement");
                return;
            }

            if (datePicker.getValue() == null) {
                showAlert("Veuillez sélectionner une date de réservation");
                return;
            }

            // Extraire l'ID utilisateur depuis la chaîne "id - nom - email"
            int userId = Integer.parseInt(selectedUser.split(" - ")[0]);
            
            // Extraire l'ID abonnement
            int abonnementId = Integer.parseInt(selectedAbonnement.split(" - ")[0]);

            // Mettre à jour la réservation
            selected.setUserId(userId);
            selected.setAbonnementId(abonnementId);
            selected.setDateReservation(datePicker.getValue().toString());
            selected.setImagePath(imagePath);

            // Sauvegarder les modifications
            service.modifier(selected);
            showAlert("Réservation modifiée avec succès !");
            
            // Rafraîchir la table et vider les champs
            afficherReservations();
            viderChamps();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la modification : " + e.getMessage());
        }
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
            
            ((Stage) userIdComboBox.getScene().getWindow()).close();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour à la sélection : " + e.getMessage());
        }
    }

    private void viderChamps() {
        userIdComboBox.setValue(null);
        abonnementIdComboBox.setValue(null);
        datePicker.setValue(null);
        imageView.setImage(null);
        imagePath = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 