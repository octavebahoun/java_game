package exception;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 3 - Securiser le jeu (package exception)
 *  Fichier : AlignementInvalideException.java
 * ============================================================
 *
 *  Erreur levee quand le deplacement entre la case de debut
 *  et la case de fin n'est pas une ligne droite valide
 *  (ex : un deplacement en "L" comme au echecs).
 */

/** Exception levee pour un alignement impossible. */
public class AlignementInvalideException extends Exception {

    private static final long serialVersionUID = 1L;

    public AlignementInvalideException(String message) {
        super(message);
    }
}
