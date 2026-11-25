package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.HashSet;
import java.util.Set;

public class Merge extends Encounter{
    private int firstGain = 0;
    private int secondGain = 0;

    /** Crée une rencontre coopérative entre Pokémon d'une même population. */
    public Merge(Pokemon firstPokemon, Pokemon secondPokemon) {
        super(firstPokemon, secondPokemon);
    }

    /** Met en commun les messages des deux Pokémon sans créer de doublons. */
    @Override
    public void resolve(RandomGenerator random) {
        Set<Message> union = new HashSet<>(_firstPokemon.getMessages());
        union.addAll(_secondPokemon.getMessages());
        firstGain = union.size() - _firstPokemon.getMessages().size();
        secondGain = union.size() - _secondPokemon.getMessages().size();
        _firstPokemon.learnAll(union);
        _secondPokemon.learnAll(union);
    }

    /** Résume les gains de messages obtenus par chaque Pokémon. */
    @Override public String describe() {
        return _firstPokemon.getLabel() + " + " + _secondPokemon.getLabel() + " : union ("
                + _firstPokemon.getLabel() + " +" + firstGain + ", "
                + _secondPokemon.getLabel() + " +" + secondGain + ")";
    }


}
