package data;

import java.util.Objects;

/** Satellite represented by its surface projection, not a physical altitude. */
public final class Satellite {
    private final String id;
    private Position position;
    private final double vitesse;
    private final double portee;
    private Balise baliseConnectee;

    public Satellite(String id, Position position, double vitesse, double portee) {
        if (id == null || id.isBlank() || !Double.isFinite(vitesse)
                || !Double.isFinite(portee) || portee < 0) {
            throw new IllegalArgumentException("Parametres du satellite invalides");
        }
        this.position = Objects.requireNonNull(position, "position");
        if (position.getProfondeur() != 0) {
            throw new IllegalArgumentException("La projection du satellite doit etre a profondeur zero");
        }
        this.id = id;
        this.vitesse = vitesse;
        this.portee = portee;
    }

    public String getId() { return id; }
    public Position getPosition() { return position; }
    public double getVitesse() { return vitesse; }
    public double getPortee() { return portee; }
    public boolean estDisponible() { return baliseConnectee == null; }
    /** Null when available. */
    public Balise getBaliseConnectee() { return baliseConnectee; }

    public boolean estAPortee(Position positionBalise, Domaine domaine) {
        return positionBalise.getProfondeur() == 0
                && domaine.distanceHorizontale(position, positionBalise) <= portee;
    }

    // Only the model's simulation loop mutates movement and reservations.
    void avancer(Domaine domaine) {
        position = domaine.normaliser(position.getX() + vitesse, 0);
    }

    boolean reserver(Balise balise) {
        Objects.requireNonNull(balise, "balise");
        if (!estDisponible()) return false;
        baliseConnectee = balise;
        return true;
    }

    void liberer(Balise balise) {
        if (baliseConnectee == balise) baliseConnectee = null;
    }
}
