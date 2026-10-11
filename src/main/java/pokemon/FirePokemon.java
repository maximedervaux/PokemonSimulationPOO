package pokemon;

import environment.GameMap;
import master.MasterFire;
import utils.RandomGenerator;

public class FirePokemon extends GoodPokemon{
    private static final int MAX_EP = 100;

    public FirePokemon() {
        super(MasterFire.getInstance(), MAX_EP);   // impose le bon Master
    }

    @Override
    public void move(GameMap map, RandomGenerator random) {
        // TODO
    }

    @Override
    public String getSymbol() { return "F"; }
}
