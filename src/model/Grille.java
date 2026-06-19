package model;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 1 - Les Donnees (package model)
 *  Fichier : Grille.java
 * ============================================================
 *
 *  Contient un tableau de caracteres char[][] de 10x10.
 *  Possede OBLIGATOIREMENT une methode public String toString()
 *  qui renvoie la grille sous forme de texte.
 */

import java.util.Random;

/** La grille de jeu : un quadrillage 10x10 de lettres. */
public class Grille {

    /** Taille fixe de la grille (10 lignes / 10 colonnes). */
    public static final int TAILLE = 10;

    /** Le tableau de caracteres de la grille. */
    private char[][] cases;

    /** Cree une grille vide de 10x10. */
    public Grille() {
        cases = new char[TAILLE][TAILLE];
    }

    /** Renvoie la lettre situee a une ligne / colonne donnee. */
    public char getCase(int ligne, int colonne) {
        return cases[ligne][colonne];
    }

    /** Place une lettre a une ligne / colonne donnee. */
    public void setCase(int ligne, int colonne, char lettre) {
        cases[ligne][colonne] = lettre;
    }

    /** Renvoie la taille de la grille. */
    public int getTaille() {
        return TAILLE;
    }

    /**
     * Tente de placer un mot dans la grille a partir de (ligne, colonne),
     * dans la direction (dLigne, dColonne).
     *   - horizontal  : dLigne=0,  dColonne=1
     *   - vertical    : dLigne=1,  dColonne=0
     *   - diagonal    : dLigne=1,  dColonne=1
     * Renvoie true si le mot a pu etre place, false sinon.
     */
    public boolean placerMot(String mot, int ligne, int colonne, int dLigne, int dColonne) {
        mot = mot.toUpperCase();

        // 1) On verifie que le mot tient dans la grille.
        int finLigne = ligne + dLigne * (mot.length() - 1);
        int finColonne = colonne + dColonne * (mot.length() - 1);
        if (finLigne < 0 || finLigne >= TAILLE || finColonne < 0 || finColonne >= TAILLE) {
            return false;
        }

        // 2) On verifie qu'on ne casse pas un mot deja place.
        int l = ligne, c = colonne;
        for (int i = 0; i < mot.length(); i++) {
            if (cases[l][c] != '\0' && cases[l][c] != mot.charAt(i)) {
                return false;
            }
            l += dLigne;
            c += dColonne;
        }

        // 3) Tout est bon : on ecrit le mot.
        l = ligne;
        c = colonne;
        for (int i = 0; i < mot.length(); i++) {
            cases[l][c] = mot.charAt(i);
            l += dLigne;
            c += dColonne;
        }
        return true;
    }

    /** Remplit toutes les cases encore vides avec des lettres aleatoires. */
    public void remplirCasesVides() {
        Random random = new Random();
        for (int l = 0; l < TAILLE; l++) {
            for (int c = 0; c < TAILLE; c++) {
                if (cases[l][c] == '\0') {
                    cases[l][c] = (char) ('A' + random.nextInt(26));
                }
            }
        }
    }

    /**
     * Renvoie la grille sous forme de texte, avec les numeros de
     * lignes et de colonnes pour aider le joueur a se reperer.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // En-tete : numeros des colonnes
        sb.append("     ");
        for (int c = 0; c < TAILLE; c++) {
            sb.append(c).append("  ");
        }
        sb.append("\n");

        // Ligne de separation
        sb.append("   +");
        for (int c = 0; c < TAILLE; c++) {
            sb.append("---");
        }
        sb.append("\n");

        // Les lignes de la grille, precedees de leur numero
        for (int l = 0; l < TAILLE; l++) {
            sb.append(String.format("%2d | ", l));
            for (int c = 0; c < TAILLE; c++) {
                char lettre = cases[l][c];
                sb.append(lettre == '\0' ? '.' : lettre).append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
