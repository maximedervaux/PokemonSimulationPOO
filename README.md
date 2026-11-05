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
- Les 48 messages sont répartis entre les agents au démarrage.

### Déplacement et énergie

- À chaque étape, l'ordre des agents est tiré au hasard et chacun se déplace une fois.
- Un agent avance de 1 à 3 cases dans une direction.
- Avec 20 % d'EP ou plus, la direction est aléatoire et différente de la précédente.
- Sous 20 % d'EP, l'agent se dirige vers son Master.
- Un agent bloqué par un obstacle, le bord de la carte ou une zone étrangère perd
  l'EP des cases qu'il lui restait à parcourir.
- À 0 EP, l'agent perd ses messages et devient un obstacle.

### Rencontres et score

Une rencontre a lieu seulement si un autre agent se trouve sur le trajet.

| Rencontre | Effet |
|---|---|
| Même population | Les deux agents mettent leurs messages en commun |
| Même alliance | Chacun reçoit au hasard une partie des messages qui lui manquent |
| Alliances opposées | Combat : les chances de gagner sont proportionnelles à l'EP, et le vainqueur prend au hasard des messages au perdant |

Un agent livre une copie de ses messages lorsqu'il termine son tour sur une case
adjacente à son Master.

La simulation s'arrête lorsqu'un Master possède tous les messages, ou au bout de
3000 étapes.

## Calibrage

| Paramètre | Valeur |
|---|---|
| EP maximum | 100 |
| Coût par case | 2 EP |
| Récupération en SafeZone | 10 EP par étape |
| Seuil de retour | 20 % des EP |
| Graine aléatoire | 67 |

**Ce calibrage reste à revoir.** Sur 200 graines, aucun groupe n'a collecté tous les
messages et 15,7 agents sur 16 sont morts en moyenne.

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
