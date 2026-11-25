package pokemon;

import master.Master;

public abstract class GoodPokemon extends Pokemon {
    /** Initialise un Pokémon rattaché à l'alliance Good. */
    protected GoodPokemon(Master master, int maxEp) {
        super(master, maxEp);
    }

    /** Indique l'appartenance à l'alliance Good. */
    @Override
    public boolean isGood(){
        return true;
    }
}
