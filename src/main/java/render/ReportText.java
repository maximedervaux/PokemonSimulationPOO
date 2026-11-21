package render;

import interaction.Message;
import master.Master;
import pokemon.FirePokemon;
import pokemon.GhostPokemon;
import pokemon.Pokemon;
import pokemon.PoisonPokemon;
import pokemon.WaterPokemon;
import simulation.Simulation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Textes communs aux affichages console et graphique. */
final class ReportText {
    private ReportText() { }

    static String scores(Simulation sim) {
        return sim.getMasters().stream()
                .map(m -> m.getGroupName() + ": " + m.getScore())
                .collect(Collectors.joining("   "));
    }

    static String result(Simulation sim) {
        List<Master> winners = sim.getWinners();
        int score = winners.get(0).getScore();
        String names = winners.stream().map(Master::getGroupName).collect(Collectors.joining(", "));
        String label = winners.size() == 1 ? "Vainqueur" : "Égalité";
        return label + " : " + names + " avec " + score + " message(s)";
    }

    /** Valeurs des compteurs statiques d'instances. */
    static String instances() {
        return "Pokemon=" + Pokemon.getInstanceCount()
                + " (Fire=" + FirePokemon.getInstanceCount()
                + " Water=" + WaterPokemon.getInstanceCount()
                + " Ghost=" + GhostPokemon.getInstanceCount()
                + " Poison=" + PoisonPokemon.getInstanceCount()
                + ")  Master=" + Master.getInstanceCount()
                + "  Message=" + Message.getInstanceCount();
    }

    /** État de chaque agent actif du groupe, par exemple "F3 96EP 2msg". */
    static List<String> agents(Simulation sim, Master master) {
        return sim.getMobiles().stream()
                .filter(p -> p.getMaster() == master)
                .sorted(Comparator.comparingInt(Pokemon::getId))
                .map(p -> p.getLabel() + " " + p.getEp() + "EP " + p.getMessages().size() + "msg")
                .toList();
    }
}
