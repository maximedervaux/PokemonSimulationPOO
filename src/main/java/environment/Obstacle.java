package environment;

public class Obstacle extends Occupant{

    /** Indique que cet occupant est un obstacle infranchissable. */
    @Override public boolean isObstacle() { return true; }

    /** Retourne le symbole de l'obstacle dans le rendu console. */
    @Override
    public String getSymbol() {
        return "O";
    }
}
