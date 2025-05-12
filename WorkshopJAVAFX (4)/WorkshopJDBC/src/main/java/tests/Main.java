package tests;

import entities.Personne;
import services.ServicePersonne;
import utils.MyDatabase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServicePersonne servicePersonne= new ServicePersonne();
        Personne p1= new Personne(22,"foulen","BenFoulen");
        Personne p2= new Personne(1,25,"Sami","BenFoulen");
        try {
            // servicePersonne.ajouter(p1);
            servicePersonne.modifier(p2);
            System.out.println(servicePersonne.afficher());;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}