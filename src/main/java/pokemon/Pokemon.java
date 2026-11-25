package pokemon;

import environment.GameMap;
import environment.Occupant;
import environment.SafeZone;
import environment.Tile;
import interaction.Encounter;
import interaction.Message;
import master.Master;
import enums.Direction;
import utils.RandomGenerator;

import java.util.*;

public abstract class Pokemon extends Occupant {
    private static int instanceCount = 0;

    private final int id;
    private final Master master;
    private final Set<Message> messages = new HashSet<>();
    private final int maxEp;
    private int ep;
    private Direction lastDirection;
    private boolean blockedLastMove = false;

    private static final int EP_PER_TILE = 1;
    private static final int REGEN_PER_STEP = 10;
    private static final int[] DETOUR_OFFSETS = {0, 1, -1, 2, -2, 3, -3, 4};
    /** Indique si le Pokémon appartient à l'alliance Good. */
    public abstract boolean isGood();

    /** Directions que cette population peut emprunter. */
    /** Retourne les directions autorisées pour cette population. */
    protected abstract List<Direction> allowedDirections();

    /** Initialise un Pokémon associé à un Master et doté de son énergie maximale. */
    protected Pokemon(Master master, int maxEp) {
        this.master = master;
        this.maxEp = maxEp;
        this.ep = maxEp;
        this.id = ++instanceCount;
    }

    /** Retourne le nombre total de Pokémon créés. */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /** Retourne l'identifiant unique du Pokémon. */
    public int getId() {
        return id;
    }

    /** Nom affiché de cet individu, par exemple F3. */
    public String getLabel() {
        return getSymbol() + id;
    }

    /** Retourne le Master de l'équipe du Pokémon. */
    public Master getMaster() {
        return master;
    }

    /** Indique si deux Pokémon appartiennent à la même équipe. */
    public boolean isSamePopulation(Pokemon other) {
        return this.master == other.master;
    }

    /** Indique si deux Pokémon appartiennent à la même alliance. */
    public boolean isSameAlliance(Pokemon other) {
        return this.isGood() == other.isGood();
    }

    /** Retourne les messages connus sans permettre leur modification directe. */
    public Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    /** Ajoute un message et indique s'il était nouveau pour ce Pokémon. */
    public boolean learn(Message m) {
        return messages.add(m);
    }

    /** Ajoute tous les messages fournis sans créer de doublons. */
    public void learnAll(Set<Message> others) {
        messages.addAll(others);
    }

    /** Retire les messages transférés à un adversaire. */
    public void removeMessages(Collection<Message> toRemove) {
        messages.removeAll(toRemove);
    }

    /** Efface les connaissances du Pokémon. */
    private void loseAllMessages() {
        messages.clear();
    }

    /** Retourne l'énergie actuelle du Pokémon. */
    public int getEp() {
        return ep;
    }

    /** Retourne l'énergie actuelle rapportée à l'énergie maximale. */
    public double getEpRatio() {
        return (double) ep / maxEp;
    }

    /** Indique si l'énergie est sous le seuil de retour de 20 %. */
    public boolean isLowEnergy() {
        return getEpRatio() < 0.20;
    }

    /** Indique si le Pokémon n'a plus d'énergie et doit être retiré. */
    public boolean isExhausted() {
        return ep <= 0;
    }

    /** Dépense de l'énergie sans descendre sous zéro. */
    private void spendEp(int amount) {
        ep = Math.max(0, ep - amount);
    }

    /** Restaure l'énergie sans dépasser le maximum du Pokémon. */
    private void restoreEp(int amount) {
        ep = Math.min(maxEp, ep + amount);
    }

    /** Mémorise la dernière direction empruntée. */
    private void setLastDirection(Direction d) {
        this.lastDirection = d;
    }

    /** Indique qu'un Pokémon actif n'est pas un obstacle. */
    @Override public boolean isObstacle() {
        return false;
    }

    /** Joue le tour de l'individu et ajoute à events ce qui s'y est passé. */
    /** Effectue le déplacement du tour, gère les rencontres et livre les messages proches du Master. */
    public void move(GameMap map, RandomGenerator random, List<String> events) {
        if (isExhausted()) return;

        Direction dir = chooseDirection(map, random);

        if (dir != null) {
            // En retour, une case par tour : la direction est recalculée pour contourner les blocages
            int distance = isLowEnergy() ? 1 : chooseDistance(random);
            travel(map, dir, distance, random, events);
        }
        deliverToMaster(map, events);
        afterTravel(map, events);
    }

    /** Choisit une direction aléatoire ou un trajet de retour selon l'énergie. */
    protected Direction chooseDirection(GameMap map, RandomGenerator random) {
        if (isLowEnergy()) {
            Direction home = map.directionToSafeZone(this);
            if (home != null) {
                return firstOpenDirection(map, home);
            }
            if (isInSafeZone(map)) {
                return null;
            }
        }
        return randomDirection(map, random);
    }

    /** Direction aléatoire vers une case accessible, sans refaire le déplacement qui vient d'être bloqué. */
    /** Tire une direction autorisée vers une case accessible. */
    private Direction randomDirection(GameMap map, RandomGenerator random) {
        Tile current = getTile();
        List<Direction> options = new ArrayList<>();
        for (Tile tile : map.getTilesAround(current.getX(), current.getY())) {
            Direction dir = Direction.fromDelta(tile.getX() - current.getX(), tile.getY() - current.getY());
            boolean justBlocked = blockedLastMove && dir == lastDirection;
            if (allowedDirections().contains(dir) && canEnter(map, tile) && !justBlocked) {
                options.add(dir);
            }
        }
        if (options.isEmpty()) {
            // La seule issue vient d'être bloquée : elle redevient possible au tour suivant
            blockedLastMove = false;
            return null;
        }
        return random.pickRandom(options);
    }

