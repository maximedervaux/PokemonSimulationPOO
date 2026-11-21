package render;

import environment.GameMap;
import environment.Occupant;
import environment.SafeZone;
import enums.Corner;
import master.Master;
import pokemon.Pokemon;
import simulation.Simulation;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

/** Carte de la simulation, mise à l'échelle de la place disponible : terrain, occupants et résultat final. */
class MapView extends JComponent {
    private static final int DEFAULT_CELL = 28;
    private static final int DEFAULT_MAX_SIZE = 700;
    private static final int TERRAIN_SIZE = 1400;
    private static final int MIN_POKEMON_SPRITE = 28;
    private static final int MIN_MASTER_SPRITE = 24;
    private static final int RESULT_SPRITE = 56;
    private static final int RESULT_FONT_SIZE = 24;

    private final Simulation sim;
    private final BufferedImage terrain;
    private String result;
    private List<Master> winners = List.of();

    MapView(Simulation sim) {
        this.sim = sim;
        GameMap map = sim.getMap();
        int longestSide = Math.max(map.getWidth(), map.getHeight());
        double cell = Math.min(DEFAULT_CELL, (double) DEFAULT_MAX_SIZE / longestSide);
        setPreferredSize(new Dimension((int) (map.getWidth() * cell), (int) (map.getHeight() * cell)));
        terrain = drawTerrain(map);
    }

