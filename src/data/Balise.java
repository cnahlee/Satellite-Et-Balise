package data;

import java.util.List;
import java.util.Objects;

import data.Deplacement.Deplacement;

/** Enum-based state machine; exploration is delegated to an interchangeable Strategy. */
public final class Balise {
    private final String id;
    private Position position;
    private EtatBalise etat = EtatBalise.COLLECTE;
    private Deplacement deplacement;
    private Position origineExploration;
    private long pasExploration;
    private final int dureeCollecte;
    private final int dureeTransmission;
    private final double vitesseRemontee;
    private int pasCollecte;
    private int pasTransmission;
    private Satellite satellite;

    public Balise(String id, Position position, Deplacement deplacement,
                  int dureeCollecte, int dureeTransmission, double vitesseRemontee) {
        if (id == null || id.isBlank() || dureeCollecte <= 0 || dureeTransmission <= 0
                || !Double.isFinite(vitesseRemontee) || vitesseRemontee <= 0) {
            throw new IllegalArgumentException("Parametres de la balise invalides");
        }
        this.id = id;
        this.position = Objects.requireNonNull(position, "position");
        this.deplacement = Objects.requireNonNull(deplacement, "deplacement");
        this.origineExploration = position;
        this.dureeCollecte = dureeCollecte;
        this.dureeTransmission = dureeTransmission;
        this.vitesseRemontee = vitesseRemontee;
    }

    public String getId() { return id; }
    public Position getPosition() { return position; }
    public EtatBalise getEtat() { return etat; }
    public Deplacement getDeplacement() { return deplacement; }
    public int getPasCollecte() { return pasCollecte; }
    public int getPasTransmission() { return pasTransmission; }
    /** Null outside TRANSMISSION. */
    public Satellite getSatellite() { return satellite; }

    /** Restart the strategy's local phase, without restarting the collection timer. */
    public void changerDeplacement(Deplacement nouveauDeplacement) {
        deplacement = Objects.requireNonNull(nouveauDeplacement, "deplacement");
        origineExploration = position;
        pasExploration = 0;
    }

    void avancer(Domaine domaine) {
        switch (etat) {
            case COLLECTE:
                Position suivante = deplacement.calculerPosition(origineExploration, pasExploration + 1, domaine);
                domaine.verifierPosition(suivante);
                position = suivante;
                pasExploration++;
                pasCollecte++;
                if (pasCollecte >= dureeCollecte) etat = EtatBalise.REMONTEE;
                break;
            case REMONTEE:
                // Ascent is independent from the chosen exploration movement.
                position = domaine.normaliser(position.getX(), position.getProfondeur() - vitesseRemontee);
                if (position.getProfondeur() == 0) etat = EtatBalise.ATTENTE;
                break;
            case ATTENTE:
                // Reservation is a separate phase, after every satellite has been released if needed.
                break;
            case TRANSMISSION:
                if (!satellite.estAPortee(position, domaine)) {
                    terminerConnexion();
                    etat = EtatBalise.ATTENTE;
                } else {
                    pasTransmission++;
                    if (pasTransmission >= dureeTransmission) {
                        terminerConnexion();
                        etat = EtatBalise.COLLECTE;
                        pasCollecte = 0;
                        pasExploration = 0;
                        origineExploration = position;
                    }
                }
                break;
        }
    }

    void chercherSatellite(List<Satellite> satellites, Domaine domaine) {
        if (etat != EtatBalise.ATTENTE) return;
        for (Satellite candidat : satellites) {
            if (candidat.estAPortee(position, domaine) && candidat.reserver(this)) {
                satellite = candidat;
                pasTransmission = 0;
                etat = EtatBalise.TRANSMISSION;
                return;
            }
        }
    }

    private void terminerConnexion() {
        satellite.liberer(this);
        satellite = null;
        pasTransmission = 0;
    }
}
