package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RandomGenerator {
    private final Random _random;

    public RandomGenerator(long seed) {
        this._random = new Random(seed);
    }

    public boolean nextBoolean(double prob) {
        return _random.nextDouble() < prob;
    }

    public <T> void shuffle(List<T> list) {
        Collections.shuffle(list, _random);
    }

    public <T> List<T> pickSubset(List<T> list, int k) {
        List<T> copy = new ArrayList<>(list);
        shuffle(copy);
        return new ArrayList<>(copy.subList(0, Math.min(k, copy.size())));
    }

    public <T> T pickRandom(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Cannot pick from an empty list");
        }
        return list.get(_random.nextInt(list.size()));
    }

    public int nextInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        return min + _random.nextInt(max - min + 1);
    }
}