    void showResult() {
        result = ReportText.result(sim);
        winners = sim.getWinners();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        GameMap map = sim.getMap();
        double cell = Math.min((double) getWidth() / map.getWidth(), (double) getHeight() / map.getHeight());
        int mapWidth = scaled(map.getWidth(), cell);
        int mapHeight = scaled(map.getHeight(), cell);

        // Carte centrée ; rien n'est dessiné en dehors de ses bords
        Graphics2D g = (Graphics2D) graphics.create((getWidth() - mapWidth) / 2, (getHeight() - mapHeight) / 2,
                mapWidth, mapHeight);
        g.drawImage(terrain, 0, 0, mapWidth, mapHeight, null);
        drawZoneBorders(g, map, cell);

        // Obstacles d'abord : ils ne doivent pas masquer les sprites agrandis des mobiles
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Occupant occupant = map.getTile(x, y).getOccupant();
                if (occupant != null && occupant.isObstacle()) {
                    g.drawImage(Ui.sprite((x * 31 + y * 17) % 3 == 0 ? "tree" : "rock"),
                            scaled(x, cell), scaled(y, cell), scaled(x + 1, cell) - scaled(x, cell),
                            scaled(y + 1, cell) - scaled(y, cell), null);
                }
            }
        }
        for (Master master : sim.getMasters()) {
            drawSprite(g, "master_" + Ui.key(master.getGroupName()), master, Math.max(MIN_MASTER_SPRITE, (int) cell), cell);
        }
        for (Pokemon p : sim.getMobiles()) {
            drawSprite(g, "pokemon_" + Ui.key(p.getMaster().getGroupName()), p, pokemonSize(cell), cell);
        }
        for (Pokemon p : sim.getMobiles()) {
            drawEnergyBar(g, p, cell);
        }
        g.dispose();

        if (result != null) {
            drawResult((Graphics2D) graphics);
        }
    }

    private static int scaled(int tiles, double cell) {
        return (int) Math.round(tiles * cell);
    }

    private static int pokemonSize(double cell) {
        return Math.max(MIN_POKEMON_SPRITE, (int) (2 * cell));
    }

    /** Construit une seule fois, puis mis à l'échelle à chaque affichage : le redimensionnement reste fluide. */
    private BufferedImage drawTerrain(GameMap map) {
        double cell = (double) TERRAIN_SIZE / Math.max(map.getWidth(), map.getHeight());
        BufferedImage image = new BufferedImage(scaled(map.getWidth(), cell), scaled(map.getHeight(), cell),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                SafeZone zone = map.getSafeZoneAt(x, y);
                String ground = zone == null ? "grass" : "floor_" + Ui.key(zone.getMaster().getGroupName());
                int variant = (x * 7 + y * 13) % 3;
                g.drawImage(Ui.sprite(ground + "_" + variant), scaled(x, cell), scaled(y, cell),
                        scaled(x + 1, cell) - scaled(x, cell), scaled(y + 1, cell) - scaled(y, cell), null);
            }
        }
        g.dispose();
        return image;
    }

    private void drawZoneBorders(Graphics2D g, GameMap map, double cell) {
        g.setStroke(new BasicStroke(2));
        for (Corner corner : Corner.values()) {
            SafeZone zone = map.getSafeZone(corner);
            g.setColor(Ui.teamColor(zone.getMaster().getGroupName()));
            g.drawRect(scaled(zone.getX0(), cell) + 1, scaled(zone.getY0(), cell) + 1,
                    scaled(zone.getWidth(), cell) - 2, scaled(zone.getHeight(), cell) - 2);
        }
    }

    /** Position du sprite : centré sur la case de l'occupant, et gardé entier à l'intérieur de la carte. */
    private Rectangle spriteBounds(Occupant occupant, int size, double cell) {
        GameMap map = sim.getMap();
        int x = scaled(occupant.getTile().getX(), cell) + (int) (cell - size) / 2;
        int y = scaled(occupant.getTile().getY(), cell) + (int) (cell - size) / 2;
        return new Rectangle(
                Math.max(0, Math.min(x, scaled(map.getWidth(), cell) - size)),
                Math.max(0, Math.min(y, scaled(map.getHeight(), cell) - size)),
                size, size);
    }

    private void drawSprite(Graphics2D g, String sprite, Occupant occupant, int size, double cell) {
        Rectangle bounds = spriteBounds(occupant, size, cell);
        g.drawImage(Ui.sprite(sprite), bounds.x, bounds.y, size, size, null);
    }

    /** Barre d'EP des mobiles (rouge sous 20 %), au-dessus de tous les sprites. */
    private void drawEnergyBar(Graphics2D g, Pokemon p, double cell) {
        int size = pokemonSize(cell);
        Rectangle bounds = spriteBounds(p, size, cell);
        int width = size * 2 / 5;
        Ui.drawBar(g, bounds.x + (size - width) / 2, bounds.y + size / 2 + size / 5, width, Math.max(2, size / 18),
                p.getEpRatio(), p.isLowEnergy() ? Ui.EP_LOW : Ui.EP_OK);
    }

    private void drawResult(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Réduit le texte s'il dépasse de la carte
        g.setFont(Ui.font(RESULT_FONT_SIZE));
        int available = getWidth() - 48;
        int textWidth = g.getFontMetrics().stringWidth(result);
        if (textWidth > available) {
            g.setFont(Ui.font((float) RESULT_FONT_SIZE * available / textWidth));
        }
        FontMetrics metrics = g.getFontMetrics();

        int spritesWidth = winners.size() * RESULT_SPRITE;
        int boxWidth = Math.max(metrics.stringWidth(result), spritesWidth) + 48;
        int boxHeight = RESULT_SPRITE + metrics.getHeight() + 44;
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (getHeight() - boxHeight) / 2;

        g.setColor(Ui.PANEL);
        g.fillRect(boxX, boxY, boxWidth, boxHeight);
        g.setColor(Ui.GOLD);
        g.setStroke(new BasicStroke(3));
        g.drawRect(boxX, boxY, boxWidth, boxHeight);

        int spriteX = (getWidth() - spritesWidth) / 2;
        for (Master winner : winners) {
            g.drawImage(Ui.sprite("master_" + Ui.key(winner.getGroupName())), spriteX, boxY + 14,
                    RESULT_SPRITE, RESULT_SPRITE, null);
            spriteX += RESULT_SPRITE;
        }
        g.drawString(result, (getWidth() - metrics.stringWidth(result)) / 2,
                boxY + RESULT_SPRITE + 22 + metrics.getAscent());
    }
}
