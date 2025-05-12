package esprit.tn.guiproject.gui;

import esprit.tn.guiproject.connection.service.ServiceAbonement;
import esprit.tn.guiproject.entities.Abonement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.ListCell;

import java.net.URL;
import java.util.ResourceBundle;

public class AbonementCRUDController implements Initializable {

    @FXML
    private TextField nomField, descriptionField, prixField, dureeField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TableView<Abonement> tableView;

    @FXML
    private TableColumn<Abonement, Integer> colId;

    @FXML
    private TableColumn<Abonement, String> colNom, colDescription, colType;

    @FXML
    private TableColumn<Abonement, Double> colPrix;

    @FXML
    private TableColumn<Abonement, Integer> colDuree;

    private ServiceAbonement service = new ServiceAbonement();
    private ObservableList<Abonement> abonementList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialiser combo
        typeComboBox.setItems(FXCollections.observableArrayList("premium", "gold", "silver"));

        // Cell factory pour symbole coloré
        typeComboBox.setCellFactory(lv -> new ListCell<String>() {
            private final Circle colorCircle = new Circle(8);
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.substring(0, 1).toUpperCase() + item.substring(1));
                    switch (item.toLowerCase()) {
                        case "gold":
                            colorCircle.setFill(Color.GOLD);
                            break;
                        case "silver":
                            colorCircle.setFill(Color.SILVER);
                            break;
                        case "premium":
                            colorCircle.setFill(Color.DARKVIOLET);
                            break;
                        default:
                            colorCircle.setFill(Color.GRAY);
                    }
                    setGraphic(colorCircle);
                }
            }
        });
        // Pour que la sélection affiche aussi le symbole :
        typeComboBox.setButtonCell(typeComboBox.getCellFactory().call(null));

        // Initialiser table
        afficherAbonements();

        // Gérer clic sur ligne
        tableView.setOnMouseClicked(this::remplirChamps);
    }

    private void afficherAbonements() {
        try {
            abonementList = FXCollections.observableArrayList(service.getAll());
            tableView.setItems(abonementList);

            colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
            colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
            colDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
            colPrix.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrix()).asObject());
            colDuree.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getDuree()).asObject());
            colType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTypeAbonement()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterAbonement() {
        try {
            Abonement a = new Abonement();
            a.setNom(nomField.getText());
            a.setDescription(descriptionField.getText());
            a.setPrix(Double.parseDouble(prixField.getText()));
            a.setDuree(Integer.parseInt(dureeField.getText()));
            a.setTypeAbonement(typeComboBox.getValue());

            service.ajouter(a);
            afficherAbonements();
            viderChamps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierAbonement() {
        try {
            Abonement selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setNom(nomField.getText());
                selected.setDescription(descriptionField.getText());
                selected.setPrix(Double.parseDouble(prixField.getText()));
                selected.setDuree(Integer.parseInt(dureeField.getText()));
                selected.setTypeAbonement(typeComboBox.getValue());

                service.modifier(selected);
                afficherAbonements();
                viderChamps();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerAbonement() {
        try {
            Abonement selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.supprimer(selected);
                afficherAbonements();
                viderChamps();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rafraichirTable() {
        afficherAbonements();
        viderChamps();
    }

    private void remplirChamps(MouseEvent event) {
        Abonement selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nomField.setText(selected.getNom());
            descriptionField.setText(selected.getDescription());
            prixField.setText(String.valueOf(selected.getPrix()));
            dureeField.setText(String.valueOf(selected.getDuree()));
            typeComboBox.setValue(selected.getTypeAbonement());
        }
    }

    private void viderChamps() {
        nomField.clear();
        descriptionField.clear();
        prixField.clear();
        dureeField.clear();
        typeComboBox.setValue(null);
    }
}
