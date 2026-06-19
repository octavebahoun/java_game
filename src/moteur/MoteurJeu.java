package moteur;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 2 - Le moteur du jeu (package moteur)
 *  Fichier : MoteurJeu.java
 * ============================================================
 *
 *  Fichier CENTRAL. Il contient :
 *    - une Grille
 *    - un Dictionnaire
 *    - un score (entier)
 *  C'est ici qu'on ecrit l'algorithme qui verifie si le mot
 *  tape par le joueur est bien present dans la grille, entre
 *  la case de debut et la case de fin.
 */

import java.util.ArrayList;

import model.Coordonnee;
import model.Dictionnaire;
import model.Grille;
import exception.AlignementInvalideException;
import exception.CoordonneesInvalidesException;

public class MoteurJeu {

    private Grille grille;
    private Dictionnaire dictionnaire;
    private int score;

    /** Les cases du dernier mot correctement trouve (utile pour l'affichage). */
    private ArrayList<Coordonnee> dernierChemin;

    /**
     * Constructeur par defaut : prepare une partie avec mots aleatoires.
     */
    public MoteurJeu() {
        this.grille = new Grille();
        this.dictionnaire = new Dictionnaire();
        this.score = 0;
        this.dernierChemin = new ArrayList<Coordonnee>();
        preparerPartieAleatoire();
    }

    /**
     * Constructeur utilise notamment par les tests automatiques :
     * on fournit nous-memes une grille et un dictionnaire.
     */
    public MoteurJeu(Grille grille, Dictionnaire dictionnaire) {
        this.grille = grille;
        this.dictionnaire = dictionnaire;
        this.score = 0;
        this.dernierChemin = new ArrayList<Coordonnee>();
    }

    /**
     * Constructeur pour mode prédéfini (deprecated - garder pour compatibilité tests).
     * Si modeAleatoire = false, utilise les mots predéfinis.
     */
    public MoteurJeu(boolean modeAleatoire) {
        this.grille = new Grille();
        this.dictionnaire = new Dictionnaire();
        this.score = 0;
        this.dernierChemin = new ArrayList<Coordonnee>();
        if (!modeAleatoire) {
            preparerPartie();
        } else {
            preparerPartieAleatoire();
        }
    }

    /** Place les mots de depart dans la grille et complete le reste. */
    private void preparerPartie() {
        // Chaque ligne : mot, ligne, colonne, dLigne, dColonne
    	    placerEtEnregistrer("CHIEN",   0, 0, 0, 1);  // horizontal
    	    placerEtEnregistrer("PYTHON",  2, 0, 0, 1);  // horizontal
    	    placerEtEnregistrer("NUAGE",   9, 0, 0, 1);  // horizontal
    	    placerEtEnregistrer("ECOLE",   0, 9, 1, 0);  // vertical
    	    placerEtEnregistrer("TIGRE",   4, 0, 1, 0);  // vertical
    	    placerEtEnregistrer("ROSE",    1, 5, 0, 1);  // horizontal
    	    placerEtEnregistrer("LAMPE",   3, 7, 1, 0);  // vertical  
    	    placerEtEnregistrer("PLUIE",   6, 2, 0, 1);  // horizontal 
    	    placerEtEnregistrer("NUIT",    5, 6, 1, 0);  // vertical  
    	    placerEtEnregistrer("VENT",    2, 5, 1, 1);  // diagonal
    	    
        grille.remplirCasesVides();
    }

    /** Prepare une partie avec mots aleatoires : en selectionne 10 et les place aleatoirement. */
    private void preparerPartieAleatoire() {
        java.util.Random random = new java.util.Random();
        dictionnaire.remplirAvecMotsAleatoires(10);
        
        // Essaie de placer les mots aleatoires a des positions aleatoires
        for (String mot : dictionnaire.getTousLesMots()) {
            boolean place = false;
            int tentatives = 0;
            while (!place && tentatives < 20) {
                int ligne = random.nextInt(Grille.TAILLE);
                int col = random.nextInt(Grille.TAILLE);
                int direction = random.nextInt(2); // 0: H, 1: V
                int dL = (direction == 1) ? 1 : 0;
                int dC = (direction == 0) ? 1 : 0;
                
                if (grille.placerMot(mot, ligne, col, dL, dC)) {
                    place = true;
                }
                tentatives++;
            }
        }
        grille.remplirCasesVides();
    }

