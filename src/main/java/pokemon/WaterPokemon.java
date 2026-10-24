package pokemon;

import environment.GameMap;
import master.MasterWater;
import utils.RandomGenerator;

public class WaterPokemon extends GoodPokemon{

    private static final int MAX_EP = 100;
    private static int instanceCount = 0;

    public WaterPokemon() {
        super(MasterWater.getInstance(), MAX_EP);
        WaterPokemon.instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public String getSymbol() { return "W"; }
}
