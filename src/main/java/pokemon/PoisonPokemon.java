package pokemon;

import enums.Direction;
import master.MasterPoison;
import utils.RandomGenerator;

import java.util.List;

public class PoisonPokemon extends BadPokemon {
    private static final int MAX_EP = 100;
    private static int instanceCount = 0;

    public PoisonPokemon() {
        super(MasterPoison.getInstance(), MAX_EP);
        PoisonPokemon.instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public String getSymbol() { return "P"; }

    /** Se déplace comme un roi : dans les 8 directions, une case à la fois. */
    @Override
    protected List<Direction> allowedDirections() {
        return Direction.all();
    }

    @Override
    protected int chooseDistance(RandomGenerator random) {
        return 1;
    }
}
