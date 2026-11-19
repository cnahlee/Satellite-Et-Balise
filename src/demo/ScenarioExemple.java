package demo;

import java.util.List;

import data.Balise;
import data.Domaine;
import data.Position;
import data.Satellite;
import data.Simulation;
import data.Deplacement.DeplacementHorizontal;
import data.Deplacement.DeplacementSinusoidal;
import data.Deplacement.DeplacementStationnaire;
import data.Deplacement.DeplacementVertical;

/** Sample initial values shared by the GUI and console. All behavior belongs to data. */
public final class ScenarioExemple {
    private ScenarioExemple() { }

    /** Fresh entities for each run: metres, m/s and one-second collection/transmission steps. */
    public static Simulation creerSimulation() {
        return new Simulation(new Domaine(1000, 100), List.of(
                new Balise("BAL-H", new Position(140, 28), new DeplacementHorizontal(6), 8, 4, 14),
                new Balise("BAL-V", new Position(380, 48), new DeplacementVertical(3), 6, 5, 18),
                new Balise("BAL-F", new Position(620, 72), new DeplacementStationnaire(), 4, 4, 24),
                new Balise("BAL-S", new Position(860, 55), new DeplacementSinusoidal(6, 12, 10), 3, 5, 18)),
                List.of(
                        new Satellite("SAT-LENT", new Position(100, 0), 12, 160),
                        new Satellite("SAT-RAPIDE", new Position(450, 0), 25, 140),
                        new Satellite("SAT-FIXE", new Position(850, 0), 0, 170)));
    }
}