    /** Contourne un blocage : essaie la direction souhaitée, puis les directions de plus en plus éloignées. */
    /** Cherche une direction ouverte proche de la direction de retour souhaitée. */
    private Direction firstOpenDirection(GameMap map, Direction wanted) {
        Tile current = getTile();
        for (int offset : DETOUR_OFFSETS) {
            Direction dir = wanted.rotated(offset);
            if (!allowedDirections().contains(dir)) continue;

            int nx = current.getX() + dir.getDx();
            int ny = current.getY() + dir.getDy();
            if (!map.isInside(nx, ny)) continue;

            Tile tile = map.getTile(nx, ny);
            boolean hasObstacle = tile.getOccupant() != null && tile.getOccupant().isObstacle();
            if (canEnter(map, tile) && !hasObstacle) {
                return dir;
            }
        }
        return null;
    }

    /** Seule la population du Master peut entrer dans sa SafeZone. */
    /** Vérifie qu'un Pokémon peut entrer sur la case et dans sa SafeZone éventuelle. */
    private boolean canEnter(GameMap map, Tile tile) {
        SafeZone zone = map.getSafeZoneAt(tile.getX(), tile.getY());
        return zone == null || zone.getMaster() == this.master;
    }

    /** Tire la distance de déplacement maximale pour un tour. */
    /** Tire la distance de déplacement maximale pour un tour. */
    protected int chooseDistance(RandomGenerator random) {
        return random.nextInt(1, 3);
    }

    /** Avance case par case jusqu'à la distance prévue ou jusqu'à un blocage/une rencontre. */
    private void travel(GameMap map, Direction dir, int distance, RandomGenerator random, List<String> events) {
        Tile current = getTile();
        blockedLastMove = false;

        for (int step = 0; step < distance; step++) {
            int nx = current.getX() + dir.getDx();
            int ny = current.getY() + dir.getDy();

            // Case 1: Out of the map
            if (!map.isInside(nx, ny)) {
                onBlocked(map, distance - step);
                break;
            }

            Tile next = map.getTile(nx, ny);

            // Case 2: Not its safeZone
            if (!canEnter(map, next)) {
                onBlocked(map, distance - step);
                break;
            }

            // Case 3: the Tile is occupied
            Occupant occupant = next.getOccupant();
            if (occupant != null) {
                if (occupant.isObstacle()) {
                    onBlocked(map, distance - step);
                } else if (occupant instanceof Pokemon other && !isConfrontationForbidden(map, other)) {
                    Encounter encounter = Encounter.between(this, other);
                    encounter.resolve(random);
                    events.add(encounter.describe());
                }
                break;
            }

            // Case 4: Move
            map.place(this, next);
            current = next;
            if (!isInSafeZone(map)) {
                spendEp(EP_PER_TILE);
                if (isExhausted()) break;
            }
        }

        setLastDirection(dir);
    }

    /** Un blocage coûte les cases restantes, sauf dans sa propre SafeZone où aucun EP n'est dépensé. */
    /** Applique le coût d'énergie des cases restantes après un blocage. */
    private void onBlocked(GameMap map, int remainingTiles) {
        blockedLastMove = true;
        if (!isInSafeZone(map)) {
            spendEp(remainingTiles * EP_PER_TILE);
        }
    }

    /** Aucune confrontation depuis une SafeZone. */
    /** Interdit les combats lancés depuis une SafeZone. */
    private boolean isConfrontationForbidden(GameMap map, Pokemon other) {
        return !isSameAlliance(other) && isInSafeZone(map);
    }

    /** Indique si le Pokémon se trouve dans la zone de son Master. */
    private boolean isInSafeZone(GameMap map) {
        Tile current = getTile();
        if (current == null) return false;
        SafeZone zone = map.getSafeZoneAt(current.getX(), current.getY());
        return zone != null && zone.getMaster() == this.master;
    }

    /** Recharge le Pokémon dans sa zone ou le convertit en obstacle s'il est épuisé. */
    private void afterTravel(GameMap map, List<String> events) {
        if (isInSafeZone(map)) {
            restoreEp(REGEN_PER_STEP);
        }
        if (isExhausted()) {
            events.add(getLabel() + " épuisé : perd " + messages.size() + " message(s) et devient un obstacle");
            loseAllMessages();
            map.convertToObstacle(this);
        }
    }

    /** Transmet au Master les messages du Pokémon lorsqu'il en est adjacent. */
    private void deliverToMaster(GameMap map, List<String> events) {
        Tile mine = getTile();
        Tile masterTile = master.getTile();
        if (mine == null || masterTile == null) return;

        int dx = Math.abs(mine.getX() - masterTile.getX());
        int dy = Math.abs(mine.getY() - masterTile.getY());

        boolean adjacent = dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
        if (adjacent) {
            int delivered = master.collect(this.messages);
            if (delivered > 0) {
                events.add(getLabel() + " livre " + delivered + " message(s) au Master " + master.getGroupName());
            }
        }
    }
}
