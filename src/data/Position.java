package data;

public class Position {

    private final double x;
    private final double profondeur;

    public Position(double x, double profondeur) {
        if (!Double.isFinite(x)
                || !Double.isFinite(profondeur)
                || profondeur < 0) {
            throw new IllegalArgumentException("Position invalide");
        }

        this.x = x;
        this.profondeur = profondeur;
    }

    public double getX() {
        return x;
    }

    public double getProfondeur() {
        return profondeur;
    }

    @Override
    public String toString() {
        return "Position{x=" + x + ", profondeur=" + profondeur + "}";
    }
}