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

    private final Master master;
    private final Set<Message> messages = new HashSet<>();
    private final int maxEp;
    private int ep;
    private Direction lastDirection;

    private static final int EP_PER_TILE = 2;
    private static final int REGEN_PER_STEP = 10;
    public abstract boolean isGood();

    protected Pokemon(Master master, int maxEp) {
        this.master = master;
        this.maxEp = maxEp;
        this.ep = maxEp;
        instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
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

    public void move(GameMap map, RandomGenerator random) {
        if (isExhausted()) return;

        Direction dir = chooseDirection(map, random);

        if (dir != null) {
            int distance = chooseDistance(random);
            travel(map, dir, distance, random);
        }
        deliverToMaster(map);
        afterTravel(map);
    }

    protected Direction chooseDirection(GameMap map, RandomGenerator random) {
        if (isLowEnergy()) {
            Direction home = map.directionToSafeZone(this);
            if (home != null) {
                return home;
            }
            if (isInSafeZone(map)) {
                return null;
            }
        }
        Direction dir;
        do {
            dir = random.pickRandom(Direction.all());
        } while (dir == lastDirection);
        return dir;
    }

    protected int chooseDistance(RandomGenerator random) {
        return random.nextInt(1, 3);
    }

    private void travel(GameMap map, Direction dir, int distance, RandomGenerator random) {
        Tile current = getTile();

        for (int step = 0; step < distance; step++) {
            int nx = current.getX() + dir.getDx();
            int ny = current.getY() + dir.getDy();

            // Case 1: Out of the map
            if (!map.isInside(nx, ny)) {
                spendEp((distance - step) * EP_PER_TILE);
                break;
            }

            Tile next = map.getTile(nx, ny);

            // Case 2: Not its safeZone
            SafeZone zone = map.getSafeZoneAt(nx, ny);
            if (zone != null && zone.getMaster() != this.master) {
                spendEp((distance - step) * EP_PER_TILE);
                break;
            }

            // Case 3: the Tile is occupied
            Occupant occupant = next.getOccupant();
            if (occupant != null) {
                if (occupant.isObstacle()) {
                    spendEp((distance - step) * EP_PER_TILE);
                } else if (occupant instanceof Pokemon other){
                    Encounter encounter = Encounter.between(this, other);
                    encounter.resolve(random);
                }
                break;
            }

            // Case 4: Move
            map.place(this, next);
            current = next;
            if (!isInSafeZone(map)) {
                spendEp(EP_PER_TILE);
            }
        }

        setLastDirection(dir);
    }

    private boolean isInSafeZone(GameMap map) {
        Tile current = getTile();
        if (current == null) return false;
        SafeZone zone = map.getSafeZoneAt(current.getX(), current.getY());
        return zone != null && zone.getMaster() == this.master;
    }

    private void afterTravel(GameMap map) {
        if (isInSafeZone(map)) {
            restoreEp(REGEN_PER_STEP);
        }
        if (isExhausted()) {
            loseAllMessages();
            map.convertToObstacle(this);
        }
    }

    private void deliverToMaster(GameMap map) {
        Tile mine = getTile();
        Tile masterTile = master.getTile();
        if (mine == null || masterTile == null) return;

        int dx = Math.abs(mine.getX() - masterTile.getX());
        int dy = Math.abs(mine.getY() - masterTile.getY());

        boolean adjacent = dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
        if (adjacent) {
            master.collect(this.messages);
        }
    }
}