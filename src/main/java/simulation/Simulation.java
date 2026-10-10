package simulation;

import environment.Map;
import utils.RandomGenerator;

public class Simulation {
    private final Map _map;
    private RandomGenerator _random;

    public Simulation(){
        _random = new RandomGenerator(1111111);
        _map = new Map(25, 25, _random, 0.2);
    }

    public void run(){
        _map.printDebug();
    }
}