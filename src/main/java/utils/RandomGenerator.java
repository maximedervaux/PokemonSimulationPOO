package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RandomGenerator {
    private final Random _random;

    /** Initialise le générateur avec la graine de la simulation. */
    public RandomGenerator(long seed) {
        this._random = new Random(seed);
    }

    /** Renvoie vrai avec la probabilité indiquée. */
    public boolean nextBoolean(double prob) {
        return _random.nextDouble() < prob;
    }

    /** Mélange la liste en utilisant le générateur de la simulation. */
    public <T> void shuffle(List<T> list) {
        Collections.shuffle(list, _random);
    }

    /** Retourne au plus {@code k} éléments choisis aléatoirement sans remplacement. */
    public <T> List<T> pickSubset(List<T> list, int k) {
        List<T> copy = new ArrayList<>(list);
        shuffle(copy);
        return new ArrayList<>(copy.subList(0, Math.min(k, copy.size())));
    }

    /** Choisit un élément au hasard ou échoue si la liste est vide. */
    public <T> T pickRandom(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Cannot pick from an empty list");
        }
        return list.get(_random.nextInt(list.size()));
    }

    /** Tire un entier dans l'intervalle inclusif indiqué. */
    public int nextInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        return min + _random.nextInt(max - min + 1);
    }
}
