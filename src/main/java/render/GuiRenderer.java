package render;

import environment.GameMap;
import environment.Occupant;
import environment.SafeZone;
import environment.Tile;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import master.Master;
import pokemon.Pokemon;
import simulation.Simulation;

import java.util.ArrayDeque;
import java.util.Deque;

public class GuiRenderer implements Renderer {
    public static final int CELL = 28;
    public static final int HUD_HEIGHT = 40;
    public static final int PANEL_WIDTH = 320;
    private static final int PANEL_MARGIN = 10;
    private static final int JOURNAL_SIZE = 12;

    private final Group root = new Group();
    private final Deque<String> journal = new ArrayDeque<>();

    /** Noeud à ajouter à la scène FXGL (FXGL.getGameScene().addUINode(...)). */
    public Group getRoot() {
        return root;
    }

    @Override
    public void render(Simulation sim) {
        root.getChildren().clear();
        GameMap map = sim.getMap();

        Rectangle background = new Rectangle(map.getWidth() * CELL + PANEL_WIDTH, map.getHeight() * CELL + HUD_HEIGHT);
        background.setFill(Color.rgb(25, 25, 30));
        root.getChildren().add(background);

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                drawTile(map, x, y);
            }
        }
        drawHud(sim);
        drawPanel(sim);
    }

    /** Affiche le résultat en haut du panneau, jusqu'à la fermeture de la fenêtre. */
    @Override
    public void renderFinal(Simulation sim) {
        Text result = new Text(sim.getMap().getWidth() * CELL + PANEL_MARGIN, 16, ReportText.result(sim));
        result.setFill(Color.GOLD);
        result.setFont(Font.font("Monospaced", 13));
        result.setWrappingWidth(PANEL_WIDTH - 2 * PANEL_MARGIN);
        root.getChildren().add(result);
    }

    private void drawTile(GameMap map, int x, int y) {
        double px = x * CELL;
        double py = HUD_HEIGHT + y * CELL;

        Rectangle background = new Rectangle(px, py, CELL - 1, CELL - 1);
        background.setFill(backgroundColor(map, x, y));
        root.getChildren().add(background);

        Tile tile = map.getTile(x, y);
        Occupant occupant = tile.getOccupant();
        if (occupant == null) return;

        Rectangle body = new Rectangle(px + 3, py + 3, CELL - 7, CELL - 7);
        body.setFill(occupantColor(occupant));
        root.getChildren().add(body);

        Text label = new Text(px + 9, py + 19, occupant.getSymbol());
        label.setFill(Color.WHITE);
        label.setFont(Font.font(14));
        root.getChildren().add(label);

        // Barre d'EP sous les mobiles (rouge sous 20 %)
        if (occupant instanceof Pokemon p) {
            double ratio = p.getEpRatio();
            Rectangle bar = new Rectangle(px + 3, py + CELL - 6, (CELL - 7) * ratio, 3);
            bar.setFill(p.isLowEnergy() ? Color.RED : Color.LIMEGREEN);
            root.getChildren().add(bar);
        }
    }

    private Color backgroundColor(GameMap map, int x, int y) {
        SafeZone zone = map.getSafeZoneAt(x, y);
        if (zone == null) return Color.rgb(40, 60, 40);          // herbe

        return switch (zone.getMaster().getGroupName()) {
            case "Fire"  -> Color.rgb(100, 45, 45);
            case "Water" -> Color.rgb(40, 65, 110);
            case "Ghost" -> Color.rgb(75, 45, 100);
            default      -> Color.rgb(65, 85, 45);
        };
    }

    private Color occupantColor(Occupant o) {
        if (o.isObstacle())      return Color.GRAY;
        if (o instanceof Master) return Color.GOLD;
        return ((Pokemon) o).isGood() ? Color.DODGERBLUE : Color.MEDIUMPURPLE;
    }

    private void drawHud(Simulation sim) {
        Text hud = new Text(10, 26, "Step " + sim.getStep() + "    " + ReportText.scores(sim));
        hud.setFill(Color.WHITE);
        hud.setFont(Font.font(16));
        root.getChildren().add(hud);
    }

    private void drawPanel(Simulation sim) {
        for (String event : sim.getEvents()) {
            journal.addFirst("[" + sim.getStep() + "] " + event);
        }
        while (journal.size() > JOURNAL_SIZE) {
            journal.removeLast();
        }

        StringBuilder sb = new StringBuilder("Instances\n").append(ReportText.instances()).append("\n\n");
        sb.append("Agents épuisés : ").append(sim.getExhaustedCount()).append("\n\n");
        for (Master m : sim.getMasters()) {
            sb.append(m.getGroupName()).append('\n');
            for (String agent : ReportText.agents(sim, m)) {
                sb.append("  ").append(agent).append('\n');
            }
        }
        sb.append("\nJournal\n");
        journal.forEach(line -> sb.append(line).append('\n'));

        Text panel = new Text(sim.getMap().getWidth() * CELL + PANEL_MARGIN, HUD_HEIGHT + 12, sb.toString());
        panel.setFill(Color.WHITE);
        panel.setFont(Font.font("Monospaced", 12));
        panel.setWrappingWidth(PANEL_WIDTH - 2 * PANEL_MARGIN);
        root.getChildren().add(panel);
    }
}