package pokemon;

import master.Master;

public abstract class BadPokemon extends Pokemon{
    protected BadPokemon(Master master, int maxEp) {
        super(master, maxEp);
    }

    @Override
    public boolean isGood(){
        return false;
    }
}
