package render;

import master.Master;
import simulation.Simulation;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayDeque;
import java.util.Deque;

/** Fenêtre principale : carte à gauche, fiches des groupes, compteurs et journal à droite. */
public class GuiRenderer extends JPanel implements Renderer {
    private static final int SIDE_WIDTH = 440;
    private static final int SIDE_MIN_WIDTH = 340;
    private static final int SIDE_MAX_WIDTH = 480;
    private static final int GAP = 8;
    private static final int JOURNAL_SIZE = 12;

    private final MapView mapView;
    private final JComponent side;
    private final JScrollPane teamsScroll = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JLabel stepLabel = label("", 32, Ui.GOLD);
    private final JTextArea stats = textArea();
    private final JTextArea journalArea = textArea();
    private final Deque<String> journal = new ArrayDeque<>();

    public GuiRenderer(Simulation sim) {
        super(new BorderLayout());
        setBackground(Ui.BACKGROUND);

        mapView = new MapView(sim);
        side = buildSide(sim);
        add(buildHeader(), BorderLayout.NORTH);
        add(mapView, BorderLayout.CENTER);
        add(side, BorderLayout.EAST);
    }

    /** Le panneau suit la largeur de la fenêtre, entre deux limites ; la carte prend le reste. */
    @Override
    public void doLayout() {
        side.setPreferredSize(new Dimension(Math.clamp(getWidth() * 3L / 8, SIDE_MIN_WIDTH, SIDE_MAX_WIDTH), 0));
        // Les fiches défilent au-delà de la moitié de la hauteur : le journal garde toujours sa place
        int teamsHeight = teamsScroll.getViewport().getView().getPreferredSize().height;
        teamsScroll.setPreferredSize(new Dimension(0, Math.min(teamsHeight, getHeight() / 2)));
        teamsScroll.invalidate();
        super.doLayout();
    }

    @Override
    public void render(Simulation sim) {
        stepLabel.setText("Étape " + sim.getStep());
        stats.setText("Agents épuisés : " + sim.getExhaustedCount() + "\n" + ReportText.instances());
        updateJournal(sim);
        repaint();
    }

    /** Affiche le résultat par-dessus la carte, jusqu'à la fermeture de la fenêtre. */
    @Override
    public void renderFinal(Simulation sim) {
        mapView.showResult();
    }

    private void updateJournal(Simulation sim) {
        for (String event : sim.getEvents()) {
            journal.addFirst("[" + sim.getStep() + "] " + event);
        }
        while (journal.size() > JOURNAL_SIZE) {
            journal.removeLast();
        }
        journalArea.setText(String.join("\n", journal));
        journalArea.setCaretPosition(0);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Ui.BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        header.add(label("Pokemon Simulation", 32, Ui.TEXT), BorderLayout.WEST);
        header.add(stepLabel, BorderLayout.EAST);
        return header;
    }

    private JComponent buildSide(Simulation sim) {
        JPanel teams = new JPanel(new GridLayout(2, 2, GAP, GAP));
        teams.setOpaque(false);
        for (Master master : sim.getMasters()) {
            teams.add(new TeamCard(sim, master));
        }

        JPanel top = new JPanel(new BorderLayout(0, GAP));
        top.setOpaque(false);
        teamsScroll.setViewportView(teams);
        teamsScroll.setBorder(null);
        teamsScroll.setOpaque(false);
        teamsScroll.getViewport().setOpaque(false);
        teamsScroll.getVerticalScrollBar().setUnitIncrement(TeamCard.ROW_HEIGHT);
        top.add(teamsScroll, BorderLayout.CENTER);
        top.add(section("Compteurs", stats), BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(journalArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 0));

        JPanel side = new JPanel(new BorderLayout(0, GAP));
        side.setBackground(Ui.BACKGROUND);
        side.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        side.setPreferredSize(new Dimension(SIDE_WIDTH, 0));
        side.add(top, BorderLayout.NORTH);
        side.add(section("Journal", scroll), BorderLayout.CENTER);
        return side;
    }

    private JComponent section(String title, JComponent content) {
        Border padding = BorderFactory.createEmptyBorder(6, 10, 8, 10);
        JPanel section = new JPanel(new BorderLayout(0, 4));
        section.setBackground(Ui.PANEL);
        section.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Ui.BORDER, 2), padding));
        section.add(label(title, 22, Ui.GOLD), BorderLayout.NORTH);
        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private static JLabel label(String text, float size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(Ui.font(size));
        label.setForeground(color);
        return label;
    }

    private static JTextArea textArea() {
        JTextArea area = new JTextArea();
        area.setFont(Ui.font(16));
        area.setForeground(Ui.TEXT);
        area.setBackground(Ui.PANEL);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
}
