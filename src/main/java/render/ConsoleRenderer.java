package render;

import environment.GameMap;
import environment.Occupant;
import master.Master;
import simulation.Simulation;

public class ConsoleRenderer implements Renderer {

    @Override
    public void render(Simulation sim) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Step ").append(sim.getStep()).append(" ===\n");
        sb.append(drawMap(sim.getMap()));
        sb.append(drawScores(sim));

        System.out.print(sb);
    }

    /** Occupant -> its symbol, SafeZone -> 'S', empty tile -> '.' */
    private String drawMap(GameMap map) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Occupant occupant = map.getTile(x, y).getOccupant();

                String symbol;
                if (occupant != null) {
                    symbol = occupant.getSymbol();
                } else if (map.getSafeZoneAt(x, y) != null) {
                    symbol = "S";
                } else {
                    symbol = ".";
                }
                sb.append(symbol).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private String drawScores(Simulation sim) {
        StringBuilder sb = new StringBuilder("Scores - ");
        for (Master m : sim.getMasters()) {
            sb.append(m.getSymbol()).append(": ").append(m.getScore()).append("  ");
        }
        return sb.append('\n').toString();
    }
}