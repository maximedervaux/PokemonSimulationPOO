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
        sb.append("Scores - ").append(ReportText.scores(sim)).append('\n');
        sb.append(drawAgents(sim));
        sb.append("Agents épuisés : ").append(sim.getExhaustedCount()).append('\n');
        sb.append("Instances - ").append(ReportText.instances()).append('\n');
        sb.append(drawEvents(sim));

        System.out.print(sb);
    }

    @Override
    public void renderFinal(Simulation sim) {
        System.out.println("Résultat - " + ReportText.result(sim));
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

    private String drawAgents(Simulation sim) {
        StringBuilder sb = new StringBuilder();
        for (Master m : sim.getMasters()) {
            sb.append(String.format("%-6s : ", m.getGroupName()))
                    .append(String.join("  ", ReportText.agents(sim, m)))
                    .append('\n');
        }
        return sb.toString();
    }

    private String drawEvents(Simulation sim) {
        StringBuilder sb = new StringBuilder();
        for (String event : sim.getEvents()) {
            sb.append("  > ").append(event).append('\n');
        }
        return sb.toString();
    }
}