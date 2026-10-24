package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.HashSet;
import java.util.List;

public class Exchange extends Encounter{

    public Exchange(Pokemon firstPokemon, Pokemon secondPokemon) {
        super(firstPokemon, secondPokemon);
    }

    @Override
    public void resolve(RandomGenerator random) {
        List<Message> aToB = missing(_firstPokemon, _secondPokemon);
        List<Message> bToA = missing(_secondPokemon, _firstPokemon);
        transfer(aToB, _secondPokemon, random);
        transfer(bToA, _firstPokemon, random);
    }

    private void transfer(List<Message> candidates, Pokemon receiver, RandomGenerator random) {
        if (candidates.isEmpty()) {
            return;
        }
        int k = random.nextInt(1, candidates.size());
        receiver.learnAll(new HashSet<>(random.pickSubset(candidates, k)));
    }

    @Override public String describe() {
        return _firstPokemon.getSymbol() + " <-> " + _secondPokemon.getSymbol() + " : exchange";
    }
}
