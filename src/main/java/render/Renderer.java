package render;

import simulation.Simulation;

public interface Renderer {
    void render(Simulation sim);

    /** Appelé une fois à la fin de la simulation. */
    default void renderFinal(Simulation sim) { }
}
