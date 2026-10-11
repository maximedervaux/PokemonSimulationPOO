package environment;

public class Obstacle extends Occupant{

    @Override public boolean isObstacle() { return true; }

    @Override
    public String getSymbol() {
        return "O";
    }
}