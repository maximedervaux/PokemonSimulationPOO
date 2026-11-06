package master;

import environment.GameMap;
import environment.Occupant;
import environment.Tile;
import interaction.Message;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Master extends Occupant {
    private static int instanceCount = 0;

    private final Set<Message> messages = new HashSet<>();
    private GameMap map;

    protected Master() {
        super();
        instanceCount++;
    }

    /** Place le Master sur la carte. */
    public void init(Tile tile, GameMap map) {
        this.map = map;
        map.place(this, tile);
    }

    /** Remet le singleton à zéro pour rejouer une partie. */
    public void reset() {
        messages.clear();
        if (map != null) {
            map.remove(this);                      // libère l'ancienne case
        }
        map = null;
    }

    /**
     * Union des messages d'un mobile dans ceux du Master (pas de doublon grâce au Set).
     * @return le nombre de nouveaux messages
     */
    public int collect(Set<Message> delivered) {
        int before = messages.size();
        messages.addAll(delivered);
        return messages.size() - before;
    }

    /** Nom du groupe dirigé par ce Master. */
    public abstract String getGroupName();

    /** Score du groupe : nombre de messages distincts détenus par le Master. */
    public int getScore() { return messages.size(); }

    public Set<Message> getMessages() { return Collections.unmodifiableSet(messages); }

    public static int getInstanceCount() { return instanceCount; }

    @Override public String getSymbol() { return "M"; }

    @Override public boolean isObstacle() { return false; }
}
