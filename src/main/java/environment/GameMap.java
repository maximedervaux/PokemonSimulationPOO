package environment;

import enums.Corner;
import enums.Direction;
import utils.RandomGenerator;

import java.util.*;

public class GameMap {

    private int _height = 0;
    private int _width = 0;
    private final List<List<Tile>> tiles = new ArrayList<>();
    private final Map<Corner, SafeZone> _safeZones = new EnumMap<>(Corner.class);

    public GameMap(int height, int width, RandomGenerator random, double density){
        this._height = height;
        this._width = width;

        // First we instantiate all tiles
        for(int x = 0; x < this._width; x++){
            List<Tile> row = new ArrayList<>();
            for(int y = 0; y < this._height; y++){
                row.add(new Tile(x, y));
            }
            tiles.add(row);
        }

        // Secondly, we generate the 4 SafeZone
        this.createSafeZone();

        // Finally, we generate the obstacles
        this.generateObstacles(random, density);
    }

    private void createSafeZone(){
        int[] dim = getSafeZoneDimensions();
        int zoneWidth = dim[0];
        int zoneHeight = dim[1];

        if (4 * zoneWidth * zoneHeight >= this._width * this._height / 2) {
            throw new IllegalStateException("SafeZones trop grandes pour cette carte");
        }

        _safeZones.put(Corner.TOP_LEFT,     new SafeZone(0,            0,             zoneWidth, zoneHeight));
        _safeZones.put(Corner.TOP_RIGHT,    new SafeZone(_width - zoneWidth,  0,             zoneWidth, zoneHeight));
        _safeZones.put(Corner.BOTTOM_LEFT,  new SafeZone(0,            _height - zoneHeight,  zoneWidth, zoneHeight));
        _safeZones.put(Corner.BOTTOM_RIGHT, new SafeZone(_width - zoneWidth,  _height - zoneHeight,  zoneWidth, zoneHeight));
    }

    private int getMaximalSafeZoneSize() {
        return (this._width * this._height - 1) / 12;
    }

    private int[] getSafeZoneDimensions() {
        int maxArea = getMaximalSafeZoneSize();

        int width = (int) Math.floor(Math.sqrt((double) maxArea * this._width / this._height));
        width = Math.clamp(width, 1, this._width / 2);

        int height = maxArea / width;
        height = Math.clamp(height, 1, this._height / 2);

        return new int[] { width, height };
    }

    public Collection<SafeZone> getSafeZones(){
        return this._safeZones.values();
    }

    /**
     * Indique si les coordonnées données correspondent à une case existante de la carte
     * @param x indice horizontal (colonne), compris entre 0 et width - 1
     * @param y indice vertical (ligne), compris entre 0 et height - 1
     * @return true si (x, y) est dans les limites de la carte, false sinon
     */
    public boolean isInside(int x, int y) {
        return x >= 0 && x < this._width && y >= 0 && y < this._height;
    }

    public Tile getTile(int x, int y){
        return this.tiles.get(x).get(y);
    }

    public List<Tile> getTilesAround(int x, int y) {
        if (!isInside(x, y)) {
            throw new IllegalArgumentException("Hors carte : (" + x + ", " + y + ")");
        }
        List<Tile> around = new ArrayList<>(8);
        for (Direction d : Direction.values()) {
            int nx = x + d.getDx();
            int ny = y + d.getDy();
            if (isInside(nx, ny)) around.add(getTile(nx, ny));
        }
        return around;
    }

    public int getAvailableTileAround(int x, int y){
        return this.getTilesAround(x,y).size();
    }

    public SafeZone getSafeZone(Corner corner) {
        return this._safeZones.get(corner);
    }

    public SafeZone getSafeZoneAt(int x, int y) {
        for (SafeZone z : this._safeZones.values()) {
            if (z.contains(x, y)) return z;
        }
        return null;
    }

    private void generateObstacles(RandomGenerator random, double density){
        for(List<Tile> row : this.tiles){
            for(Tile tile : row){
                if(this.getSafeZoneAt(tile.getX(), tile.getY()) != null) continue;
                if(!tile.isFree()) continue;
                if(random.nextBoolean(density)){
                    tile.setOccupant(new Obstacle());
                }
            }
        }
    }

    public void place(Occupant occupant, Tile tile){
        tile.setOccupant(occupant);
    }

    /**
     * Method to debug till we get UI.
     * Occupant -> its symbol (# obstacle, F/W/g/p masters...),
     * SafeZone -> 'S', empty tile -> '.'
     */
    public void printDebug() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < this._height; y++) {
            for (int x = 0; x < this._width; x++) {
                Tile tile = getTile(x, y);
                Occupant occupant = tile.getOccupant();

                String symbol;
                if (occupant != null) {
                    symbol = occupant.getSymbol();     // polymorphe : pas d'instanceof
                } else if (getSafeZoneAt(x, y) != null) {
                    symbol = "S";
                } else {
                    symbol = ".";
                }
                sb.append(symbol).append(' ');
            }
            sb.append('\n');
        }
        System.out.print(sb);
    }

    public List<Tile> getFreeTilesIn(SafeZone zone) {
        List<Tile> free = new ArrayList<>();
        for (int y = zone.getY0(); y < zone.getY0() + zone.getHeight(); y++) {
            for (int x = zone.getX0(); x < zone.getX0() + zone.getWidth(); x++) {
                Tile t = getTile(x, y);
                if (t.getOccupant() == null) free.add(t);
            }
        }
        return free;
    }

}