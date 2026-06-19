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

    // --- Suivi du temps pour le combo ---
    private long dernierTempsTrouve = 0;
    private int comboActuel = 1;

    /** Les cases du dernier mot correctement trouve (utile pour l'affichage). */
    private ArrayList<Coordonnee> dernierChemin;

    private int difficulteActuelle = 2; // 1: Facile, 2: Moyen, 3: Difficile

    /**
     * Constructeur par defaut : prepare une partie avec mots aleatoires et difficulté Moyenne.
     */
    public MoteurJeu() {
        this(2);
    }

    /**
     * Constructeur avec choix de la difficulté.
     */
    public MoteurJeu(int difficulte) {
        this.grille = new Grille();
        this.dictionnaire = new Dictionnaire();
        this.score = 0;
        this.difficulteActuelle = difficulte;
        this.dernierChemin = new ArrayList<Coordonnee>();
        preparerPartieAleatoire(difficulte);
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
            preparerPartieAleatoire(difficulteActuelle);
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

    /** Prepare une partie avec mots aleatoires : selectionne selon la difficulte et garantit le placement. */
    private void preparerPartieAleatoire(int difficulte) {
        int nbMotsVoulus = 6; // Facile
        if (difficulte == 2) nbMotsVoulus = 10; // Moyen
        if (difficulte == 3) nbMotsVoulus = 15; // Difficile

        java.util.Random random = new java.util.Random();
        ArrayList<String> candidats = Dictionnaire.genererMotsAleatoires(100);
        
        for (String mot : candidats) {
            if (dictionnaire.getNombreTotal() >= nbMotsVoulus) {
                break;
            }
            
            boolean place = false;
            int tentatives = 0;
            while (!place && tentatives < 50) {
                int ligne = random.nextInt(Grille.TAILLE);
                int col = random.nextInt(Grille.TAILLE);
                
                int dL = 0, dC = 0;
                if (difficulte == 1) {
                    // Facile : H (0,1) ou V (1,0)
                    int direction = random.nextInt(2);
                    dL = (direction == 1) ? 1 : 0;
                    dC = (direction == 0) ? 1 : 0;
                } else if (difficulte == 2) {
                    // Moyen : H, V, Diagonale bas-droite
                    int dir = random.nextInt(3);
                    dL = (dir == 0) ? 0 : (dir == 1) ? 1 : 1;
                    dC = (dir == 0) ? 1 : (dir == 1) ? 0 : 1;
                } else {
                    // Difficile : Toutes directions
                    dL = random.nextInt(3) - 1;
                    dC = random.nextInt(3) - 1;
                }
                
                if (dL != 0 || dC != 0) {
                    if (grille.placerMot(mot, ligne, col, dL, dC)) {
                        dictionnaire.ajouterMot(mot);
                        place = true;
                    }
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
            
            long tempsActuel = System.currentTimeMillis();
            if (dernierTempsTrouve > 0 && (tempsActuel - dernierTempsTrouve) < 60000) {
                comboActuel++;
            } else {
                comboActuel = 1;
            }
            dernierTempsTrouve = tempsActuel;
            
            score += motCherche.length() * 10 * comboActuel;
            dernierChemin = chemin;
            return true;   // "Gagne"
        }

        return false;      // "Perdu"
    }

    public int getComboActuel() {
        return comboActuel;
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
