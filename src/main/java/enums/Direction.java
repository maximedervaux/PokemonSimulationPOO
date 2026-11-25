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
    private static final List<Direction> ORTHOGONALS = List.of(NORTH, EAST, SOUTH, WEST);
    private static final List<Direction> DIAGONALS = List.of(NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST);

    /** Crée une direction à partir de son déplacement horizontal et vertical. */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** Retourne le déplacement horizontal associé à la direction. */
    public int getDx() {
        return dx;
    }

    /** Retourne le déplacement vertical associé à la direction. */
    public int getDy() {
        return dy;
    }

    /** Retourne la direction obtenue après une rotation horaire par huitièmes de tour. */
    public Direction rotated(int eighths) {
        return ALL.get(Math.floorMod(ordinal() + eighths, ALL.size()));
    }

    /** Retourne les huit directions de déplacement. */
    public static List<Direction> all() {
        return ALL;
    }

    /** Retourne les quatre directions horizontales et verticales. */
    public static List<Direction> orthogonals() {
        return ORTHOGONALS;
    }

    /** Retourne les quatre directions diagonales. */
    public static List<Direction> diagonals() {
        return DIAGONALS;
    }

    /** Convertit un vecteur de déplacement en direction normalisée. */
    public static Direction fromDelta(int dx, int dy) {
        int sx = Integer.signum(dx);
        int sy = Integer.signum(dy);
        for (Direction d : Direction.all()) {
            if (d.dx == sx && d.dy == sy) return d;
        }
        return null;
    }
}
