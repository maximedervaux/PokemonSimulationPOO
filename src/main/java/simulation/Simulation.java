package simulation;

import enums.Corner;
import environment.GameMap;
import environment.SafeZone;
import environment.Tile;
import master.MasterFire;
import master.MasterGhost;
import master.MasterPoison;
import master.MasterWater;
import master.Master;
import pokemon.*;
import utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Simulation {
    private final GameMap _map;
    private RandomGenerator _random;
    private int _numberOfPokemonPerType;
    private final List<Pokemon> _mobiles = new ArrayList<>();

    public Simulation(int numberOfPokemonPerType){
        _random = new RandomGenerator(1111111);
        _map = new GameMap(25, 25, _random, 0.05);
        // TODO: throw si trop grand par rapport à la map
        this._numberOfPokemonPerType = numberOfPokemonPerType;

        this.generateMasters();
        this.generatePokemons();
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

    private void generatePokemons() {
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

    public void run(){
        _map.printDebug();
    }
}