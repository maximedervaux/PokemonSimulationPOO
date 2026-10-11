package master;

import environment.GameMap;
import environment.Occupant;
import environment.SafeZone;
import environment.Tile;
import interaction.Message;

import java.util.HashSet;
import java.util.Set;

public abstract class Master extends Occupant {
    private static int instanceCount = 0;

    protected final Set<Message> messages = new HashSet<>();
    protected SafeZone safeZone;
    protected GameMap map;
    private Tile tile;

    protected Master() {
        super();
        instanceCount++;
    }

    /** Attache le Master à sa zone et le place sur la carte. */
    public void init(SafeZone zone, Tile tile, GameMap map) {
        this.safeZone = zone;
        this.map = map;
        map.place(this, tile);                     // met à jour Tile.occupant et Master.tile
    }

    /** Remet le singleton à zéro pour rejouer une partie. */
    public void reset() {
        messages.clear();
        if (tile != null) {
            tile.setOccupant(null);                // libère l'ancienne case
        }
        tile = null;
        safeZone = null;
        map = null;
    }

    /** Union des messages d'un mobile dans ceux du Master (pas de doublon grâce au Set). */
    public void collect(Set<Message> delivered) {
        messages.addAll(delivered);
    }

    /** Score du groupe : nombre de messages distincts détenus par le Master. */
    public int getScore() { return messages.size(); }

    public Set<Message> getMessages() { return messages; }
    public SafeZone getSafeZone() { return safeZone; }
    public Tile getTile() { return tile; }
    public void setTile(Tile tile) { this.tile = tile; }   // appelé par GameMap.place uniquement

    public static int getInstanceCount() { return instanceCount; }

    @Override public String getSymbol() { return "M";};

    @Override public boolean isObstacle() { return false; }
}