package moteur;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 1 - NB : petit programme de test
 *  Fichier : TestGrille.java
 * ============================================================
 *
 *  "Avant d'aller plus loin, creez un petit programme de test
 *   pour afficher votre grille dans la console."
 */

import model.Grille;

public class TestGrille {

    public static void main(String[] args) {
        Grille grille = new Grille();

        // On place quelques mots pour verifier l'affichage.
        grille.placerMot("CHAT", 0, 0, 0, 1);   // horizontal
        grille.placerMot("ARBRE", 4, 0, 1, 0);  // vertical
        grille.remplirCasesVides();

        System.out.println("=== Test d'affichage de la grille ===\n");
        System.out.println(grille);
        System.out.println("Si la grille s'affiche correctement, l'Etape 1 est validee !");
    }
}
