package environment;

public class Tile {
    private final int _x;
    private final int _y;
    private Occupant _occupant = null;

    Tile(int x, int y){
        this._x = x;
        this._y = y;
    }

    Tile(int x, int y, Occupant occupant){
        this._x = x;
        this._y = y;
        this._occupant = occupant;
    }

    public int getX() {
        return this._x;
    }

    public int getY() {
        return this._y;
    }

    public Occupant getOccupant() {
        return _occupant;
    }

    public void setOccupant(Occupant _occupant) {
        this._occupant = _occupant;
    }

    public boolean isFree(){
        return this._occupant == null;
    }
}