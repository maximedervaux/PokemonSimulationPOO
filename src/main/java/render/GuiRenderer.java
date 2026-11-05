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

public class GuiRenderer implements Renderer {
    public static final int CELL = 28;
    public static final int HUD_HEIGHT = 40;

    private final Group root = new Group();

    /** Noeud à ajouter à la scène FXGL (FXGL.getGameScene().addUINode(...)). */
    public Group getRoot() {
        return root;
    }

    @Override
    public void render(Simulation sim) {
        root.getChildren().clear();
        GameMap map = sim.getMap();

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                drawTile(map, x, y);
            }
        }
        drawHud(sim);
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

        return switch (zone.getMaster().getSymbol()) {
            case "F" -> Color.rgb(100, 45, 45);
            case "W" -> Color.rgb(40, 65, 110);
            case "G" -> Color.rgb(75, 45, 100);
            default  -> Color.rgb(65, 85, 45);
        };
    }

    private Color occupantColor(Occupant o) {
        if (o.isObstacle())      return Color.GRAY;
        if (o instanceof Master) return Color.GOLD;
        return ((Pokemon) o).isGood() ? Color.DODGERBLUE : Color.MEDIUMPURPLE;
    }

    private void drawHud(Simulation sim) {
        StringBuilder sb = new StringBuilder("Step " + sim.getStep() + "    ");
        for (Master m : sim.getMasters()) {
            sb.append(m.getSymbol()).append(" : ").append(m.getScore()).append("    ");
        }
        Text hud = new Text(10, 26, sb.toString());
        hud.setFill(Color.WHITE);
        hud.setFont(Font.font(16));
        root.getChildren().add(hud);
    }
}