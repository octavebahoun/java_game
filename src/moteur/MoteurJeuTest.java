package moteur;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 3 - Les tests automatiques (JUnit)
 *  Fichier : MoteurJeuTest.java
 * ============================================================
 *
 *  3 tests qui verifient automatiquement que l'algorithme :
 *    1. detecte bien un mot HORIZONTAL,
 *    2. detecte bien un mot VERTICAL,
 *    3. declenche bien une erreur si l'alignement est faux.
 *
 *  Ces tests utilisent JUnit 5 (org.junit.jupiter).
 */

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Coordonnee;
import model.Dictionnaire;
import model.Grille;
import exception.AlignementInvalideException;

public class MoteurJeuTest {

    private MoteurJeu moteur;

    /**
     * Avant chaque test on construit une grille connue :
     *   - "CHAT" en HORIZONTAL  a partir de (0,0)
     *   - "ARBRE" en VERTICAL   a partir de (2,5)
     */
    @BeforeEach
    public void initialiser() {
        Grille grille = new Grille();
        grille.placerMot("CHAT", 0, 0, 0, 1);   // (0,0)(0,1)(0,2)(0,3)
        grille.placerMot("ARBRE", 2, 5, 1, 0);  // (2,5)(3,5)(4,5)(5,5)(6,5)
        grille.remplirCasesVides();

        Dictionnaire dictionnaire = new Dictionnaire();
        dictionnaire.ajouterMot("CHAT");
        dictionnaire.ajouterMot("ARBRE");

        moteur = new MoteurJeu(grille, dictionnaire);
    }

    /** TEST 1 : un mot horizontal doit etre detecte. */
    @Test
    public void testMotHorizontal() throws Exception {
        boolean trouve = moteur.verifier("CHAT",
                new Coordonnee(0, 0), new Coordonnee(0, 3));
        assertTrue(trouve, "Le mot horizontal CHAT devrait etre trouve.");
    }

    /** TEST 2 : un mot vertical doit etre detecte. */
    @Test
    public void testMotVertical() throws Exception {
        boolean trouve = moteur.verifier("ARBRE",
                new Coordonnee(2, 5), new Coordonnee(6, 5));
        assertTrue(trouve, "Le mot vertical ARBRE devrait etre trouve.");
    }

    /** TEST 3 : un mot diagonal doit etre detecte. */
    @Test
    public void testMotDiagonal() throws Exception {
        Grille grille = new Grille();
        grille.placerMot("VENT", 2, 2, 1, 1);
        grille.remplirCasesVides();

        Dictionnaire dictionnaire = new Dictionnaire();
        dictionnaire.ajouterMot("VENT");
        moteur = new MoteurJeu(grille, dictionnaire);

        boolean trouve = moteur.verifier("VENT", new Coordonnee(2, 2), new Coordonnee(5, 5));
        assertTrue(trouve, "Le mot diagonal VENT devrait etre trouve.");
    }

    /** TEST 4 : un alignement impossible (en "L") doit lever une exception. */
    @Test
    public void testAlignementInvalide() {
        assertThrows(AlignementInvalideException.class, () -> {
            // (0,0) -> (2,3) : ni horizontal, ni vertical, ni diagonal => erreur.
            moteur.verifier("CHAT", new Coordonnee(0, 0), new Coordonnee(2, 3));
        }, "Un deplacement en L devrait lever AlignementInvalideException.");
    }

    /** Test complementaire : un mauvais mot ne doit pas etre valide. */
    @Test
    public void testMotInexistant() throws Exception {
        boolean trouve = moteur.verifier("CHAT",
                new Coordonnee(2, 5), new Coordonnee(6, 5)); // ici c'est ARBRE
        assertFalse(trouve, "CHAT ne devrait pas etre trouve a la place de ARBRE.");
    }
}
