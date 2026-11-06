package pokemon;

import enums.Direction;
import master.MasterGhost;

import java.util.List;

public class GhostPokemon extends BadPokemon{
    private static final int MAX_EP = 100;
    private static int instanceCount = 0;

    public GhostPokemon() {
        super(MasterGhost.getInstance(), MAX_EP);
        GhostPokemon.instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public String getSymbol() { return "G"; }

    /** Se déplace comme une dame : dans les 8 directions. */
    @Override
    protected List<Direction> allowedDirections() {
        return Direction.all();
    }
}
