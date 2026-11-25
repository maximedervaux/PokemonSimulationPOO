package pokemon;

import enums.Direction;
import master.MasterPoison;
import utils.RandomGenerator;

import java.util.List;

public class PoisonPokemon extends BadPokemon {
    private static final int MAX_EP = 100;
    private static int instanceCount = 0;

    /** Crée un Pokémon Poison rattaché au Master Poison. */
    public PoisonPokemon() {
        super(MasterPoison.getInstance(), MAX_EP);
        PoisonPokemon.instanceCount++;
    }

    /** Retourne le nombre de Pokémon Poison créés. */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /** Retourne le symbole console de la population Poison. */
    @Override
    public String getSymbol() { return "P"; }

    /** Se déplace comme un roi : dans les 8 directions, une case à la fois. */
    @Override
    protected List<Direction> allowedDirections() {
        return Direction.all();
    }

    /** Limite le déplacement de ce Pokémon à une seule case par tour. */
    @Override
    protected int chooseDistance(RandomGenerator random) {
        return 1;
    }
}
