package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public abstract class Encounter {
    protected final Pokemon _firstPokemon;
    protected final Pokemon _secondPokemon;

    /** Mémorise les deux Pokémon impliqués dans la rencontre. */
    protected Encounter(Pokemon firstPokemon, Pokemon secondPokemon) {
        this._firstPokemon = firstPokemon;
        this._secondPokemon = secondPokemon;
    }

    /** Crée la résolution adaptée à l'équipe et à l'alliance des deux Pokémon. */
    public static Encounter between(Pokemon firstPokemon, Pokemon secondPokemon) {
        if (firstPokemon.isSamePopulation(secondPokemon)) return new Merge(firstPokemon, secondPokemon);
        if (firstPokemon.isSameAlliance(secondPokemon))   return new Exchange(firstPokemon, secondPokemon);
        return new Battle(firstPokemon, secondPokemon);
    }

    /** Résout la rencontre en utilisant le générateur aléatoire partagé. */
    public abstract void resolve(RandomGenerator random);

    /** Décrit brièvement le résultat de la rencontre pour le journal. */
    public abstract String describe();

    /** Liste les messages détenus par {@code from} mais absents de {@code to}. */
    protected static List<Message> missing(Pokemon from, Pokemon to) {
        List<Message> list = new ArrayList<>(from.getMessages());
        list.removeAll(to.getMessages());
        return list;
    }


}
