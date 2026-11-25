package environment;

public class Tile {
    private final int _x;
    private final int _y;
    private Occupant _occupant = null;

    /** Crée une case aux coordonnées de la carte fournies. */
    Tile(int x, int y){
        this._x = x;
        this._y = y;
    }

    /** Retourne l'abscisse de la case. */
    public int getX() {
        return this._x;
    }

    /** Retourne l'ordonnée de la case. */
    public int getY() {
        return this._y;
    }

    /** Retourne l'occupant de la case, ou {@code null} si elle est libre. */
    public Occupant getOccupant() {
        return _occupant;
    }

    /** Définit l'occupant de cette case ; appelé par {@link GameMap}. */
    void setOccupant(Occupant _occupant) {
        this._occupant = _occupant;
    }

    /** Indique si aucun occupant n'est présent sur cette case. */
    public boolean isFree(){
        return this._occupant == null;
    }
}
