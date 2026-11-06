package render;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;
import simulation.Simulation;
import utils.Config;

import java.io.IOException;

public class GuiApp extends GameApplication {
    private Config config;
    private Simulation sim;
    private GuiRenderer renderer;

    @Override
    protected void initSettings(GameSettings settings) {
        try {
            config = Config.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        settings.setWidth(config.getInt("map.width") * GuiRenderer.CELL + GuiRenderer.PANEL_WIDTH);
        settings.setHeight(config.getInt("map.height") * GuiRenderer.CELL + GuiRenderer.HUD_HEIGHT);
        settings.setTitle("Pokemon Simulation");
        settings.setMainMenuEnabled(false);
    }

    @Override
    protected void initGame() {
        try {
            sim = new Simulation(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        renderer = new GuiRenderer();
        FXGL.getGameScene().addUINode(renderer.getRoot());
        renderer.render(sim);

        FXGL.run(() -> {
            if (!sim.isFinished()) {
                sim.step();
                renderer.render(sim);
                if (sim.isFinished()) {
                    renderer.renderFinal(sim);
                }
            }
        }, Duration.millis(config.getLong("simulation.step.delay.ms")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
