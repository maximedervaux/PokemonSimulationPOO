# Pokemon Simulation

Simulation multi-agents réalisée en Java pour le cours « Concept Objet » (M2).
Quatre populations de Pokémon parcourent une carte pour collecter des messages et
les rapporter à leur Master. Le groupe dont le Master détient le plus de messages
distincts gagne.

## Lancement

Prérequis : JDK 21 ou plus récent, et Maven.

```bash
mvn compile                       # compilation
mvn exec:java                     # simulation en console
mvn exec:java -Dexec.args=gui     # simulation avec interface graphique
```

L'interface graphique (FXGL) crée les dossiers `logs/` et `system/` dans le dossier
courant.

## Règles choisies

Le sujet laisse plusieurs règles à définir. Voici celles du code actuel.

### Populations et carte

| Population | Alliance | Coin de la carte |
|---|---|---|
| Fire (`F`) | Good | Haut gauche |
| Water (`W`) | Good | Haut droit |
| Ghost (`G`) | Bad | Bas gauche |
| Poison (`P`) | Bad | Bas droit |

- La carte fait 25 × 25 cases, avec environ 5 % d'obstacles hors des SafeZones.
- Chaque SafeZone fait 7 × 7 cases et seule sa population peut y entrer.
- Chaque Master (`M`) reste immobile dans le coin de la carte, dans sa SafeZone.
- Chaque population compte 4 agents mobiles.
- 48 messages, tirés parmi 200 textes distincts, sont répartis entre les agents au démarrage.

### Déplacement et énergie

Chaque population a son propre déplacement :

| Population | Déplacement | Directions | Cases par tour | EP maximum |
|---|---|---|---|---|
| Fire | Fou | 4 diagonales | 1 à 3 | 100 |
| Water | Tour | 4 lignes droites | 1 à 3 | 200 |
| Ghost | Dame | 8 directions | 1 à 3 | 100 |
| Poison | Roi | 8 directions | 1 | 100 |

- À chaque étape, l'ordre des agents est tiré au hasard et chacun se déplace une fois.
- Avec 20 % d'EP ou plus, la direction est tirée au hasard parmi celles de la population qui
  mènent à une case de la carte, hors zone étrangère. La direction qui vient d'être bloquée
  est exclue, sauf au tour suivant si c'était la seule possible : l'agent attend alors un tour.
- Sous 20 % d'EP, l'agent se dirige vers son Master, une case par tour, et contourne
  les obstacles et les zones étrangères, toujours avec les directions de sa population.
- Un agent Fire reste toujours sur la même couleur de case, comme un fou aux échecs : deux
  agents Fire de couleurs différentes ne se rencontrent jamais.
- L'EP n'est dépensé qu'en dehors de sa propre SafeZone.
- Hors de sa SafeZone, un agent bloqué par un obstacle, le bord de la carte ou une zone
  étrangère perd l'EP des cases qu'il lui restait à parcourir.
- À 0 EP, l'agent s'arrête aussitôt, perd ses messages et devient un obstacle.

### Rencontres et score

Une rencontre a lieu seulement si un autre agent se trouve sur le trajet.

| Rencontre | Effet |
|---|---|
| Même population | Les deux agents mettent leurs messages en commun |
| Même alliance | Chacun reçoit au hasard une partie des messages qui lui manquent |
| Alliances opposées | Combat : les chances de gagner sont proportionnelles à l'EP, et le vainqueur prend au hasard des messages au perdant |

Un agent dans sa SafeZone ne combat pas : s'il rencontre un adversaire juste en dehors,
il s'arrête sans autre effet.

Un agent livre une copie de ses messages lorsqu'il termine son tour sur une case
adjacente à son Master.

La simulation s'arrête lorsqu'un Master possède tous les messages, lorsqu'il ne reste
plus aucun agent mobile, ou au bout de 3000 étapes. Le groupe dont le Master a le plus
de messages gagne ; à score égal, les groupes concernés sont à égalité.

## Affichage

À chaque étape, la console et l'interface graphique montrent :

- la carte et le score de chaque groupe ;
- l'EP et le nombre de messages de chaque agent actif, par exemple `F3 96EP 2msg` ;
- le nombre d'agents épuisés ;
- les compteurs d'instances (`Pokemon`, chaque population, `Master`, `Message`) ;
- les événements : combats, échanges, unions, livraisons et épuisements.

Le résultat final s'affiche à la fin de la simulation. L'interface graphique garde les
12 derniers événements dans son journal.

## Calibrage

| Paramètre | Valeur |
|---|---|
| EP maximum | 100, et 200 pour Water |
| Coût par case | 1 EP |
| Récupération en SafeZone | 10 EP par étape |
| Seuil de retour | 20 % des EP |
| Graine aléatoire | 67 |

Avec ces valeurs, sur 200 graines (1000 à 1199) :

- un groupe collecte tous les messages dans 180 runs ;
- 13,6 agents sur 16 sont encore vivants en fin de run, en moyenne ;
- la simulation s'arrête après 315 étapes (médiane) ;
- 20 runs vont jusqu'à 3000 étapes, dont 16 parce qu'au moins un message a été perdu ;
- les victoires se répartissent en Fire 45, Water 38, Ghost 79, Poison 22, avec 16 égalités.

Water a 200 EP parce qu'une Tour peut avoir 40 cases à parcourir pour rentrer : son seuil de
retour passe ainsi de 20 à 40 EP.

## Configuration

Le fichier `src/main/resources/simulation.properties` contient les paramètres
modifiables sans changer le code :

| Clé | Rôle | Valeur |
|---|---|---|
| `simulation.seed` | Graine du générateur pseudo-aléatoire | 67 |
| `simulation.max.steps` | Nombre maximal d'étapes | 3000 |
| `simulation.step.delay.ms` | Pause entre deux étapes, en millisecondes | 300 |
| `map.width` | Largeur de la carte, en cases | 25 |
| `map.height` | Hauteur de la carte, en cases | 25 |
| `map.obstacle.density` | Probabilité d'obstacle par case hors SafeZone | 0.05 |
| `pokemon.per.type` | Nombre d'agents par population | 4 |
| `messages.count` | Nombre de messages à collecter | 48 |
| `messages.resource` | Ressource du classpath contenant les messages | `messages.txt` |

Chaque clé peut être remplacée au lancement :

```bash
mvn exec:java -Dsimulation.seed=42 -Dsimulation.step.delay.ms=0
```

Une même graine et une même configuration donnent toujours le même résultat.

## Structure du code

| Package | Rôle |
|---|---|
| `simulation` | Boucle principale et initialisation |
| `environment` | Carte, cases, SafeZones et obstacles |
| `pokemon` | Agents mobiles, alliances et populations |
| `master` | Masters (Singletons) |
| `interaction` | Rencontres et messages |
| `render` | Affichage console et graphique |
| `utils` | Générateur pseudo-aléatoire et configuration |
| `enums` | Directions et coins de la carte |