    /** Place un mot dans la grille et, si ca reussit, l'ajoute au dictionnaire. */
    private void placerEtEnregistrer(String mot, int ligne, int col, int dL, int dC) {
        if (grille.placerMot(mot, ligne, col, dL, dC)) {
            dictionnaire.ajouterMot(mot);
        }
    }

    /**
     * Verifie qu'une coordonnee est bien a l'interieur de la grille.
     * @throws CoordonneesInvalidesException si elle est en dehors.
     */
    private void verifierDansGrille(Coordonnee co) throws CoordonneesInvalidesException {
        int max = Grille.TAILLE - 1;
        if (co.getLigne() < 0 || co.getLigne() > max
                || co.getColonne() < 0 || co.getColonne() > max) {
            throw new CoordonneesInvalidesException(
                    "La coordonnee " + co + " est hors de la grille (valeurs autorisees : 0 a " + max + ").");
        }
    }

    /**
     * ALGORITHME PRINCIPAL.
     * Verifie si le mot saisi se trouve dans la grille entre les
     * cases 'debut' et 'fin'. Le mot peut etre horizontal, vertical ou diagonal,
     * et peut etre lu dans les deux sens.
     *
     * @return true si le mot est trouve (et present dans le dictionnaire),
     *         false sinon.
     * @throws CoordonneesInvalidesException si une case est hors grille.
     * @throws AlignementInvalideException   si le trajet n'est pas une droite valide.
     */
    public boolean verifier(String mot, Coordonnee debut, Coordonnee fin)
            throws CoordonneesInvalidesException, AlignementInvalideException {

        // 1) Les deux coordonnees doivent etre dans la grille.
        verifierDansGrille(debut);
        verifierDansGrille(fin);

        // 2) Le trajet debut -> fin doit etre une ligne droite.
        int distLigne = Math.abs(fin.getLigne() - debut.getLigne());
        int distColonne = Math.abs(fin.getColonne() - debut.getColonne());

        boolean horizontal = (distLigne == 0 && distColonne != 0);
        boolean vertical   = (distColonne == 0 && distLigne != 0);
        boolean diagonal   = (distLigne == distColonne && distLigne != 0);

        if (!horizontal && !vertical && !diagonal) {
            throw new AlignementInvalideException(
                    "Le deplacement de " + debut + " vers " + fin
                    + " n'est pas valide : il faut une ligne horizontale, verticale ou diagonale.");
        }

        // 3) On lit les lettres de debut jusqu'a fin.
        int pasLigne = Integer.compare(fin.getLigne(), debut.getLigne());      // -1, 0 ou +1
        int pasColonne = Integer.compare(fin.getColonne(), debut.getColonne());
        int longueur = Math.max(distLigne, distColonne) + 1;

        StringBuilder lecture = new StringBuilder();
        ArrayList<Coordonnee> chemin = new ArrayList<Coordonnee>();

        int l = debut.getLigne();
        int c = debut.getColonne();
        for (int i = 0; i < longueur; i++) {
            lecture.append(grille.getCase(l, c));
            chemin.add(new Coordonnee(l, c));
            l += pasLigne;
            c += pasColonne;
        }

        String motLu = lecture.toString();
        String motLuInverse = lecture.reverse().toString();
        String motCherche = mot.toUpperCase();

        boolean correspond = motLu.equalsIgnoreCase(motCherche)
                || motLuInverse.equalsIgnoreCase(motCherche);

        // 4) Le mot doit correspondre ET faire partie des mots a trouver.
        if (correspond && dictionnaire.contient(motCherche)) {
            dictionnaire.supprimer(motCherche);
            score += motCherche.length() * 10;
            dernierChemin = chemin;
            return true;   // "Gagne"
        }

        return false;      // "Perdu"
    }

    // --- Accesseurs ---
    public Grille getGrille() {
        return grille;
    }

    public Dictionnaire getDictionnaire() {
        return dictionnaire;
    }

    public int getScore() {
        return score;
    }

    /** Renvoie true si le mot a deja ete trouve pendant cette partie. */
    public boolean motDejaTrouve(String mot) {
        return dictionnaire.estDejaTrouve(mot);
    }

    /** Renvoie les cases du dernier mot trouve (pour les colorer dans l'IHM). */
    public ArrayList<Coordonnee> getDernierChemin() {
        return dernierChemin;
    }

    /** Renvoie true si tous les mots ont ete trouves. */
    public boolean partieGagnee() {
        return dictionnaire.estVide();
    }
}
