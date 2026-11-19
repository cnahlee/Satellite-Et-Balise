package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Deterministic single-threaded simulation; one call advances one simulated second. */
public final class Simulation {
    private final Domaine domaine;
    private final List<Balise> balises;
    private final List<Satellite> satellites;
    private long numeroPas;

    public Simulation(Domaine domaine, List<Balise> balises, List<Satellite> satellites) {
        this.domaine = Objects.requireNonNull(domaine, "domaine");
        this.balises = List.copyOf(balises);
        this.satellites = List.copyOf(satellites);
        Set<String> identifiants = new HashSet<>();
        for (Balise balise : this.balises) {
            verifierEntite(balise.getId(), balise.getPosition(), identifiants);
        }
        identifiants.clear();
        for (Satellite satellite : this.satellites) {
            verifierEntite(satellite.getId(), satellite.getPosition(), identifiants);
        }
    }

    private void verifierEntite(String id, Position position, Set<String> identifiants) {
        domaine.verifierPosition(position);
        if (!identifiants.add(id)) throw new IllegalArgumentException("Identifiant duplique : " + id);
    }

    public Domaine getDomaine() { return domaine; }
    public List<Balise> getBalises() { return balises; }
    public List<Satellite> getSatellites() { return satellites; }
    public long getNumeroPas() { return numeroPas; }

    public void avancer() {
        List<Balise> dejaEnAttente = new ArrayList<>();
        for (Balise balise : balises) {
            if (balise.getEtat() == EtatBalise.ATTENTE) dejaEnAttente.add(balise);
        }
        for (Satellite satellite : satellites) satellite.avancer(domaine);
        for (Balise balise : balises) balise.avancer(domaine);
        // All releases happen before assignments. List order breaks ties deterministically.
        // Newly surfaced/disconnected balises remain visibly ATTENTE until the next step.
        for (Balise balise : dejaEnAttente) balise.chercherSatellite(satellites, domaine);
        numeroPas++;
    }
}
