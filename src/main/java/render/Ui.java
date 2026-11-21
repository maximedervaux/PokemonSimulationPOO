package render;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Palette, police et sprites de l'interface graphique. */
final class Ui {
    static final Color BACKGROUND = new Color(0x1A1C2C);
    static final Color PANEL = new Color(0x262B44);
    static final Color BORDER = new Color(0x566C86);
    static final Color TEXT = new Color(0xF4F4F4);
    static final Color MUTED = new Color(0x94B0C2);
    static final Color GOLD = new Color(0xFFCD75);
    static final Color EP_OK = new Color(0xA7F070);
    static final Color EP_LOW = new Color(0xE43B44);

    private static final Font FONT = loadFont();
    private static final Map<String, BufferedImage> SPRITES = new HashMap<>();

    private Ui() { }

    static Font font(float size) {
        return FONT.deriveFont(size);
    }

    /** Charge sprites/{name}.png une seule fois. */
    static BufferedImage sprite(String name) {
        return SPRITES.computeIfAbsent(name, Ui::loadSprite);
    }

    /** Clé d'un groupe dans le nom des sprites, par exemple "fire". */
    static String key(String groupName) {
        return groupName.toLowerCase(Locale.ROOT);
    }

    static Color teamColor(String groupName) {
        return switch (groupName) {
            case "Fire"  -> new Color(0xEF7D57);
            case "Water" -> new Color(0x41A6F6);
            case "Ghost" -> new Color(0xC9A8F0);
            default      -> new Color(0x38B764);
        };
    }

    /** Barre pleine à ratio, sur fond sombre. */
    static void drawBar(Graphics2D g, int x, int y, int width, int height, double ratio, Color fill) {
        g.setColor(BACKGROUND);
        g.fillRect(x, y, width, height);
        g.setColor(fill);
        g.fillRect(x, y, (int) Math.round(width * ratio), height);
    }

    private static Font loadFont() {
        try (InputStream in = Ui.class.getResourceAsStream("/fonts/VT323-Regular.ttf")) {
            return Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (FontFormatException e) {
            throw new IllegalStateException(e);
        }
    }

    private static BufferedImage loadSprite(String name) {
        try (InputStream in = Ui.class.getResourceAsStream("/sprites/" + name + ".png")) {
            if (in == null) {
                throw new IllegalArgumentException("Sprite introuvable : " + name);
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
