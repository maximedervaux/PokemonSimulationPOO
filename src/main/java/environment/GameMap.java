package environment;

import enums.Corner;
import enums.Direction;
import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.*;

public class GameMap {
    private final int _height;
    private final int _width;
    private final List<List<Tile>> tiles = new ArrayList<>();
    private final Map<Corner, SafeZone> _safeZones = new EnumMap<>(Corner.class);

    public GameMap(int width, int height, RandomGenerator random, double density){
        this._height = height;
        this._width = width;

        // First we instantiate all tiles
        for(int x = 0; x < this._width; x++){
            List<Tile> column = new ArrayList<>();
            for(int y = 0; y < this._height; y++){
                column.add(new Tile(x, y));
            }
            tiles.add(column);
        }

        // Secondly, we generate the 4 SafeZone
        this.createSafeZone();

        // Finally, we generate the obstacles
        this.generateObstacles(random, density);
    }

    public int getHeight() {
        return _height;
    }

    public int getWidth() {
        return _width;
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
        for(List<Tile> column : this.tiles){
            for(Tile tile : column){
                if(this.getSafeZoneAt(tile.getX(), tile.getY()) != null) continue;
                if(!tile.isFree()) continue;
                if(random.nextBoolean(density)){
                    this.place(new Obstacle(), tile);
                }
            }
        }
    }

    public void place(Occupant occupant, Tile tile){
        if (!tile.isFree()) {
            throw new IllegalArgumentException("Case déjà occupée : (" + tile.getX() + ", " + tile.getY() + ")");
        }

        // Liberate the old tile
        this.remove(occupant);

        // Update the new one
        tile.setOccupant(occupant);
        occupant.setTile(tile);
    }

    /** Retire un occupant de la carte et libère sa case. */
    public void remove(Occupant occupant) {
        if (occupant.getTile() != null) {
            occupant.getTile().setOccupant(null);
            occupant.setTile(null);
        }
    }

    public List<Tile> getFreeTilesIn(SafeZone zone) {
        List<Tile> free = new ArrayList<>();
        for (int y = zone.getY0(); y < zone.getY0() + zone.getHeight(); y++) {
            for (int x = zone.getX0(); x < zone.getX0() + zone.getWidth(); x++) {
                Tile tile = getTile(x, y);
                if (tile.getOccupant() == null) free.add(tile);
            }
        }
        return free;
    }

    /**
     * Direction à suivre pour rejoindre la SafeZone du mobile : elle vise la case de son Master.
     * @return null si le mobile est déjà adjacent à son Master
     */
    public Direction directionToSafeZone(Pokemon pokemon) {
        Tile masterTile = pokemon.getMaster().getTile();
        Tile current = pokemon.getTile();
        if (masterTile == null || current == null) {
            return null;
        }

        int dx = masterTile.getX() - current.getX();
        int dy = masterTile.getY() - current.getY();

        if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
            return null;
        }

        return Direction.fromDelta(dx, dy);
    }

    /**
     * Remplace un mobile épuisé par un obstacle sur sa case.
     * Le mobile n'a plus de case : il sort de la carte.
     */
    public void convertToObstacle(Pokemon pokemon) {
        Tile current = pokemon.getTile();
        if (current == null) return;
        this.remove(pokemon);
        this.place(new Obstacle(), current);
    }
}