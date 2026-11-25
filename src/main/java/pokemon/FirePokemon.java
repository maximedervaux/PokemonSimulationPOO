package pokemon;

import enums.Direction;
import master.MasterFire;

import java.util.List;

public class FirePokemon extends GoodPokemon{
    private static final int MAX_EP = 100;
    private static int instanceCount = 0;

    /** Crée un Pokémon Fire rattaché au Master Fire. */
    public FirePokemon() {
        super(MasterFire.getInstance(), MAX_EP);
        FirePokemon.instanceCount++;
    }

    /** Retourne le nombre de Pokémon Fire créés. */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /** Retourne le symbole console de la population Fire. */
    @Override
    public String getSymbol() { return "F"; }

    /** Se déplace comme un fou : en diagonale. */
    @Override
    protected List<Direction> allowedDirections() {
        return Direction.diagonals();
    }
}
