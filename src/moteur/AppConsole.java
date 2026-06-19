package moteur;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 2 - Le jeu en mode texte  (+ Etape 3 : try-catch)
 *  Fichier : AppConsole.java
 * ============================================================
 *
 *  Programme PRINCIPAL en console.
 *  Il affiche la grille, demande au joueur de taper un mot,
 *  puis la ligne/colonne de debut et de fin (avec un Scanner).
 *  Il donne ces infos au MoteurJeu qui repond "Gagne" ou "Perdu".
 *
 *  Etape 3 : tout est protege par des blocs try-catch pour ne
 *  jamais crasher (coordonnees hors grille, alignement en "L",
 *  ou lettres tapees a la place des chiffres).
 */

import java.util.InputMismatchException;
import java.util.Scanner;

import model.Coordonnee;
import exception.AlignementInvalideException;
import exception.CoordonneesInvalidesException;

public class AppConsole {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoteurJeu moteur = new MoteurJeu();

        System.out.println("==========================================");
        System.out.println("        JEU DE MOTS CACHES (console)       ");
        System.out.println("==========================================");
        System.out.println("Trouvez tous les mots caches dans la grille !");
        System.out.println("(tapez 'q' au moment du mot pour quitter)\n");

        // Boucle principale : on joue tant qu'il reste des mots a trouver.
        while (!moteur.partieGagnee()) {

            // Affichage de la grille et de l'etat de la partie.
            System.out.println(moteur.getGrille());
            System.out.println("Score : " + moteur.getScore());
            System.out.println("Mots restants : " + moteur.getDictionnaire().getMots());
            System.out.println("------------------------------------------");

            // 1) Le mot recherche.
            System.out.print("Quel mot cherchez-vous ? ");
            String mot = scanner.nextLine().trim();
            if (mot.equalsIgnoreCase("q")) {
                System.out.println("Partie abandonnee. A bientot !");
                break;
            }
            if (mot.isEmpty()) {
                System.out.println(">> Veuillez taper un mot.\n");
                continue;
            }

            try {
                // 2) Les coordonnees de debut et de fin (lues comme des entiers).
                int ligneDebut   = lireEntier(scanner, "Ligne de DEBUT   : ");
                int colonneDebut = lireEntier(scanner, "Colonne de DEBUT : ");
                int ligneFin     = lireEntier(scanner, "Ligne de FIN     : ");
                int colonneFin   = lireEntier(scanner, "Colonne de FIN   : ");

                Coordonnee debut = new Coordonnee(ligneDebut, colonneDebut);
                Coordonnee fin   = new Coordonnee(ligneFin, colonneFin);

                // 3) On interroge le moteur.
                boolean trouve = moteur.verifier(mot, debut, fin);

                if (trouve) {
                    System.out.println("\n>>> GAGNE ! Le mot \"" + mot.toUpperCase() + "\" a ete trouve.\n");
                } else {
                    System.out.println("\n>>> PERDU. Ce n'est pas le bon mot a cet endroit.\n");
                }

            } catch (CoordonneesInvalidesException e) {
                // Coordonnee hors de la grille.
                System.out.println("\n>> Erreur : " + e.getMessage());
                System.out.println(">> Veuillez retaper votre saisie.\n");

            } catch (AlignementInvalideException e) {
                // Deplacement impossible (ex : en "L").
                System.out.println("\n>> Erreur : " + e.getMessage());
                System.out.println(">> Veuillez retaper votre saisie.\n");

            } catch (NumberFormatException e) {
                // Lettres tapees a la place des chiffres.
                System.out.println("\n>> Saisie incorrecte : veuillez entrer des CHIFFRES pour les coordonnees.\n");
            }
        }

        if (moteur.partieGagnee()) {
            System.out.println(moteur.getGrille());
            System.out.println("**********************************************");
            System.out.println("  BRAVO ! Tous les mots ont ete trouves.");
            System.out.println("  Score final : " + moteur.getScore());
            System.out.println("**********************************************");
        }

        scanner.close();
    }

    /**
     * Lit un entier au clavier. Si le joueur tape autre chose que
     * des chiffres, on leve une NumberFormatException qui sera
     * attrapee plus haut ("Saisie incorrecte").
     */
    private static int lireEntier(Scanner scanner, String message) {
        System.out.print(message);
        String saisie = scanner.nextLine().trim();
        try {
            return Integer.parseInt(saisie);
        } catch (NumberFormatException e) {
            // On relance pour la traiter au niveau de la boucle de jeu.
            throw new NumberFormatException(saisie);
        } catch (InputMismatchException e) {
            throw new NumberFormatException(saisie);
        }
    }
}
