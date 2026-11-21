package render;

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
import java.awt.RenderingHints;
import java.util.Comparator;
import java.util.List;

/** Fiche d'un groupe : score du Master et état de ses agents actifs. */
class TeamCard extends JComponent {
    private static final int PADDING = 10;
    private static final int SPRITE = 28;
    private static final int HEADER_HEIGHT = 46;
    static final int ROW_HEIGHT = 18;
    private static final int LABEL_WIDTH = 28;
    private static final int STATS_WIDTH = 76;

    private final Simulation sim;
    private final Master master;
    private final Color color;

    TeamCard(Simulation sim, Master master) {
        this.sim = sim;
        this.master = master;
        this.color = Ui.teamColor(master.getGroupName());
        setPreferredSize(new Dimension(0, HEADER_HEIGHT + agents().size() * ROW_HEIGHT + PADDING));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Ui.PANEL);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);

        drawHeader(g);

        int y = HEADER_HEIGHT;
        for (Pokemon agent : agents()) {
            drawAgent(g, agent, y);
            y += ROW_HEIGHT;
        }
    }

    private void drawHeader(Graphics2D g) {
        g.drawImage(Ui.sprite("master_" + Ui.key(master.getGroupName())), PADDING, PADDING, SPRITE, SPRITE, null);

        int textX = PADDING + SPRITE + 6;
        g.setFont(Ui.font(22));
        g.setColor(color);
        g.drawString(master.getGroupName(), textX, PADDING + 14);

        int total = sim.getMessageCount();
        String score = master.getScore() + "/" + total;
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(Ui.TEXT);
        g.drawString(score, getWidth() - PADDING - metrics.stringWidth(score), PADDING + 14);

        Ui.drawBar(g, textX, PADDING + 20, getWidth() - PADDING - textX, 6,
                (double) master.getScore() / total, color);
    }

    private void drawAgent(Graphics2D g, Pokemon agent, int y) {
        g.setFont(Ui.font(16));
        FontMetrics metrics = g.getFontMetrics();
        int baseline = y + metrics.getAscent();

        g.setColor(Ui.MUTED);
        g.drawString(agent.getLabel(), PADDING, baseline);

        Ui.drawBar(g, PADDING + LABEL_WIDTH, y + 4, getWidth() - 2 * PADDING - LABEL_WIDTH - STATS_WIDTH, 7,
                agent.getEpRatio(), agent.isLowEnergy() ? Ui.EP_LOW : Ui.EP_OK);

        String stats = agent.getEp() + "EP " + agent.getMessages().size() + "msg";
        g.setColor(Ui.TEXT);
        g.drawString(stats, getWidth() - PADDING - metrics.stringWidth(stats), baseline);
    }

    private List<Pokemon> agents() {
        return sim.getMobiles().stream()
                .filter(p -> p.getMaster() == master)
                .sorted(Comparator.comparingInt(Pokemon::getId))
                .toList();
    }
}
