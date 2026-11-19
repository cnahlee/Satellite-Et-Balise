package data.Deplacement;

import data.Domaine;
import data.Position;

/** Positive speed descends; negative speed rises. Depth stops at either bound. */
public final class DeplacementVertical implements Deplacement {
    private final double vitesse;

    public DeplacementVertical() {
        this(1);
    }

    public DeplacementVertical(double vitesse) {
        if (!Double.isFinite(vitesse)) throw new IllegalArgumentException("Vitesse non finie");
        this.vitesse = vitesse;
    }

    @Override
    public Position calculerPosition(Position origine, long pas, Domaine domaine) {
        verifierArguments(origine, pas, domaine);
        return domaine.normaliser(origine.getX(), origine.getProfondeur() + vitesse * pas);
    }
}
