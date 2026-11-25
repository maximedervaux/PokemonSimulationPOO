package pokemon;

import enums.Direction;
import master.MasterGhost;

import java.util.List;

public class GhostPokemon extends BadPokemon{
    private static final int MAX_EP = 100;
    private static int instanceCount = 0;

    /** Crée un Pokémon Ghost rattaché au Master Ghost. */
    public GhostPokemon() {
        super(MasterGhost.getInstance(), MAX_EP);
        GhostPokemon.instanceCount++;
    }

    /** Retourne le nombre de Pokémon Ghost créés. */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /** Retourne le symbole console de la population Ghost. */
    @Override
    public String getSymbol() { return "G"; }

    /** Se déplace comme une dame : dans les 8 directions. */
    @Override
    protected List<Direction> allowedDirections() {
        return Direction.all();
    }
}
