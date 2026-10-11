package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RandomGenerator {
    private final Random _random;
    private final long _seed;

    public RandomGenerator(long seed) {
        this._seed = seed;
        this._random = new Random(seed);
    }

    public long getSeed() {
        return _seed;
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
}