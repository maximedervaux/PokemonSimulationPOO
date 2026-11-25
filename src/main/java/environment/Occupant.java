package environment;

public abstract class Occupant {
    private Tile tile;

    /** Retourne la case actuellement occupée, ou {@code null} si l'objet est hors carte. */
    public Tile getTile() {
        return tile;
    }

    /** Met à jour la case de l'occupant ; cette opération est réservée à {@link GameMap}. */
    void setTile(Tile tile) { this.tile = tile; }

    /** Indique si cet occupant bloque le déplacement comme un obstacle. */
    public abstract boolean isObstacle();

    /** Retourne le symbole utilisé pour représenter cet occupant. */
    public abstract String getSymbol();
}
