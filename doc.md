# Documentation : Jeu de Mots Cachés (Java)

Ce document fournit une explication exhaustive de l'architecture, du code source et des fonctionnalités du projet **Jeu de Mots Cachés**, enrichie d'extraits de code Java.

---

## 1. Vue d'Ensemble & Architecture

Le projet est structuré en **4 packages** principaux (conception proche du modèle MVC - Modèle-Vue-Contrôleur) afin de séparer les données, la logique métier et l'interface utilisateur.

```
src/
├── model/        <-- Les Données (Grille, Coordonnées, Dictionnaire)
├── exception/    <-- La Sécurité (Exceptions personnalisées pour les erreurs)
├── moteur/        <-- La Logique (Calculs de score, validation, boucle console)
└── view/         <-- L'Interface (Fenêtre graphique Swing)
```

---

## 2. Analyse Détaillée des Packages et Classes

### 📦 1. Le Package `model` (Les Données)

#### 📄 `Coordonnee.java`
Représente un couple `(ligne, colonne)` dans la grille. Redéfinit `equals` et `hashCode` pour pouvoir comparer deux coordonnées par leurs valeurs.

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Coordonnee autre = (Coordonnee) obj;
    return ligne == autre.ligne && colonne == autre.colonne;
}
```

#### 📄 `Dictionnaire.java`
Gère la liste des mots chargés. Propose le chargement dynamique du fichier correspondant au thème choisi.

```java
public static boolean changerTheme(java.nio.file.Path chemin) {
    ArrayList<String> backup = new ArrayList<String>(MOTS_ETENDUS);
    MOTS_ETENDUS.clear();
    if (chargerMotsDepuisFichier(chemin)) {
        return true;
    } else {
        // En cas d'échec, on restaure le thème précédent
        MOTS_ETENDUS.addAll(backup);
        return false;
    }
}
```

#### 📄 `Grille.java`
Modélise la grille bidimensionnelle ($10 \times 10$ lettres) et gère le placement initial des mots.

```java
public boolean placerMot(String mot, int ligne, int colonne, int dLigne, int dColonne) {
    // 1) Vérification que le mot rentre dans les limites de la grille
    int finLigne = ligne + dLigne * (mot.length() - 1);
    int finColonne = colonne + dColonne * (mot.length() - 1);
    if (finLigne < 0 || finLigne >= TAILLE || finColonne < 0 || finColonne >= TAILLE) {
        return false;
    }

    // 2) Vérification de la compatibilité avec les lettres déjà présentes
    int l = ligne, c = colonne;
    for (int i = 0; i < mot.length(); i++) {
        if (cases[l][c] != '\0' && cases[l][c] != mot.charAt(i)) {
            return false;
        }
        l += dLigne; c += dColonne;
    }

    // 3) Placement du mot
    l = ligne; c = colonne;
    for (int i = 0; i < mot.length(); i++) {
        cases[l][c] = mot.charAt(i);
        l += dLigne; c += dColonne;
    }
    return true;
}
```

---

### 📦 2. Le Package `exception` (La Sécurité)

Ces classes héritent de `Exception`. Elles permettent d'intercepter les erreurs de saisie (hors limites de grille ou alignement invalide) et d'afficher des fenêtres de dialogue (pop-ups) explicatives à l'utilisateur sans arrêter le programme.

```java
public class AlignementInvalideException extends Exception {
    private static final long serialVersionUID = 1L;
    public AlignementInvalideException(String message) {
        super(message);
    }
}
```

---

### 📦 3. Le Package `moteur` (La Logique de Jeu)

#### 📄 `MoteurJeu.java`
Contient l'algorithme principal de vérification des mots, de calcul du score et du multiplicateur de combo.

```java
public boolean verifier(String mot, Coordonnee debut, Coordonnee fin)
        throws CoordonneesInvalidesException, AlignementInvalideException {

    verifierDansGrille(debut);
    verifierDansGrille(fin);

    int distLigne = Math.abs(fin.getLigne() - debut.getLigne());
    int distColonne = Math.abs(fin.getColonne() - debut.getColonne());

    // Vérifier l'alignement rectiligne
    boolean horizontal = (distLigne == 0 && distColonne != 0);
    boolean vertical   = (distColonne == 0 && distLigne != 0);
    boolean diagonal   = (distLigne == distColonne && distLigne != 0);

    if (!horizontal && !vertical && !diagonal) {
        throw new AlignementInvalideException("Déplacement non rectiligne.");
    }

    // Extraction des lettres de debut à fin
    int pasLigne = Integer.compare(fin.getLigne(), debut.getLigne());
    int pasColonne = Integer.compare(fin.getColonne(), debut.getColonne());
    int longueur = Math.max(distLigne, distColonne) + 1;

    StringBuilder lecture = new StringBuilder();
    int l = debut.getLigne(), c = debut.getColonne();
    for (int i = 0; i < longueur; i++) {
        lecture.append(grille.getCase(l, c));
        l += pasLigne; c += pasColonne;
    }

    String motLu = lecture.toString();
    String motLuInverse = lecture.reverse().toString();
    String motCherche = mot.toUpperCase();

    boolean correspond = motLu.equalsIgnoreCase(motCherche) || motLuInverse.equalsIgnoreCase(motCherche);

    if (correspond && dictionnaire.contient(motCherche)) {
        dictionnaire.supprimer(motCherche);
        
        // Logique de Combo (écart < 60s)
        long tempsActuel = System.currentTimeMillis();
        if (dernierTempsTrouve > 0 && (tempsActuel - dernierTempsTrouve) < 60000) {
            comboActuel++;
        } else {
            comboActuel = 1;
        }
        dernierTempsTrouve = tempsActuel;
        
        score += motCherche.length() * 10 * comboActuel;
        return true;
    }
    return false;
}
```

---

### 📦 4. Le Package `view` (L'Interface Graphique Swing)

#### 📄 `FenetrePrincipale.java`
Gère l'affichage Swing de la grille, le minuteur et la sélection par clics à la souris.

##### A. Sélection par clics successifs
```java
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
```

##### B. Miniteur Swing
```java
// Initialisation du minuteur dans le constructeur
minuteur = new javax.swing.Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        tempsEcouleSecondes++;
        mettreAJourTimer();
    }
});
minuteur.start();
```

---

## 3. Compilation et Exécution du Projet

Depuis la racine du projet :

### 1. Compiler le code
```bash
mkdir -p bin
javac -d bin src/model/*.java src/exception/*.java src/moteur/MoteurJeu.java src/moteur/AppConsole.java src/view/FenetrePrincipale.java src/moteur/TestGrille.java
```

### 2. Lancer la version graphique
```bash
java -cp bin view.FenetrePrincipale
```

### 3. Lancer la version Console
```bash
java -cp bin moteur.AppConsole
```
