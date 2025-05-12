package esprit.tn.guiproject.gui;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import esprit.tn.guiproject.connection.DatabaseConnection;
import esprit.tn.guiproject.connection.service.ReservationAbonnement;
import esprit.tn.guiproject.connection.service.ServiceAbonement;
import esprit.tn.guiproject.entities.Abonement;
import esprit.tn.guiproject.services.ReservationAbonnementService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.geometry.Point2D;
import esprit.tn.guiproject.utils.EmailUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reservation_abonement_controlor {

    @FXML private ComboBox<String> userIdComboBox;
    @FXML private ComboBox<String> abonnementIdComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField imagePathField;
    @FXML private TableView<ReservationAbonnement> tableView;
    @FXML private TableColumn<ReservationAbonnement, Integer> colId;
    @FXML private TableColumn<ReservationAbonnement, Integer> colUserId;
    @FXML private TableColumn<ReservationAbonnement, Integer> colAbonnementId;
    @FXML private TableColumn<ReservationAbonnement, String> colDate;
    @FXML private TableColumn<ReservationAbonnement, String> colImagePath;
    @FXML private TableColumn<ReservationAbonnement, String> colEtat;
    @FXML private ImageView imageView;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private Slider zoomSlider;
    @FXML private Slider rotationSlider;
    @FXML private Button resetButton;
    @FXML private Label coordXLabel;
    @FXML private Label coordYLabel;
    @FXML private Label zoomLabel;
    @FXML private Label rotationLabel;

    private final ReservationAbonnementService service = new ReservationAbonnementService();
    private final ServiceAbonement abonnementService = new ServiceAbonement();
    private ObservableList<ReservationAbonnement> reservationList;
    private static final String STRIPE_SECRET_KEY = "sk_test_51RMG3yR4wmLjLi1gN7XLjUxM1G81ZlvEP95ijroDZylZ8Id7RxfShW1tncIE9I3PxZvShNyQgI2gBVBpvN007cod00xSWeVq5C";
    private static final String STRIPE_PUBLIC_KEY = "pk_test_51RMG3yR4wmLjLi1glIOliKmR7Bkg9HLcYeMeE3TKtpGHHsjfzuSjLZqLR7PM73XxwpGNLsYBbEtmy8KcqH0NihF400oeTT7HCD";
    private Scale scale;
    private Rotate rotate;
    private Translate translate;
    private Point2D dragDelta = new Point2D(0, 0);

    @FXML
    private void initialize() {
        try {
            // Configuration de Stripe
            Stripe.apiKey = STRIPE_SECRET_KEY;
            
            System.out.println("Initialisation du contrôleur...");
            
            // Initialisation des composants
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(320);
            imageView.setFitHeight(240);

            // Création du masque pour l'image
            Rectangle clip = new Rectangle(320, 240);
            clip.setArcWidth(5);
            clip.setArcHeight(5);
            imageView.setClip(clip);

            // Configuration des sliders
            zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                imageView.setScaleX(newVal.doubleValue());
                imageView.setScaleY(newVal.doubleValue());
                updateZoomLabel(newVal.doubleValue());
            });

            rotationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                imageView.setRotate(newVal.doubleValue());
                updateRotationLabel(newVal.doubleValue());
            });

            // Gestion du déplacement de l'image
            imageView.setOnMousePressed(event -> {
                imageView.setCursor(Cursor.CLOSED_HAND);
                dragDelta = new Point2D(
                    imageView.getTranslateX() - event.getX(),
                    imageView.getTranslateY() - event.getY()
                );
            });

            imageView.setOnMouseReleased(event -> {
                imageView.setCursor(Cursor.DEFAULT);
            });

            imageView.setOnMouseDragged(event -> {
                imageView.setTranslateX(event.getX() + dragDelta.getX());
                imageView.setTranslateY(event.getY() + dragDelta.getY());
                updateCoordinateLabels(imageView.getTranslateX(), imageView.getTranslateY());
            });

            // Initialisation des labels
            updateCoordinateLabels(0, 0);
            updateZoomLabel(1.0);
            updateRotationLabel(0);

            // Chargement des données
            System.out.println("Chargement des ComboBox...");
            chargerComboBoxes();
            System.out.println("Chargement des réservations...");
            afficherReservations();
            System.out.println("Initialisation terminée");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur d'initialisation : " + e.getMessage());
        }
    }

    private void updateCoordinateLabels(double x, double y) {
        coordXLabel.setText(String.format("%.1f", x));
        coordYLabel.setText(String.format("%.1f", y));
    }

    private void updateZoomLabel(double zoom) {
        zoomLabel.setText(String.format("%.0f%%", zoom * 100));
    }

    private void updateRotationLabel(double rotation) {
        rotationLabel.setText(String.format("%.0f°", rotation));
    }

    @FXML
    private void resetImageTransformations() {
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        imageView.setRotate(0);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        zoomSlider.setValue(1.0);
        rotationSlider.setValue(0);
        updateCoordinateLabels(0, 0);
        updateZoomLabel(1.0);
        updateRotationLabel(0);
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
                imagePathField.setText(selectedFile.getAbsolutePath());
                resetImageTransformations();
            } catch (IOException e) {
                showAlert("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }

    private void chargerComboBoxes() {
        try {
            System.out.println("Début du chargement des ComboBox...");
            
            // Vérifier la connexion
            if (DatabaseConnection.getInstance().getConnection() == null) {
                System.out.println("Erreur : Pas de connexion à la base de données");
                showAlert("Erreur de connexion à la base de données");
                return;
            }

            // Charger les utilisateurs depuis la base de données
            String queryUsers = "SELECT id, nom, email FROM utilisateur";
            System.out.println("Requête utilisateurs : " + queryUsers);
            
            PreparedStatement stmtUsers = DatabaseConnection.getInstance().getConnection().prepareStatement(queryUsers);
            ResultSet rsUsers = stmtUsers.executeQuery();
            
            ObservableList<String> userItems = FXCollections.observableArrayList();
            int userCount = 0;
            while (rsUsers.next()) {
                int id = rsUsers.getInt("id");
                String nom = rsUsers.getString("nom");
                String email = rsUsers.getString("email");
                String userItem = id + " - " + nom + " - " + email;
                userItems.add(userItem);
                userCount++;
                System.out.println("Utilisateur trouvé : " + userItem);
            }
            System.out.println("Nombre d'utilisateurs chargés : " + userCount);
            
            Platform.runLater(() -> {
                userIdComboBox.setItems(userItems);
                System.out.println("ComboBox utilisateurs mis à jour");
            });
            
            rsUsers.close();
            stmtUsers.close();

            // Charger les abonnements depuis la base de données
            String queryAbonnements = "SELECT id, nom, prix FROM abonement";
            System.out.println("Requête abonnements : " + queryAbonnements);
            
            PreparedStatement stmtAbonnements = DatabaseConnection.getInstance().getConnection().prepareStatement(queryAbonnements);
            ResultSet rsAbonnements = stmtAbonnements.executeQuery();
            
            ObservableList<String> abonnementItems = FXCollections.observableArrayList();
            int abonnementCount = 0;
            while (rsAbonnements.next()) {
                int id = rsAbonnements.getInt("id");
                String nom = rsAbonnements.getString("nom");
                double prix = rsAbonnements.getDouble("prix");
                String abonnementItem = id + " - " + nom + " (" + prix + " €)";
                abonnementItems.add(abonnementItem);
                abonnementCount++;
                System.out.println("Abonnement trouvé : " + abonnementItem);
            }
            System.out.println("Nombre d'abonnements chargés : " + abonnementCount);
            
            Platform.runLater(() -> {
                abonnementIdComboBox.setItems(abonnementItems);
                System.out.println("ComboBox abonnements mis à jour");
            });
            
            rsAbonnements.close();
            stmtAbonnements.close();

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du chargement des ComboBox : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur lors du chargement des données : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du chargement des ComboBox : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur inattendue : " + e.getMessage());
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

            // Vérification de la sélection d'un abonnement
            String selectedAbonnement = abonnementIdComboBox.getValue();
            if (selectedAbonnement == null) {
                showAlert("Veuillez sélectionner un abonnement");
                return;
            }

            // Extraction du prix de l'abonnement
            double prix = Double.parseDouble(selectedAbonnement.split("\\(")[1].split("€")[0].trim());
            int montantEnCentimes = (int) (prix * 100); // Conversion en centimes pour Stripe

            // Création de l'intention de paiement
            Map<String, Object> params = new HashMap<>();
            params.put("amount", montantEnCentimes);
            params.put("currency", "eur");
            params.put("payment_method_types", List.of("card"));
            params.put("description", "Paiement abonnement: " + selectedAbonnement);

            PaymentIntent intent = PaymentIntent.create(params);

            // Simulation du paiement (à remplacer par l'intégration réelle)
            showAlert("Paiement traité avec succès ! Montant: " + prix + "€");
            
            // Réinitialisation des champs de paiement
            cardNumberField.clear();
            expiryField.clear();
            cvvField.clear();
            
        } catch (StripeException e) {
            System.err.println("Erreur Stripe : " + e.getMessage());
            showAlert("Erreur lors du paiement : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            showAlert("Erreur inattendue lors du paiement : " + e.getMessage());
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
            colImagePath.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImagePath()));
            colEtat.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEtat()));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des réservations : " + e.getMessage());
        }
    }

    @FXML
    private void ajouterReservation() {
        try {
            String selectedUser = userIdComboBox.getValue();
            String selectedAbonnement = abonnementIdComboBox.getValue();
            
            if (selectedUser == null || selectedAbonnement == null) {
                showAlert("Veuillez sélectionner un utilisateur et un abonnement");
                return;
            }

            // Extraire l'ID utilisateur et l'email depuis la chaîne "id - nom - email"
            String[] userParts = selectedUser.split(" - ");
            int userId = Integer.parseInt(userParts[0]);
            String userNom = userParts.length > 1 ? userParts[1] : "";
            String userEmail = userParts.length > 2 ? userParts[2] : "";

            // Vérifier si la date est sélectionnée
            if (datePicker.getValue() == null) {
                showAlert("Veuillez sélectionner une date de réservation");
                return;
            }

            // Extraire l'ID abonnement
            int abonnementId = Integer.parseInt(selectedAbonnement.split(" - ")[0]);

            // Récupérer les infos de l'abonnement depuis la base
            String nomAbonnement = "";
            String description = "";
            double prix = 0;
            int duree = 0;
            String type = "";
            try {
                java.sql.Connection conn = DatabaseConnection.getInstance().getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement("SELECT nom, description, prix, duree, type_abonement FROM abonement WHERE id = ?");
                ps.setInt(1, abonnementId);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nomAbonnement = rs.getString("nom");
                    description = rs.getString("description");
                    prix = rs.getDouble("prix");
                    duree = rs.getInt("duree");
                    type = rs.getString("type_abonement");
                }
                rs.close();
                ps.close();
            } catch (Exception ex) {
                System.err.println("Erreur lors de la récupération de l'abonnement : " + ex.getMessage());
            }

            // Créer la réservation
            ReservationAbonnement r = new ReservationAbonnement(
                    userId,
                    abonnementId,
                    datePicker.getValue().toString()
            );
            r.setImagePath(imagePathField.getText());
            r.setEtat("En attente");

            // Ajouter la réservation
            service.ajouter(r);
            showAlert("Réservation ajoutée avec succès !");
            afficherReservations();
            viderChamps();

            // Envoyer l'email récapitulatif
            if (!userEmail.isEmpty()) {
                String subject = "Confirmation de votre réservation d'abonnement";
                String content = "Bonjour " + userNom + ",\n\n" +
                        "Votre réservation d'abonnement a bien été prise en compte.\n\n" +
                        "Détails de l'abonnement :\n" +
                        "- Nom : " + nomAbonnement + "\n" +
                        "- Description : " + description + "\n" +
                        "- Prix : " + prix + " €\n" +
                        "- Durée : " + duree + " mois\n" +
                        "- Type : " + type + "\n" +
                        "- Date de réservation : " + datePicker.getValue().toString() + "\n\n" +
                        "Merci pour votre confiance !";
                try {
                    EmailUtil.sendEmail(userEmail, subject, content);
                    System.out.println("Email de confirmation envoyé à " + userEmail);
                } catch (Exception ex) {
                    System.err.println("Erreur lors de l'envoi de l'email : " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void modifierReservation() {
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

            // Extraire l'ID utilisateur depuis la chaîne "id - nom - email"
            int userId = Integer.parseInt(selectedUser.split(" - ")[0]);

            // Vérifier si la date est sélectionnée
            if (datePicker.getValue() == null) {
                showAlert("Veuillez sélectionner une date de réservation");
                return;
            }

            // Mettre à jour la réservation
            selected.setUserId(userId);
            selected.setAbonnementId(Integer.parseInt(selectedAbonnement.split(" - ")[0]));
            selected.setDateReservation(datePicker.getValue().toString());
            selected.setImagePath(imagePathField.getText());

            service.modifier(selected);
            showAlert("Réservation modifiée avec succès !");
            afficherReservations();
            viderChamps();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void supprimerReservation() {
        try {
            ReservationAbonnement selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.supprimer(selected);
                afficherReservations();
                viderChamps();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @FXML
    private void rafraichirTable() {
        afficherReservations();
        viderChamps();
    }

    @FXML
    private void remplirChamps(MouseEvent event) {
        ReservationAbonnement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                userIdComboBox.setValue(selected.getUserId() + " - " + service.getUserName(selected.getUserId()));
                abonnementIdComboBox.setValue(selected.getAbonnementId() + " - " + service.getAbonnementName(selected.getAbonnementId()));
                datePicker.setValue(LocalDate.parse(selected.getDateReservation()));
                imagePathField.setText(selected.getImagePath());
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur lors du chargement des données : " + e.getMessage());
            }
        }
    }

    private void viderChamps() {
        userIdComboBox.setValue(null);
        abonnementIdComboBox.setValue(null);
        datePicker.setValue(null);
        imagePathField.clear();
        cardNumberField.clear();
        expiryField.clear();
        cvvField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
