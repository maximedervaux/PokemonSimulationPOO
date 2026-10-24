package interaction;

import pokemon.Pokemon;
import utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public abstract class Encounter {
    protected final Pokemon _firstPokemon;
    protected final Pokemon _secondPokemon;

    protected Encounter(Pokemon firstPokemon, Pokemon secondPokemon) {
        this._firstPokemon = firstPokemon;
        this._secondPokemon = secondPokemon;
    }

    public static Encounter between(Pokemon firstPokemon, Pokemon secondPokemon) {
        if (firstPokemon.isSamePopulation(secondPokemon)) return new Merge(firstPokemon, secondPokemon);
        if (firstPokemon.isSameAlliance(secondPokemon))   return new Exchange(firstPokemon, secondPokemon);
        return new Battle(firstPokemon, secondPokemon);
    }

    public abstract void resolve(RandomGenerator random);

    public abstract String describe();

    protected static List<Message> missing(Pokemon from, Pokemon to) {
        List<Message> list = new ArrayList<>(from.getMessages());
        list.removeAll(to.getMessages());
        return list;
    }


}
