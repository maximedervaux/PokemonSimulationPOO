package pokemon;

import environment.GameMap;
import master.MasterGhost;
import utils.RandomGenerator;

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
}
