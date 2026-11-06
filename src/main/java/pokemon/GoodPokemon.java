package pokemon;

import master.Master;

public abstract class GoodPokemon extends Pokemon {
    protected GoodPokemon(Master master, int maxEp) {
        super(master, maxEp);
    }

    @Override
    public boolean isGood(){
        return true;
    }
}
