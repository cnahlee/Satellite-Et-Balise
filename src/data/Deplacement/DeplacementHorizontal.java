package data.Deplacement;

import data.Position;
import data.Domaine;

/** Signed horizontal speed in metres per second (one second per step). */
public final class DeplacementHorizontal implements Deplacement {
    private final double vitesse;

    public DeplacementHorizontal() {
        this(1);
    }

    public DeplacementHorizontal(double vitesse) {
        if (!Double.isFinite(vitesse)) throw new IllegalArgumentException("Vitesse non finie");
        this.vitesse = vitesse;
    }

    @Override
    public Position calculerPosition(Position origine, long pas, Domaine domaine) {
        verifierArguments(origine, pas, domaine);
        return domaine.normaliser(origine.getX() + vitesse * pas, origine.getProfondeur());
    }
}
