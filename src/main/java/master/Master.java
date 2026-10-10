package master;

import environment.SafeZone;
import pokemon.Pokemon;

public abstract class Master extends Pokemon {
    private static int instanceCount = 0;
    protected SafeZone safeZone;

    protected Master(){
        super();
        instanceCount++;
    }
}