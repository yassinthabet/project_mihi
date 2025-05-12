package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PersonneInfo {

    @FXML
    private TextField age;

    @FXML
    private TextField nom;

    @FXML
    private TextField prenom;

    public void setAge(String age) {
        this.age.setText(age);
    }

    public void setNom(String nom) {
        this.nom.setText(nom);
    }

    public void setPrenom(String prenom) {
        this.prenom.setText(prenom);
    }
}
