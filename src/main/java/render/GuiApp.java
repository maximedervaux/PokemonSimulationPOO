package render;

import simulation.Simulation;
import utils.Config;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.UncheckedIOException;

public class GuiApp {
    private static final int MIN_WIDTH = 780;
    private static final int MIN_HEIGHT = 600;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuiApp::start);
    }

    private static void start() {
        Config config;
        Simulation sim;
        try {
            config = Config.load();
            sim = new Simulation(config);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        GuiRenderer renderer = new GuiRenderer(sim);
        renderer.render(sim);

        JFrame frame = new JFrame("Pokemon Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(renderer);
        frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        frame.pack();

        // Sur un petit écran, la fenêtre ne dépasse pas la zone disponible
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        frame.setSize(Math.min(frame.getWidth(), screen.width), Math.min(frame.getHeight(), screen.height));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer((int) config.getLong("simulation.step.delay.ms"), null);
        timer.addActionListener(e -> {
            if (sim.isFinished()) {
                renderer.renderFinal(sim);
                timer.stop();
                return;
            }
            sim.step();
            renderer.render(sim);
        });
        timer.start();
    }
}
