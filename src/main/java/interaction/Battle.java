package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.HashSet;
import java.util.List;

public class Battle extends Encounter {
    private Pokemon winner;
    private int stolenCount = 0;

    /** Crée un combat entre Pokémon d'alliances opposées. */
    public Battle(Pokemon firstPokemon, Pokemon secondPokemon) {
        super(firstPokemon, secondPokemon);
    }

    /** Désigne un vainqueur selon l'énergie et lui transfère des messages du perdant. */
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
        stolenCount = taken.size();
    }

    /** Résume le vainqueur et le nombre de messages dérobés. */
    @Override public String describe() {
        return _firstPokemon.getLabel() + " vs " + _secondPokemon.getLabel() + " : "
                + winner.getLabel() + " gagne et prend " + stolenCount + " message(s)";
    }
}
