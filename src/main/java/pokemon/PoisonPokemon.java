package pokemon;

import environment.GameMap;
import master.MasterPoison;
import utils.RandomGenerator;

public class PoisonPokemon extends BadPokemon {
    private static final int MAX_EP = 100;

    public PoisonPokemon() {
        super(MasterPoison.getInstance(), MAX_EP);   // impose le bon Master
    }

    @Override
    public void move(GameMap map, RandomGenerator random) {
        // TODO
    }

    @Override
    public String getSymbol() { return "P"; }
}
