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
    public abstract boolean isGood();

    /** Directions que cette population peut emprunter. */
    protected abstract List<Direction> allowedDirections();

    protected Pokemon(Master master, int maxEp) {
        this.master = master;
        this.maxEp = maxEp;
        this.ep = maxEp;
        this.id = ++instanceCount;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public int getId() {
        return id;
    }

    /** Nom affiché de cet individu, par exemple F3. */
    public String getLabel() {
        return getSymbol() + id;
    }

    public Master getMaster() {
        return master;
    }

    public boolean isSamePopulation(Pokemon other) {
        return this.master == other.master;
    }

    public boolean isSameAlliance(Pokemon other) {
        return this.isGood() == other.isGood();
    }

    public Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    public boolean learn(Message m) {
        return messages.add(m);
    }

    public void learnAll(Set<Message> others) {
        messages.addAll(others);
    }

    public void removeMessages(Collection<Message> toRemove) {
        messages.removeAll(toRemove);
    }

    private void loseAllMessages() {
        messages.clear();
    }

    public int getEp() {
        return ep;
    }

    public double getEpRatio() {
        return (double) ep / maxEp;
    }

    public boolean isLowEnergy() {
        return getEpRatio() < 0.20;
    }

    public boolean isExhausted() {
        return ep <= 0;
    }

    private void spendEp(int amount) {
        ep = Math.max(0, ep - amount);
    }

    private void restoreEp(int amount) {
        ep = Math.min(maxEp, ep + amount);
    }

    private void setLastDirection(Direction d) {
        this.lastDirection = d;
    }

    @Override public boolean isObstacle() {
        return false;
    }

    /** Joue le tour de l'individu et ajoute à events ce qui s'y est passé. */
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
    private boolean canEnter(GameMap map, Tile tile) {
        SafeZone zone = map.getSafeZoneAt(tile.getX(), tile.getY());
        return zone == null || zone.getMaster() == this.master;
    }

    protected int chooseDistance(RandomGenerator random) {
        return random.nextInt(1, 3);
    }

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
    private void onBlocked(GameMap map, int remainingTiles) {
        blockedLastMove = true;
        if (!isInSafeZone(map)) {
            spendEp(remainingTiles * EP_PER_TILE);
        }
    }

    /** Aucune confrontation depuis une SafeZone. */
    private boolean isConfrontationForbidden(GameMap map, Pokemon other) {
        return !isSameAlliance(other) && isInSafeZone(map);
    }

    private boolean isInSafeZone(GameMap map) {
        Tile current = getTile();
        if (current == null) return false;
        SafeZone zone = map.getSafeZoneAt(current.getX(), current.getY());
        return zone != null && zone.getMaster() == this.master;
    }

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