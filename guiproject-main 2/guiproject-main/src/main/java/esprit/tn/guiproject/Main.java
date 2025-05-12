package esprit.tn.guiproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Première interface : Sélection de l'Abonnement
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/SelectionAbonnement.fxml"));
            Parent root1 = loader1.load();
            Stage stage1 = new Stage();
            stage1.setTitle("Sélection de l'Abonnement");
            stage1.setScene(new Scene(root1));
            stage1.show();

            // Deuxième interface : Gestion des Abonnements
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/Gestion_abonement.fxml"));
            Parent root2 = loader2.load();
            Stage stage2 = new Stage();
            stage2.setTitle("Gestion des Abonnements");
            stage2.setScene(new Scene(root2));
            stage2.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
