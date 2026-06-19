package model;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 1 - Les Donnees (package model)
 *  Fichier : Coordonnee.java
 * ============================================================
 *
 *  Un outil simple pour stocker deux chiffres :
 *  une LIGNE et une COLONNE.
 */

/** Represente une position dans la grille : un couple (ligne, colonne). */
public class Coordonnee {

    private int ligne;
    private int colonne;

    /** Construit une coordonnee a partir d'une ligne et d'une colonne. */
    public Coordonnee(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }

    // --- Accesseurs (getters) ---
    public int getLigne() {
        return ligne;
    }

    public int getColonne() {
        return colonne;
    }

    // --- Modificateurs (setters) ---
    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    /** Affichage lisible d'une coordonnee, ex : (3, 5). */
    @Override
    public String toString() {
        return "(" + ligne + ", " + colonne + ")";
    }
}
