J’ai déjà créé un projet Java dans IntelliJ pour notre projet universitaire
« Satellites et balises ». Implémente directement dans ce projet une première
version simple qui simule la collecte de température par une balise.

Repère le dossier source et le package existants. Réutilise Main.java s’il
existe. Garde la configuration IntelliJ et le système de build actuels.

Implémente trois classes :

- Mesure.java : une classe Java classique avec trois champs privés final
  (int pas, double profondeur, double temperature), un constructeur,
  des getters et un toString() lisible. N’utilise aucun record.
- Balise.java : une balise avec une profondeur fixe, une capacité mémoire
  positive, une liste de mesures et une enum Etat avec COLLECTE et REMONTEE.
  Elle commence en COLLECTE. À chaque appel à avancer(int pas), elle
  enregistre une mesure de température calculée par 15.0 - 0.1 * profondeur.
  Dès que la mémoire est pleine, elle passe à REMONTEE. Les appels suivants
  ne produisent plus de mesures. La remontée physique viendra plus tard.
- Main.java : crée une balise à 20 m avec une capacité de 3 mesures.
  Fais avancer la simulation de 4 pas et affiche, après chaque pas, le
  numéro du pas, l’état et le nombre de mesures. Affiche ensuite les mesures.

Les quatre premières lignes attendues sont :

Pas 1 | COLLECTE | 1 mesure(s)
Pas 2 | COLLECTE | 2 mesure(s)
Pas 3 | REMONTEE | 3 mesure(s)
Pas 4 | REMONTEE | 3 mesure(s)

Garde le code accessible à des étudiants. Limite ce premier incrément à la
collecte et au changement d’état. Compile et lance Main pour vérifier la
sortie, puis explique brièvement le rôle de chaque fichier et comment lancer
le programme depuis IntelliJ.