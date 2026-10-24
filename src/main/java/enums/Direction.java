package enums;

import java.util.List;

public enum Direction {
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    private final int dx;
    private final int dy;

    private static final List<Direction> ALL = List.of(values());

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public static List<Direction> all() {
        return ALL;
    }

    public static Direction fromDelta(int dx, int dy) {
        int sx = Integer.signum(dx);
        int sy = Integer.signum(dy);
        for (Direction d : Direction.all()) {
            if (d.dx == sx && d.dy == sy) return d;
        }
        return null;
    }
}