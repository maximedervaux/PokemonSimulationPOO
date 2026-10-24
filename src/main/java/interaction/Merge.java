package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.HashSet;
import java.util.Set;

public class Merge extends Encounter{

    public Merge(Pokemon firstPokemon, Pokemon secondPokemon) {
        super(firstPokemon, secondPokemon);
    }

    @Override
    public void resolve(RandomGenerator random) {
        Set<Message> union = new HashSet<>(_firstPokemon.getMessages());
        union.addAll(_secondPokemon.getMessages());
        _firstPokemon.learnAll(union);
        _secondPokemon.learnAll(union);
    }

    @Override public String describe() {
        return _firstPokemon.getSymbol() + " + " + _secondPokemon.getSymbol() + " : merge";
    }


}
