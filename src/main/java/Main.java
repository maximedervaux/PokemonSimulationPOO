import simulation.Simulation;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Simulation sim = new Simulation(4, 36);
        sim.run();
    }
}