package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.HashSet;
import java.util.List;

public class Exchange extends Encounter{
    private int firstGain = 0;
    private int secondGain = 0;

    /** Crée un échange entre Pokémon d'une même alliance. */
    public Exchange(Pokemon firstPokemon, Pokemon secondPokemon) {
        super(firstPokemon, secondPokemon);
    }

    /** Transfère aléatoirement des messages manquants dans les deux sens. */
    @Override
    public void resolve(RandomGenerator random) {
        List<Message> aToB = missing(_firstPokemon, _secondPokemon);
        List<Message> bToA = missing(_secondPokemon, _firstPokemon);
        secondGain = transfer(aToB, _secondPokemon, random);
        firstGain = transfer(bToA, _firstPokemon, random);
    }

    /** Transfère un sous-ensemble de messages manquants au destinataire. */
    private int transfer(List<Message> candidates, Pokemon receiver, RandomGenerator random) {
        if (candidates.isEmpty()) {
            return 0;
        }
        int k = random.nextInt(1, candidates.size());
        receiver.learnAll(new HashSet<>(random.pickSubset(candidates, k)));
        return k;
    }

    /** Résume le nombre de messages échangés par chaque Pokémon. */
    @Override public String describe() {
        return _firstPokemon.getLabel() + " <-> " + _secondPokemon.getLabel() + " : échange ("
                + _firstPokemon.getLabel() + " +" + firstGain + ", "
                + _secondPokemon.getLabel() + " +" + secondGain + ")";
    }
}
