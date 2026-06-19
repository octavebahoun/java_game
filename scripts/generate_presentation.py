from pptx import Presentation
from pptx.util import Inches, Pt

prs = Presentation()

# Helper to add slide with title and bullet points and speaker notes
def add_bullet_slide(title, bullets, notes=None):
    slide_layout = prs.slide_layouts[1]  # Title and Content
    slide = prs.slides.add_slide(slide_layout)
    slide.shapes.title.text = title
    body = slide.shapes.placeholders[1]
    tf = body.text_frame
    tf.clear()
    for i, b in enumerate(bullets):
        if i == 0:
            p = tf.paragraphs[0]
            p.text = b
        else:
            p = tf.add_paragraph()
            p.text = b
            p.level = 1
    if notes:
        slide.notes_slide.notes_text_frame.text = notes
    return slide

# Title slide
slide_layout = prs.slide_layouts[0]
slide = prs.slides.add_slide(slide_layout)
slide.shapes.title.text = "Jeu de Mots Cachés"
slide.placeholders[1].text = "Présentation du projet & démonstration"
slide.notes_slide.notes_text_frame.text = "Bonjour, je présente le projet 'Jeu de Mots Cachés'."

# Overview
add_bullet_slide(
    "Contexte et objectif",
    [
        "Petit jeu éducatif en Java (Swing + console)",
        "But : trouver des mots cachés dans une grille 10x10",
        "Approche : architecture MVC légère, tests unitaires simples"
    ],
    notes="Objectif : expliquer rapidement le projet, sa finalité et le public visé."
)

# Architecture
add_bullet_slide(
    "Architecture du projet",
    [
        "Packages : model, moteur, view, exception",
        "model : structures de données (Grille, Dictionnaire, Coordonnee)",
        "moteur : logique du jeu (MoteurJeu, AppConsole)",
        "view : interface graphique Swing (FenetrePrincipale)"
    ],
    notes="Montrer comment les responsabilités sont séparées entre model, moteur et view."
)

# Grille
add_bullet_slide(
    "Classe `Grille` (src/model/Grille.java)",
    [
        "Représente une grille 10x10 de lettres",
        "Méthodes clés : placerMot(), remplirCasesVides(), toString()",
        "Gestion des collisions et remplissage aléatoire"
    ],
    notes="Expliquer l'algorithme de placement : vérification de la place, compatibilité, puis écriture."
)

# MoteurJeu
add_bullet_slide(
    "Classe `MoteurJeu` (src/moteur/MoteurJeu.java)",
    [
        "Contient la grille, le dictionnaire et le score",
        "méthode principale verifier() : lit trajectoire entre deux coordonnées",
        "Valide mot + présence dans le dictionnaire -> met à jour le score"
    ],
    notes="Décrire l'algorithme de vérification : validation des coordonnées, détermination de l'alignement, lecture et comparaison."
)

# Dictionnaire
add_bullet_slide(
    "Classe `Dictionnaire` (src/model/Dictionnaire.java)",
    [
        "Stocke les mots à trouver et la liste complète",
        "Permet d'ajouter, vérifier, supprimer et compter les mots",
        "Gère l'état des mots trouvés"
    ],
    notes="Préciser que les mots sont stockés en majuscules et que le dictionnaire suit les mots restants."
)

# Interface graphique
add_bullet_slide(
    "Interface graphique (src/view/FenetrePrincipale.java)",
    [
        "Swing : grille visuelle (GridLayout 10x10)",
        "Champs de saisie pour mot et coordonnées, boutons Valider/Rejouer",
        "Feedback visuel : colorisation des mots trouvés et popups d'erreur"
    ],
    notes="Montrer la capture d'écran (si disponible) et expliquer l'interaction utilisateur."
)

# Mode console
add_bullet_slide(
    "Mode console (src/moteur/AppConsole.java)",
    [
        "Version texte du jeu pour tester rapidement",
        "Affiche la grille, lit saisies, gère exceptions et messages utilisateur"
    ],
    notes="Indiquer que le mode console facilite le test sans interface graphique."
)

# Démo
add_bullet_slide(
    "Démonstration",
    [
        "Lancer `AppConsole` pour jouer en CLI",
        "Ou exécuter `FenetrePrincipale` pour la version GUI",
        "Montrer recherche d'un mot et mise à jour du score"
    ],
    notes="Expliquer brièvement les étapes de la démo et ce que le public doit observer."
)

# Conclusion
add_bullet_slide(
    "Conclusion et perspectives",
    [
        "Projet pédagogique facile à étendre",
        "Améliorations possibles : import de listes, niveau aléatoire, sauvegarde de parties",
        "Questions ?"
    ],
    notes="Proposer des pistes d'amélioration et inviter aux questions."
)

# Save
prs.save('presentation.pptx')
print('presentation.pptx créée avec succès.')
