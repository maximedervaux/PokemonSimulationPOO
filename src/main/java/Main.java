import render.ConsoleRenderer;
import render.GuiApp;
import render.Renderer;
import simulation.Simulation;
import utils.Config;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("gui")) {
            GuiApp.main(args);
        } else {
            Config config = Config.load();
            run(new Simulation(config), new ConsoleRenderer(), config.getLong("simulation.step.delay.ms"));
        }
    }

    private static void run(Simulation sim, Renderer renderer, long delayMs) {
        renderer.render(sim);
        try {
            while (!sim.isFinished()) {
                sim.step();
                renderer.render(sim);
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        renderer.renderFinal(sim);
    }
}
