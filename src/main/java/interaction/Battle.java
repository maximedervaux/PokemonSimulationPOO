package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.HashSet;
import java.util.List;

public class Battle extends Encounter {
    private Pokemon winner;

    public Battle(Pokemon firstPokemon, Pokemon secondPokemon) {
        super(firstPokemon, secondPokemon);
    }

    @Override
    public void resolve(RandomGenerator random) {
        double lifeFirst = _firstPokemon.getEp();
        double lifeSecond = _secondPokemon.getEp();
        double probaFirst = lifeFirst / (lifeFirst + lifeSecond);

        winner = random.nextBoolean(probaFirst) ? _firstPokemon : _secondPokemon;
        Pokemon loser = (winner == _firstPokemon) ? _secondPokemon : _firstPokemon;

        List<Message> stolen = missing(loser, winner);
        if (stolen.isEmpty()) return;

        int k = random.nextInt(1, stolen.size());
        List<Message> taken = random.pickSubset(stolen, k);
        winner.learnAll(new HashSet<>(taken));
        loser.removeMessages(taken);
    }

    @Override public String describe() { return _firstPokemon.getSymbol() + " vs " + _secondPokemon.getSymbol() + " : " + (winner == null ? "?" : winner.getSymbol() + " wins"); }
}
