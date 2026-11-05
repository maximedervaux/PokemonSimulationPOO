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
import utils.Config;
import utils.RandomGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Simulation {
    private final GameMap _map;
    private final RandomGenerator _random;
    private final int _numberOfPokemonPerType;
    private final int _maxSteps;
    private final List<Pokemon> _mobiles = new ArrayList<>();
    private List<Message> _universe;
    private int _step = 0;

    public Simulation(Config config) throws IOException {
        _random = new RandomGenerator(config.getLong("simulation.seed"));
        _map = new GameMap(config.getInt("map.width"), config.getInt("map.height"), _random,
                config.getDouble("map.obstacle.density"));
        this._numberOfPokemonPerType = config.getInt("pokemon.per.type");
        this._maxSteps = config.getInt("simulation.max.steps");

        this.generateMasters();
        this.generatePokemon();
        this.generateMessages(config.getString("messages.resource"), config.getInt("messages.count"));
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
        master.init(zone.getOuterCornerTile(_map, corner), _map);
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
            _mobiles.add(pokemon);
        }
    }

    private void generateMessages(String resource, int count) throws IOException {
        this._universe = MessageFactory.load(resource, count, _random);
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

    public void step() {
        _random.shuffle(_mobiles);
        for (Pokemon poke : _mobiles) {
            if (!poke.isExhausted()) {
                poke.move(_map, _random);
            }
        }
        _mobiles.removeIf(Pokemon::isExhausted);
        _step++;
    }

    public GameMap getMap() {
        return _map;
    }

    public int getStep() {
        return _step;
    }

    public List<Master> getMasters() {
        return List.of(MasterFire.getInstance(), MasterWater.getInstance(),
                MasterGhost.getInstance(), MasterPoison.getInstance());
    }

    public boolean isFinished() {
        return _step >= _maxSteps || _mobiles.isEmpty() || this.anyMasterHasAllMessages();
    }

    private boolean anyMasterHasAllMessages() {
        return getMasters().stream().anyMatch(m -> m.getScore() == _universe.size());
    }
}
