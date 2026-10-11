package pokemon;

import environment.GameMap;
import master.MasterWater;
import utils.RandomGenerator;

public class WaterPokemon extends GoodPokemon{

    private static final int MAX_EP = 100;

    public WaterPokemon() {
        super(MasterWater.getInstance(), MAX_EP);   // impose le bon Master
    }

    @Override
    public void move(GameMap map, RandomGenerator random) {
        // TODO
    }

    @Override
    public String getSymbol() { return "W"; }
}
