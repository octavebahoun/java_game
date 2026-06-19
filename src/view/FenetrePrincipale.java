package view;

/*
 * ============================================================
 *  PROJET : Jeu de Mots CachES
 *  Etape 4 - L'interface graphique (package view)
 *  Fichier : FenetrePrincipale.java
 * ============================================================
 *
 *  Une fenetre (JFrame) qui s'ouvre a l'ecran.
 *    - la grille graphique : un JPanel en GridLayout(10,10)
 *      affichant les 100 lettres dans des cases visuelles ;
 *    - des champs de texte (JTextField) pour le mot et les
 *      coordonnees, et un bouton JButton "Valider" ;
 *    - au clic, l'IHM envoie les donnees au MoteurJeu :
 *        * si le mot est bon -> l'affichage se met a jour ;
 *        * si une exception se declenche -> un JOptionPane
 *          (fenetre pop-up) previent le joueur.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import javax.swing.JComboBox;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.Coordonnee;
import model.Grille;
import moteur.MoteurJeu;
import exception.AlignementInvalideException;
import exception.CoordonneesInvalidesException;

public class FenetrePrincipale extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Palette de couleurs ---
    private static final Color FOND        = new Color(0x1E, 0x29, 0x3B);
    private static final Color CASE_FOND   = new Color(0xF5, 0xF1, 0xE6);
    private static final Color CASE_TROUVE = new Color(0x7C, 0xC5, 0x76);
    private static final Color ACCENT      = new Color(0xF2, 0x9E, 0x4B);
    private static final Color TEXTE_CLAIR = new Color(0xEC, 0xEF, 0xF4);

    // --- Le moteur du jeu (cree a l'etape 2) ---
    private MoteurJeu moteur;

    // --- Les 100 cases de la grille ---
    private final JLabel[][] cases = new JLabel[Grille.TAILLE][Grille.TAILLE];

    // --- Suivi de la selection a la souris ---
    private Coordonnee coordSelectionnee = null;
    private Color couleurCaseSelectionnee = null;
    
    // --- Les champs de saisie ---
    private final JTextField champMot      = new JTextField(8);
    private final JTextField champLigneDeb = new JTextField(2);
    private final JTextField champColDeb   = new JTextField(2);
    private final JTextField champLigneFin = new JTextField(2);
    private final JTextField champColFin   = new JTextField(2);

    // --- Sélection du thème ---
    private final JComboBox<String> comboThemes = new JComboBox<String>(new String[]{
        "Par defaut", "Animaux", "Informatique"
    });

    // --- Affichage de l'état de la partie ---
    private final JLabel labelScore    = new JLabel();
    private final JLabel labelRestants = new JLabel();
    private final JLabel labelTimer    = new JLabel();

    // --- Minuteur ---
    private int tempsEcouleSecondes = 0;
    private javax.swing.Timer minuteur;

    public FenetrePrincipale() {
        super("Jeu de Mots Caches");
        this.moteur = new MoteurJeu();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(FOND);

        add(construireTitre(), BorderLayout.NORTH);
        add(construireGrille(), BorderLayout.CENTER);
        add(construireCommandes(), BorderLayout.SOUTH);

        rafraichirGrille();
        rafraichirEtat();

        // Initialisation du minuteur
        minuteur = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempsEcouleSecondes++;
                mettreAJourTimer();
            }
        });
        minuteur.start();

        pack();
        setMinimumSize(new Dimension(560, 640));
        setLocationRelativeTo(null); // centre la fenetre a l'ecran
    }

    /** Bandeau de titre en haut. */
    private JPanel construireTitre() {
        JPanel panneau = new JPanel(new BorderLayout());
        panneau.setBackground(FOND);
        panneau.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel titre = new JLabel("JEU DE MOTS CACHES", SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 24));
        titre.setForeground(ACCENT);
        panneau.add(titre, BorderLayout.CENTER);
        return panneau;
    }

    /** La grille graphique : JPanel + GridLayout(10,10). */
    private JPanel construireGrille() {
        JPanel panneau = new JPanel(new GridLayout(Grille.TAILLE, Grille.TAILLE, 3, 3));
        panneau.setBackground(FOND);
        panneau.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int l = 0; l < Grille.TAILLE; l++) {
            for (int c = 0; c < Grille.TAILLE; c++) {
                JLabel caseLettre = new JLabel("", SwingConstants.CENTER);
                caseLettre.setOpaque(true);
                caseLettre.setBackground(CASE_FOND);
                caseLettre.setForeground(FOND);
                caseLettre.setFont(new Font("Monospaced", Font.BOLD, 20));
                caseLettre.setPreferredSize(new Dimension(40, 40));
                caseLettre.setBorder(BorderFactory.createLineBorder(new Color(0xD8, 0xCF, 0xB8)));
                cases[l][c] = caseLettre;

                final int ligneActuelle = l;
                final int colonneActuelle = c;
                caseLettre.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        gererClicCase(ligneActuelle, colonneActuelle);
                    }
                });

                panneau.add(caseLettre);
            }
        }
        return panneau;
    }

    /** La zone du bas : champs de saisie, bouton, et etat de la partie. */
    private JPanel construireCommandes() {
        JPanel panneau = new JPanel(new BorderLayout(8, 8));
        panneau.setBackground(FOND);
        panneau.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        // -- Ligne de saisie --
        JPanel saisie = new JPanel();
        saisie.setBackground(FOND);
        saisie.add(etiquette("Mot :"));
        saisie.add(champMot);
        saisie.add(etiquette("  Debut (L,C) :"));
        saisie.add(champLigneDeb);
        saisie.add(champColDeb);
        saisie.add(etiquette("  Fin (L,C) :"));
        saisie.add(champLigneFin);
        saisie.add(champColFin);
        
        saisie.add(etiquette("  Theme :"));
        saisie.add(comboThemes);

        JButton boutonValider = new JButton("Valider");
        boutonValider.setBackground(ACCENT);
        boutonValider.setForeground(FOND);
        boutonValider.setFont(new Font("SansSerif", Font.BOLD, 14));
        boutonValider.setFocusPainted(false);
        boutonValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onValider();
            }
        });
        saisie.add(boutonValider);
        
        JButton boutonRejouer = new JButton("Rejouer");
        boutonRejouer.setBackground(new Color(0x4C, 0xAF, 0x50)); // vert
        boutonRejouer.setForeground(FOND);
        boutonRejouer.setFont(new Font("SansSerif", Font.BOLD, 14));
        boutonRejouer.setFocusPainted(false);
        boutonRejouer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRejouer();
            }
        });
        saisie.add(boutonRejouer);

        JButton boutonQuitter = new JButton("Quitter");
        boutonQuitter.setBackground(new Color(0xF2, 0x4C, 0x4C)); // rouge
        boutonQuitter.setForeground(FOND);
        boutonQuitter.setFont(new Font("SansSerif", Font.BOLD, 14));
        boutonQuitter.setFocusPainted(false);
        boutonQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        saisie.add(boutonQuitter);

        // -- Ligne d'etat (score + mots restants) --
        JPanel ligneScoreTimer = new JPanel(new BorderLayout());
        ligneScoreTimer.setBackground(FOND);
        labelScore.setForeground(TEXTE_CLAIR);
        labelScore.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelTimer.setForeground(TEXTE_CLAIR);
        labelTimer.setFont(new Font("SansSerif", Font.BOLD, 14));
        mettreAJourTimer();
        ligneScoreTimer.add(labelScore, BorderLayout.WEST);
        ligneScoreTimer.add(labelTimer, BorderLayout.EAST);

        JPanel etat = new JPanel(new GridLayout(2, 1));
        etat.setBackground(FOND);
        labelRestants.setForeground(TEXTE_CLAIR);
        labelRestants.setFont(new Font("SansSerif", Font.PLAIN, 13));
        etat.add(ligneScoreTimer);
        etat.add(labelRestants);

        panneau.add(saisie, BorderLayout.CENTER);
        panneau.add(etat, BorderLayout.SOUTH);
        return panneau;
    }

    private JLabel etiquette(String texte) {
        JLabel l = new JLabel(texte);
        l.setForeground(TEXTE_CLAIR);
        return l;
    }

    /**
     * Action declenchee au clic sur "Valider".
     * Recupere la saisie, interroge le MoteurJeu, et :
     *   - met a jour l'affichage si le mot est bon ;
     *   - affiche un JOptionPane si une exception se declenche.
     */
    
    private void onValider() {
        String mot = champMot.getText().trim();
        if (mot.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez taper un mot a rechercher.",
                    "Saisie incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (moteur.motDejaTrouve(mot)) {
            JOptionPane.showMessageDialog(this,
                    "Ce mot a deja ete trouve.",
                    "Mot deja trouve", JOptionPane.INFORMATION_MESSAGE);
            champMot.setText("");
            return;
        }

        try {
            // Conversion des champs texte en chiffres.
            int ligneDeb = Integer.parseInt(champLigneDeb.getText().trim());
            int colDeb   = Integer.parseInt(champColDeb.getText().trim());
            int ligneFin = Integer.parseInt(champLigneFin.getText().trim());
            int colFin   = Integer.parseInt(champColFin.getText().trim());

            Coordonnee debut = new Coordonnee(ligneDeb, colDeb);
            Coordonnee fin   = new Coordonnee(ligneFin, colFin);

            boolean trouve = moteur.verifier(mot, debut, fin);

            if (trouve) {
                colorerChemin();
                rafraichirEtat();
                champMot.setText("");
                if (moteur.partieGagnee()) {
                    minuteur.stop();
                    JOptionPane.showMessageDialog(this,
                            "BRAVO ! Tous les mots ont ete trouves.\nScore final : " + moteur.getScore() + "\nTemps mis : " + String.format("%02d:%02d", tempsEcouleSecondes / 60, tempsEcouleSecondes % 60),
                            "Victoire", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ce n'est pas le bon mot a cet endroit. Reessayez !",
                        "Perdu", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            // Lettres a la place des chiffres.
            JOptionPane.showMessageDialog(this,
                    "Saisie incorrecte : les coordonnees doivent etre des CHIFFRES.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);

        } catch (CoordonneesInvalidesException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Coordonnees invalides", JOptionPane.ERROR_MESSAGE);

        } catch (AlignementInvalideException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Alignement invalide", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onRejouer() {
        // 1. Charger le thème sélectionné et réinitialiser le moteur
        appliquerThemeSelectionne();
        moteur = new MoteurJeu();
        coordSelectionnee = null;
        couleurCaseSelectionnee = null;
        
        // Réinitialiser le minuteur
        tempsEcouleSecondes = 0;
        mettreAJourTimer();
        minuteur.restart();

        // 2. Réinitialiser les couleurs et lettres de la grille graphique
        Grille grille = moteur.getGrille();
        for (int l = 0; l < Grille.TAILLE; l++) {
            for (int c = 0; c < Grille.TAILLE; c++) {
                cases[l][c].setText(String.valueOf(grille.getCase(l, c)));
                cases[l][c].setBackground(CASE_FOND); // remet la couleur d'origine
            }
        }

        // 3. Vider les champs de saisie
        champMot.setText("");
        champLigneDeb.setText("");
        champColDeb.setText("");
        champLigneFin.setText("");
        champColFin.setText("");

        // 4. Mettre à jour score et mots restants
        rafraichirEtat();
    }

    /** Gère le clic sur une case de la grille pour la sélection par souris. */
    private void gererClicCase(int l, int c) {
        if (coordSelectionnee == null) {
            // Premier clic : sélection du début
            coordSelectionnee = new Coordonnee(l, c);
            couleurCaseSelectionnee = cases[l][c].getBackground();
            cases[l][c].setBackground(new Color(0xFD, 0xBA, 0x74)); // Orange clair

            champLigneDeb.setText(String.valueOf(l));
            champColDeb.setText(String.valueOf(c));
            champLigneFin.setText("");
            champColFin.setText("");
        } else {
            // Second clic : sélection de la fin et validation
            Coordonnee debut = coordSelectionnee;
            
            // Rétablir la couleur d'origine de la première case
            if (couleurCaseSelectionnee != null) {
                cases[debut.getLigne()][debut.getColonne()].setBackground(couleurCaseSelectionnee);
            }

            champLigneFin.setText(String.valueOf(l));
            champColFin.setText(String.valueOf(c));

            onValider();
            
            coordSelectionnee = null;
            couleurCaseSelectionnee = null;
        }
    }

    /** Recopie les lettres du moteur dans les cases graphiques. */
    private void rafraichirGrille() {
        Grille grille = moteur.getGrille();
        for (int l = 0; l < Grille.TAILLE; l++) {
            for (int c = 0; c < Grille.TAILLE; c++) {
                cases[l][c].setText(String.valueOf(grille.getCase(l, c)));
            }
        }
    }

    /** Colore en vert les cases du dernier mot trouve. */
    private void colorerChemin() {
        for (Coordonnee co : moteur.getDernierChemin()) {
            cases[co.getLigne()][co.getColonne()].setBackground(CASE_TROUVE);
        }
    }

    /** Met a jour le score et la liste des mots restants. */
    private void rafraichirEtat() {
        int trouves = moteur.getDictionnaire().getNombreTrouves();
        int total = moteur.getDictionnaire().getNombreTotal();
        int combo = moteur.getComboActuel();
        String comboTexte = (combo > 1) ? "    |    🔥 COMBO x" + combo + " !" : "";
 
        labelScore.setText("Score : " + moteur.getScore() + "    |    Mots trouves : " + trouves + " / " + total + comboTexte);
        labelRestants.setText(construireListeMots());
    }

    /** Applique le thème sélectionné dans la JComboBox. */
    private void appliquerThemeSelectionne() {
        String theme = (String) comboThemes.getSelectedItem();
        Path chemin;
        if ("Animaux".equals(theme)) {
            chemin = Paths.get("data", "animaux.txt");
        } else if ("Informatique".equals(theme)) {
            chemin = Paths.get("data", "informatique.txt");
        } else {
            chemin = Paths.get("dictionnaire.txt");
        }
        model.Dictionnaire.changerTheme(chemin);
    }

    /** Met à jour l'affichage du minuteur. */
    private void mettreAJourTimer() {
        int minutes = tempsEcouleSecondes / 60;
        int secondes = tempsEcouleSecondes % 60;
        labelTimer.setText(String.format("Temps : %02d:%02d", minutes, secondes));
    }

    /** Construit une liste HTML avec les mots trouves barres. */
    private String construireListeMots() {
        StringBuilder sb = new StringBuilder("<html>Mots : ");
        for (String mot : moteur.getDictionnaire().getTousLesMots()) {
            if (moteur.getDictionnaire().estDejaTrouve(mot)) {
                sb.append("<span style='color:#AAB2BF'><s>")
                        .append(mot)
                        .append("</s></span>");
            } else {
                sb.append("<b>").append(mot).append("</b>");
            }
            sb.append("&nbsp;&nbsp;");
        }
        sb.append("</html>");
        return sb.toString();
    }

    /** Point d'entree : ouvre la fenetre. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {		
            @Override
            public void run() {
                new FenetrePrincipale().setVisible(true);
            }
        });
    }
}
