package pokemon;

import master.MasterFire;

public class FirePokemon extends GoodPokemon{
    private static final int MAX_EP = 100;
    private static int instanceCount = 0;

    public FirePokemon() {
        super(MasterFire.getInstance(), MAX_EP);
        FirePokemon.instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public String getSymbol() { return "F"; }
}
