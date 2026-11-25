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
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Simulation {
    private final GameMap _map;
    private final RandomGenerator _random;
    private final int _numberOfPokemonPerType;
    private final int _maxSteps;
    private final List<Pokemon> _mobiles = new ArrayList<>();
    private final List<String> _events = new ArrayList<>();
    private List<Message> _universe;
    private int _step = 0;

    /** Initialise la carte, les équipes, les agents et leur univers de messages. */
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

    /** Crée et place les quatre Masters dans leurs SafeZones respectives. */
    private void generateMasters() {
        assign(Corner.TOP_LEFT,     MasterFire.getInstance());
        assign(Corner.TOP_RIGHT,    MasterWater.getInstance());
        assign(Corner.BOTTOM_LEFT,  MasterGhost.getInstance());
        assign(Corner.BOTTOM_RIGHT, MasterPoison.getInstance());
    }

    /** Réinitialise un Master et l'installe dans le coin indiqué. */
    private void assign(Corner corner, Master master) {
        SafeZone zone = _map.getSafeZone(corner);
        master.reset();
        zone.setMaster(master);
        master.init(zone.getOuterCornerTile(_map, corner), _map);
    }

    /** Crée les populations mobiles dans les SafeZones correspondantes. */
    private void generatePokemon() {
        spawn(Corner.TOP_LEFT,     FirePokemon::new);
        spawn(Corner.TOP_RIGHT,    WaterPokemon::new);
        spawn(Corner.BOTTOM_LEFT,  GhostPokemon::new);
        spawn(Corner.BOTTOM_RIGHT, PoisonPokemon::new);
    }

    /** Place le nombre configuré de Pokémon sur des cases libres de leur zone. */
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

    /** Charge l'univers de messages puis le répartit entre les Pokémon. */
    private void generateMessages(String resource, int count) throws IOException {
        this._universe = MessageFactory.load(resource, count, _random);
        this.distributeMessages(_universe);
    }

    /** Distribue équitablement les messages après en avoir mélangé l'ordre. */
    private void distributeMessages(List<Message> universe) {
        List<Message> shuffled = new ArrayList<>(universe);
        _random.shuffle(shuffled);

        for (int i = 0; i < shuffled.size(); i++) {
            Pokemon poke = _mobiles.get(i % _mobiles.size());
            poke.learn(shuffled.get(i));
        }
    }

    /** Fait jouer une fois chaque agent actif dans un ordre aléatoire. */
    public void step() {
        _events.clear();
        _random.shuffle(_mobiles);
        for (Pokemon poke : _mobiles) {
            if (!poke.isExhausted()) {
                poke.move(_map, _random, _events);
            }
        }
        _mobiles.removeIf(Pokemon::isExhausted);
        _step++;
    }

    /** Retourne la carte de cette simulation. */
    public GameMap getMap() {
        return _map;
    }

    /** Retourne le nombre d'étapes déjà jouées. */
    public int getStep() {
        return _step;
    }

    /** Retourne les quatre Masters dans l'ordre des équipes. */
    public List<Master> getMasters() {
        return List.of(MasterFire.getInstance(), MasterWater.getInstance(),
                MasterGhost.getInstance(), MasterPoison.getInstance());
    }

    /** Agents encore actifs. */
    public List<Pokemon> getMobiles() {
        return Collections.unmodifiableList(_mobiles);
    }

    /** Ce qui s'est passé pendant la dernière étape. */
    public List<String> getEvents() {
        return Collections.unmodifiableList(_events);
    }

    /** Nombre de messages à collecter. */
    public int getMessageCount() {
        return _universe.size();
    }

    /** Retourne le nombre de Pokémon retirés après épuisement. */
    public int getExhaustedCount() {
        return getMasters().size() * _numberOfPokemonPerType - _mobiles.size();
    }

    /** Masters ayant le meilleur score : un seul en cas de victoire, plusieurs en cas d'égalité. */
    public List<Master> getWinners() {
        int best = getMasters().stream().mapToInt(Master::getScore).max().orElse(0);
        return getMasters().stream().filter(m -> m.getScore() == best).toList();
    }

    /** Indique si une condition de fin de partie est atteinte. */
    public boolean isFinished() {
        return _step >= _maxSteps || _mobiles.isEmpty() || this.anyMasterHasAllMessages();
    }

    /** Vérifie si un Master possède tous les messages de l'univers. */
    private boolean anyMasterHasAllMessages() {
        return getMasters().stream().anyMatch(m -> m.getScore() == _universe.size());
    }
}
