package pokemon;

import environment.GameMap;
import master.MasterPoison;
import utils.RandomGenerator;

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
}
