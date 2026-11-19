package data;

import java.util.Objects;

/** Periodic horizontal strip [0, largeur), with water depth in [0, profondeurMax]. */
public final class Domaine {
    private final double largeur;
    private final double profondeurMax;

    public Domaine(double largeur, double profondeurMax) {
        if (!Double.isFinite(largeur) || largeur <= 0
                || !Double.isFinite(profondeurMax) || profondeurMax <= 0) {
            throw new IllegalArgumentException("Les dimensions du domaine doivent etre finies et positives");
        }
        this.largeur = largeur;
        this.profondeurMax = profondeurMax;
    }

    public double getLargeur() { return largeur; }
    public double getProfondeurMax() { return profondeurMax; }

    public void verifierPosition(Position position) {
        Objects.requireNonNull(position, "position");
        if (position.getX() < 0 || position.getX() >= largeur
                || position.getProfondeur() > profondeurMax) {
            throw new IllegalArgumentException("Position hors du domaine : " + position);
        }
    }

    /** Wrap x at the seam and clamp depth at the surface/seabed. */
    public Position normaliser(double x, double profondeur) {
        if (!Double.isFinite(x) || !Double.isFinite(profondeur)) {
            throw new IllegalArgumentException("Coordonnees non finies");
        }
        double xNormalise = x % largeur;
        if (xNormalise < 0) xNormalise += largeur;
        // Rounding a tiny negative remainder can produce exactly largeur.
        if (xNormalise >= largeur) xNormalise = 0;
        return new Position(xNormalise, Math.max(0, Math.min(profondeurMax, profondeur)));
    }

    /** Shortest horizontal distance, including across the periodic seam. */
    public double distanceHorizontale(Position premiere, Position seconde) {
        verifierPosition(premiere);
        verifierPosition(seconde);
        double distance = Math.abs(premiere.getX() - seconde.getX());
        return Math.min(distance, largeur - distance);
    }
}
