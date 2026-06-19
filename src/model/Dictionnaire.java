package model;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 1 - Les Donnees (package model)
 *  Fichier : Dictionnaire.java
 * ============================================================
 *
 *  Contient la liste des mots que le joueur doit trouver.
 *  - verifier si un mot est dans la liste
 *  - supprimer un mot quand il est trouve
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** La liste des mots a decouvrir dans la grille. */
public class Dictionnaire {

    /** La liste des mots restant a trouver. */
    private ArrayList<String> mots;

    /** La liste complete des mots de la partie. */
    private ArrayList<String> tousLesMots;

    /** Chemin du fichier de dictionnaire contenant les mots disponibles. */
    private static final Path CHEMIN_DICTIONNAIRE = Paths.get("dictionnaire.txt");

    /** Liste etendue de mots disponibles pour le mode aleatoire. */
    private static final ArrayList<String> MOTS_ETENDUS = new ArrayList<String>();

    static {
        if (!chargerMotsDepuisFichier(CHEMIN_DICTIONNAIRE)) {
            initialiserMotsParDefaut();
        }
    }

    private static void initialiserMotsParDefaut() {
        Collections.addAll(MOTS_ETENDUS,
            "CHIEN", "PYTHON", "NUAGE", "ECOLE", "TIGRE", "ROSE", "LAMPE", "PLUIE", "NUIT", "VENT",
            "MAISON", "SOLEIL", "LUNE", "ETOILE", "ARBRE", "FLEUR", "HERBE", "CHAT", "OISEAU", "POISSON",
            "MONTAGNE", "RIVIERE", "OCEAN", "PLAGE", "FORET", "DESERT", "CHAMP", "JARDIN", "BOIS", "ROUTE",
            "VOITURE", "TRAIN", "AVION", "BATEAU", "VELO", "MOTO", "CAMION"
        );
    }

    private static boolean chargerMotsDepuisFichier(Path chemin) {
        try {
            List<String> lignes = Files.readAllLines(chemin, StandardCharsets.UTF_8);
            for (String ligne : lignes) {
                if (ligne == null) {
                    continue;
                }
                String mot = ligne.trim();
                if (mot.isEmpty()) {
                    continue;
                }
                mot = mot.toUpperCase();
                if (!MOTS_ETENDUS.contains(mot)) {
                    MOTS_ETENDUS.add(mot);
                }
            }
            return !MOTS_ETENDUS.isEmpty();
        } catch (IOException e) {
            System.err.println("Impossible de charger le dictionnaire depuis " + chemin + " : " + e.getMessage());
            return false;
        }
    }

    /** Cree un dictionnaire vide. */
    public Dictionnaire() {
        this.mots = new ArrayList<String>();
        this.tousLesMots = new ArrayList<String>();
    }

    /** Ajoute un mot a la liste (toujours stocke en MAJUSCULES). */
    public void ajouterMot(String mot) {
        if (mot == null || mot.isBlank()) {
            return;
        }
        String motMajuscule = mot.trim().toUpperCase();
        if (motMajuscule.isEmpty() || tousLesMots.contains(motMajuscule)) {
            return;
        }
        mots.add(motMajuscule);
        tousLesMots.add(motMajuscule);
    }

    /** Renvoie true si le mot fait partie de la liste a trouver. */
    public boolean contient(String mot) {
        return mots.contains(mot.toUpperCase());
    }

    /** Supprime un mot de la liste (appele quand le joueur l'a trouve). */
    public void supprimer(String mot) {
        mots.remove(mot.toUpperCase());
    }

    /** Renvoie true si le mot appartient a la partie mais a deja ete trouve. */
    public boolean estDejaTrouve(String mot) {
        String motMajuscule = mot.toUpperCase();
        return tousLesMots.contains(motMajuscule) && !mots.contains(motMajuscule);
    }

    /** Renvoie true s'il ne reste plus aucun mot a trouver. */
    public boolean estVide() {
        return mots.isEmpty();
    }

    /** Renvoie le nombre total de mots de la partie. */
    public int getNombreTotal() {
        return tousLesMots.size();
    }

    /** Renvoie le nombre de mots deja trouves. */
    public int getNombreTrouves() {
        return tousLesMots.size() - mots.size();
    }

    /** Renvoie la liste des mots encore a trouver. */
    public ArrayList<String> getMots() {
        return new ArrayList<String>(mots);
    }

    /** Renvoie la liste complete des mots de la partie. */
    public ArrayList<String> getTousLesMots() {
        return new ArrayList<String>(tousLesMots);
    }

    /** 
     * Retourne une liste de N mots aleatoires selectiones dans MOTS_ETENDUS.
     * Les mots sont melange pour chaque appel.
     */
    public static ArrayList<String> genererMotsAleatoires(int nombre) {
        ArrayList<String> candidats = new ArrayList<String>();
        for (String mot : MOTS_ETENDUS) {
            candidats.add(mot);
        }
        Collections.shuffle(candidats);
        ArrayList<String> resultat = new ArrayList<String>();
        for (int i = 0; i < Math.min(nombre, candidats.size()); i++) {
            resultat.add(candidats.get(i));
        }
        return resultat;
    }

    /** Remplit le dictionnaire avec N mots aleatoires. */
    public void remplirAvecMotsAleatoires(int nombre) {
        mots.clear();
        tousLesMots.clear();
        ArrayList<String> motsAleatoires = genererMotsAleatoires(nombre);
        for (String mot : motsAleatoires) {
            ajouterMot(mot);
        }
    }

    /**
     * Change la liste globale des mots disponibles en chargeant un nouveau fichier.
     * Si le chargement échoue, restaure la liste précédente.
     */
    public static boolean changerTheme(java.nio.file.Path chemin) {
        ArrayList<String> backup = new ArrayList<String>(MOTS_ETENDUS);
        MOTS_ETENDUS.clear();
        if (chargerMotsDepuisFichier(chemin)) {
            return true;
        } else {
            MOTS_ETENDUS.addAll(backup);
            return false;
        }
    }

    /** Retourne la liste etendue de mots disponibles. */
    public static String[] getMotsDisponibles() {
        return MOTS_ETENDUS.toArray(new String[0]);
    }
}
