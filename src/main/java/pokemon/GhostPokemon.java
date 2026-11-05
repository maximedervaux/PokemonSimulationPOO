package pokemon;

import master.MasterGhost;

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
