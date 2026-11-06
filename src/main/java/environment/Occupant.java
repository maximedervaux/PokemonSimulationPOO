package environment;

public abstract class Occupant {
    private Tile tile;

    public Tile getTile() {
        return tile;
    }

    /** Réservé à GameMap, qui garde la case et son occupant cohérents. */
    void setTile(Tile tile) { this.tile = tile; }

    public abstract boolean isObstacle();

    public abstract String getSymbol();
}
