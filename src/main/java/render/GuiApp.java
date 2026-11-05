package render;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;
import simulation.Simulation;

import java.io.IOException;

public class GuiApp extends GameApplication {
    private static final int MAP_SIZE = 25;          // doit correspondre à la taille de GameMap

    private Simulation sim;
    private GuiRenderer renderer;
    private boolean announced = false;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(MAP_SIZE * GuiRenderer.CELL);
        settings.setHeight(MAP_SIZE * GuiRenderer.CELL + GuiRenderer.HUD_HEIGHT);
        settings.setTitle("Pokemon Simulation");
        settings.setMainMenuEnabled(false);
    }

    @Override
    protected void initGame() {
        try {
            sim = new Simulation(4, 48);             // 4 mobiles par population, 48 messages
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
            } else if (!announced) {
                announced = true;
            }
        }, Duration.seconds(0.3));
    }

    public static void main(String[] args) {
        launch(args);
    }
}