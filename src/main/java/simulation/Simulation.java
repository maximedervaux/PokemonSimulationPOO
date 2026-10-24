package simulation;

import enums.Corner;
import environment.GameMap;
import environment.SafeZone;
import environment.Tile;
import interaction.Message;
import interaction.MessageFactory;
import master.MasterFire;
import master.MasterGhost;
import master.MasterPoison;
import master.MasterWater;
import master.Master;
import pokemon.*;
import utils.RandomGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Simulation {
    private final GameMap _map;
    private final RandomGenerator _random;
    private final int _numberOfPokemonPerType;
    private final List<Pokemon> _mobiles = new ArrayList<>();
    private List<Message> _universe;

    public Simulation(int numberOfPokemonPerType, int numberOfMessages) throws IOException {
        _random = new RandomGenerator(1111111);
        _map = new GameMap(25, 25, _random, 0.05);
        // TODO: throw si trop grand par rapport à la map
        this._numberOfPokemonPerType = numberOfPokemonPerType;

        this.generateMasters();
        this.generatePokemon();
        this.generateMessages(numberOfMessages);
    }

    private void generateMasters() {
        assign(Corner.TOP_LEFT,     MasterFire.getInstance());
        assign(Corner.TOP_RIGHT,    MasterWater.getInstance());
        assign(Corner.BOTTOM_LEFT,  MasterGhost.getInstance());
        assign(Corner.BOTTOM_RIGHT, MasterPoison.getInstance());
    }

    private void assign(Corner corner, Master master) {
        SafeZone zone = _map.getSafeZone(corner);
        master.reset();
        zone.setMaster(master);
        master.init(zone, zone.getOuterCornerTile(_map, corner), _map);
    }

    private void generatePokemon() {
        spawn(Corner.TOP_LEFT,     FirePokemon::new);
        spawn(Corner.TOP_RIGHT,    WaterPokemon::new);
        spawn(Corner.BOTTOM_LEFT,  GhostPokemon::new);
        spawn(Corner.BOTTOM_RIGHT, PoisonPokemon::new);
    }

    private void spawn(Corner corner, Supplier<Pokemon> factory) {
        SafeZone zone = _map.getSafeZone(corner);
        List<Tile> free = _map.getFreeTilesIn(zone);

        if (_numberOfPokemonPerType > free.size()) {
            throw new IllegalArgumentException("SafeZone " + corner + " too small for "
                    + _numberOfPokemonPerType + " pokemons (" + free.size() + " free tiles)");
        }

        List<Tile> chosen = _random.pickSubset(free, _numberOfPokemonPerType);
        for (Tile tile : chosen) {
            Pokemon pokemon = factory.get();
            _map.place(pokemon, tile);
            System.out.println(pokemon.getSymbol() + " -> " + pokemon.getTile());
            _mobiles.add(pokemon);
        }
    }

    private void generateMessages(int count) throws IOException {
        this._universe = MessageFactory.load(Path.of("./src/main/data/messages.txt"), count, _random);
        this.distributeMessages(_universe);
    }

    private void distributeMessages(List<Message> universe) {
        List<Message> shuffled = new ArrayList<>(universe);
        _random.shuffle(shuffled);

        for (int i = 0; i < shuffled.size(); i++) {
            Pokemon poke = _mobiles.get(i % _mobiles.size());
            poke.learn(shuffled.get(i));
        }
    }

    public void run(){
        // TODO: Export step in a conf file
        _map.printDebug();
        for (int step = 0; step < 50; step++) {
            _random.shuffle(_mobiles);
            for (Pokemon poke : _mobiles) {
                poke.move(_map, _random);
            }
            System.out.println("Step " + step);
            // Tempo to see what is going on
            try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        System.out.println("Scores - F:" + MasterFire.getInstance().getScore()
                + " W:" + MasterWater.getInstance().getScore()
                + " G:" + MasterGhost.getInstance().getScore()
                + " P:" + MasterPoison.getInstance().getScore());
        _map.printDebug();
    }
}