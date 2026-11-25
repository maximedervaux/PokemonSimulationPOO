package pokemon;

import enums.Direction;
import master.MasterWater;

import java.util.List;

public class WaterPokemon extends GoodPokemon{

    // Le seuil de retour (20 % = 40 EP) couvre les 40 cases du plus long retour en ligne droite
    private static final int MAX_EP = 200;
    private static int instanceCount = 0;

    /** Crée un Pokémon Water rattaché au Master Water. */
    public WaterPokemon() {
        super(MasterWater.getInstance(), MAX_EP);
        WaterPokemon.instanceCount++;
    }

    /** Retourne le nombre de Pokémon Water créés. */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /** Retourne le symbole console de la population Water. */
    @Override
    public String getSymbol() { return "W"; }

    /** Se déplace comme une tour : en ligne droite. */
    @Override
    protected List<Direction> allowedDirections() {
        return Direction.orthogonals();
    }
}
