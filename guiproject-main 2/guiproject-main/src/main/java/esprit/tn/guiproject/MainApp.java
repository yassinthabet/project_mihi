package esprit.tn.guiproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Première interface : Réservation Abonnement
        Parent root1 = FXMLLoader.load(getClass().getResource("/Resrvation_abonement.fxml"));
        Stage stage1 = new Stage();
        stage1.setTitle("Réservation Abonnement");
        stage1.setScene(new Scene(root1));
        stage1.show();

        // Deuxième interface : Gestion des Abonnements
        Parent root2 = FXMLLoader.load(getClass().getResource("/Gestion_abonement.fxml"));
        Stage stage2 = new Stage();
        stage2.setTitle("Gestion des Abonnements");
        stage2.setScene(new Scene(root2));
        stage2.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
//Resrvation_abonement