package pokemon;

import environment.GameMap;
import master.MasterGhost;
import utils.RandomGenerator;

public class GhostPokemon extends BadPokemon{
    private static final int MAX_EP = 100;

    public GhostPokemon() {
        super(MasterGhost.getInstance(), MAX_EP);   // impose le bon Master
    }

    @Override
    public void move(GameMap map, RandomGenerator random) {
        // TODO
    }

    @Override
    public String getSymbol() { return "G"; }
}
