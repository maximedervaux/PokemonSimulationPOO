package environment;

public abstract class Occupant {
    private Tile tile;                     // case actuelle (null si non placé)

    public Tile getTile() { return tile; }

    /** Appelé uniquement par GameMap.place(...) pour garder Tile.occupant et Occupant.tile cohérents. */
    public void setTile(Tile tile) { this.tile = tile; }

    /** Vrai si l'occupant bloque définitivement le passage (Obstacle). */
    public abstract boolean isObstacle();

    /** Symbole pour l'affichage console. */
    public abstract String getSymbol();
}