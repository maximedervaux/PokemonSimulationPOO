package environment;

import enums.Corner;
import master.Master;

public class SafeZone {
    private final int _x0, _y0, _width, _height;
    private Master _master;

    public SafeZone(int x0, int y0, int width, int height) {
        this._x0 = x0; this._y0 = y0;
        this._width = width; this._height = height;
    }

    public int getY0() {
        return _y0;
    }

    public int getX0() {
        return _x0;
    }

    public boolean contains(int x, int y) {
        return x >= _x0 && x < _x0 + _width && y >= _y0 && y < _y0 + _height;
    }

    public Master getMaster() {
        return _master;
    }
    public void setMaster(Master m) {
        this._master = m;
    }

    public Tile getOuterCornerTile(GameMap map, Corner corner) {
        int x = (corner == Corner.TOP_LEFT || corner == Corner.BOTTOM_LEFT)
                ? _x0 : _x0 + _width - 1;
        int y = (corner == Corner.TOP_LEFT || corner == Corner.TOP_RIGHT)
                ? _y0 : _y0 + _height - 1;
        return map.getTile(x, y);
    }

    public int getHeight() {
        return this._height;
    }

    public int getWidth(){
        return this._width;
    }
}