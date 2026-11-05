import render.ConsoleRenderer;
import render.GuiApp;
import simulation.Simulation;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("gui")) {
            GuiApp.main(args);
        } else {
            new Simulation(4, 48).run(new ConsoleRenderer());
        }
    }
}