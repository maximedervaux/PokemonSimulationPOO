package pokemon;

import environment.GameMap;
import environment.Occupant;
import interaction.Message;
import master.Master;
import enums.Direction;
import utils.RandomGenerator;

import java.util.HashSet;
import java.util.Set;

public abstract class Pokemon extends Occupant {
    private static int instanceCount = 0;

    protected final Master master;                      // identifie la population
    protected final Set<Message> messages = new HashSet<>();
    protected final int maxEp;
    protected int ep;
    protected Direction lastDirection;

    protected Pokemon(Master master, int maxEp) {
        this.master = master;
        this.maxEp = maxEp;
        this.ep = maxEp;
        instanceCount++;
    }

    /** Un tour : calcule le trajet, se déplace, gère blocages et rencontres. Polymorphe. */
    public abstract void move(GameMap map, RandomGenerator random);

    // --- Population / alliance ---
    public Master getMaster() { return master; }

    public boolean isSamePopulation(Pokemon other) {
        return this.master == other.master;
    }

    public Set<Message> getMessages() { return messages; }

    public boolean learn(Message m) { return messages.add(m); }

    public void learnAll(Set<Message> others) { messages.addAll(others); }

    public void loseAllMessages() { messages.clear(); }

    public int getEp() { return ep; }
    public double getEpRatio() { return (double) ep / maxEp; }
    public boolean isLowEnergy() { return getEpRatio() < 0.20; }
    public boolean isExhausted() { return ep <= 0; }

    public void spendEp(int amount) { ep = Math.max(0, ep - amount); }
    public void restoreEp(int amount) { ep = Math.min(maxEp, ep + amount); }

    public Direction getLastDirection() { return lastDirection; }
    protected void setLastDirection(Direction d) { this.lastDirection = d; }

    @Override public boolean isObstacle() { return false; }

    public static int getInstanceCount() { return instanceCount; }
}