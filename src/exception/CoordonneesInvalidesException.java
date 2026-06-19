package exception;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 3 - Securiser le jeu (package exception)
 *  Fichier : CoordonneesInvalidesException.java
 * ============================================================
 *
 *  Erreur levee quand le joueur tape une coordonnee qui se
 *  trouve EN DEHORS de la grille (ex : ligne 15).
 */

/** Exception levee pour une coordonnee hors de la grille. */
public class CoordonneesInvalidesException extends Exception {

    private static final long serialVersionUID = 1L;

    public CoordonneesInvalidesException(String message) {
        super(message);
    }
}
