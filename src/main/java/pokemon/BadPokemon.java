package pokemon;

import master.Master;

public abstract class BadPokemon extends Pokemon{
    /** Initialise un Pokémon rattaché à l'alliance Bad. */
    protected BadPokemon(Master master, int maxEp) {
        super(master, maxEp);
    }

    /** Indique que ce Pokémon n'appartient pas à l'alliance Good. */
    @Override
    public boolean isGood(){
        return false;
    }
}
