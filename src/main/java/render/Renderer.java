package render;

import simulation.Simulation;

public interface Renderer {
    /** Affiche l'état courant de la simulation. */
    void render(Simulation sim);

    /** Appelé une fois à la fin de la simulation. */
    default void renderFinal(Simulation sim) { }
}
