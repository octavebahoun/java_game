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

import java.util.ArrayList;

/** La liste des mots a decouvrir dans la grille. */
public class Dictionnaire {

    /** La liste des mots restant a trouver. */
    private ArrayList<String> mots;

    /** La liste complete des mots de la partie. */
    private ArrayList<String> tousLesMots;

    /** Cree un dictionnaire vide. */
    public Dictionnaire() {
        this.mots = new ArrayList<String>();
        this.tousLesMots = new ArrayList<String>();
    }

    /** Ajoute un mot a la liste (toujours stocke en MAJUSCULES). */
    public void ajouterMot(String mot) {
        String motMajuscule = mot.toUpperCase();
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
        return mots;
    }

    /** Renvoie la liste complete des mots de la partie. */
    public ArrayList<String> getTousLesMots() {
        return tousLesMots;
    }
}
