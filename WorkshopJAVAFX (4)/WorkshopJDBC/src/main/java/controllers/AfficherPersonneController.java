package controllers;

import entities.Personne;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServicePersonne;

import java.sql.SQLException;

public class AfficherPersonneController {
    @javafx.fxml.FXML
    private TableColumn colage;
    @javafx.fxml.FXML
    private TableView tvpersonne;
    @javafx.fxml.FXML
    private TableColumn colnom;
    @javafx.fxml.FXML
    private TableColumn colprenom;

    @javafx.fxml.FXML
    void initialize() {

        ServicePersonne servicePersonne=new ServicePersonne();
        try {
            ObservableList<Personne> observableList= FXCollections.observableArrayList(servicePersonne.afficher());
            tvpersonne.setItems(observableList);
            colnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colprenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colage.setCellValueFactory(new PropertyValueFactory<>("age"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
