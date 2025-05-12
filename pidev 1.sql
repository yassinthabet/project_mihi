-- Table des utilisateurs
CREATE TABLE Utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    mot_de_passe VARCHAR(255),
    type_utilisateur VARCHAR(50),
    preferences TEXT
);

-- Table des transports
CREATE TABLE Transport (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50),
    nom VARCHAR(100),
    disponibilite BOOLEAN,
    horaire VARCHAR(100),
    position_actuelle VARCHAR(255)
);

-- Table des points d’intérêt
CREATE TABLE PointInteret (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    latitude DOUBLE,
    longitude DOUBLE,
    type VARCHAR(50)
);

-- Table des trajets
CREATE TABLE Trajet (
    id INT PRIMARY KEY AUTO_INCREMENT,
    point_depart INT,
    point_arrivee INT,
    distance DOUBLE,
    temps_estime TIME,
    FOREIGN KEY (point_depart) REFERENCES PointInteret(id),
    FOREIGN KEY (point_arrivee) REFERENCES PointInteret(id)
);

-- Table des itinéraires
CREATE TABLE Itineraire (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT,
    trajet_id INT,
    mode_transport VARCHAR(50),
    date_heure DATETIME,
    etat VARCHAR(50),
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id),
    FOREIGN KEY (trajet_id) REFERENCES Trajet(id)
);

-- Table des services d’accessibilité
CREATE TABLE Accessibilite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lieu_id INT,
    type_acces VARCHAR(100),
    description TEXT,
    FOREIGN KEY (lieu_id) REFERENCES PointInteret(id)
);

-- Table des paiements
CREATE TABLE Paiement (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT,
    montant DECIMAL(10, 2),
    methode VARCHAR(50),
    date DATETIME,
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id)
);

-- Table des abonnements
CREATE TABLE Abonnement (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT,
    type_abonnement VARCHAR(100),
    date_debut DATE,
    date_fin DATE,
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id)
);
