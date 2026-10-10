package environment;

import master.Master;

public class SafeZone {

    private final int _x0, _y0, _width, _height;
    private Master _master;

    public SafeZone(int x0, int y0, int width, int height) {
        this._x0 = x0; this._y0 = y0;
        this._width = width; this._height = height;
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

}