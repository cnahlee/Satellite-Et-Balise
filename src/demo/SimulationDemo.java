package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import data.Balise;
import data.Domaine;
import data.EtatBalise;
import data.Position;
import data.Satellite;
import data.Simulation;
import data.Deplacement.DeplacementSinusoidal;
import data.Deplacement.DeplacementStationnaire;

/** Console demonstration, independent from the GUI entry point Main. */
public final class SimulationDemo {
    private SimulationDemo() { }

    public static void main(String[] args) {
        executer("Exploration et cycles complets (1 pas = 1 seconde)",
                ScenarioExemple.creerSimulation(), 30, true);

        Domaine petitDomaine = new Domaine(200, 50);
        executer("Competition : deux balises, un satellite", new Simulation(petitDomaine,
                List.of(baliseSurface("BAL-A", 2), baliseSurface("BAL-B", 2)),
                List.of(new Satellite("SAT-PARTAGE", new Position(50, 0), 0, 5))), 9, false);

        executer("Perte de communication : le satellite quitte la portee", new Simulation(petitDomaine,
                List.of(baliseSurface("BAL-PERTE", 3)),
                List.of(new Satellite("SAT-PASSAGE", new Position(20, 0), 10, 5))), 7, false);
    }

    private static Balise baliseSurface(String id, int dureeTransmission) {
        return new Balise(id, new Position(50, 0), new DeplacementStationnaire(), 1, dureeTransmission, 5);
    }

    private static void executer(String titre, Simulation simulation, int nombrePas, boolean changerStrategie) {
        System.out.println("\n=== " + titre + " ===");
        afficher(simulation);
        for (int i = 0; i < nombrePas; i++) {
            if (changerStrategie && simulation.getNumeroPas() == 4) {
                simulation.getBalises().get(0).changerDeplacement(new DeplacementSinusoidal(2, 5, 8));
                System.out.println("  STRATEGIE : BAL-H passe de horizontal a sinusoidal, sans remettre la collecte a zero.");
            }
            List<EtatBalise> avant = new ArrayList<>();
            for (Balise balise : simulation.getBalises()) avant.add(balise.getEtat());
            simulation.avancer();
            afficher(simulation);
            for (int index = 0; index < avant.size(); index++) {
                Balise balise = simulation.getBalises().get(index);
                if (avant.get(index) == EtatBalise.TRANSMISSION) {
                    if (balise.getEtat() == EtatBalise.COLLECTE) {
                        System.out.println("  TERMINEE : " + balise.getId() + " libere son satellite et reprend la collecte.");
                    } else if (balise.getEtat() == EtatBalise.ATTENTE) {
                        System.out.println("  PERTE : " + balise.getId() + " libere son satellite et attend une nouvelle connexion.");
                    }
                }
            }
        }
    }

    private static void afficher(Simulation simulation) {
        System.out.println("Pas " + simulation.getNumeroPas() + " (t = " + simulation.getNumeroPas() + " s)");
        for (Satellite satellite : simulation.getSatellites()) {
            System.out.printf(Locale.ROOT, "  %s x=%.1f m, projection=%.1f m, v=%.1f m/s : %s%s%n",
                    satellite.getId(), satellite.getPosition().getX(), satellite.getPosition().getProfondeur(),
                    satellite.getVitesse(), satellite.estDisponible() ? "DISPONIBLE" : "OCCUPE",
                    satellite.estDisponible() ? "" : " par " + satellite.getBaliseConnectee().getId());
        }
        for (Balise balise : simulation.getBalises()) {
            System.out.printf(Locale.ROOT, "  %s x=%.1f m, profondeur=%.1f m : %s, %s%s%n",
                    balise.getId(), balise.getPosition().getX(), balise.getPosition().getProfondeur(),
                    balise.getEtat(), balise.getDeplacement().getClass().getSimpleName(),
                    balise.getSatellite() == null ? "" : " -> " + balise.getSatellite().getId()
                            + " (pas transmis=" + balise.getPasTransmission() + ")");
        }
    }
}
