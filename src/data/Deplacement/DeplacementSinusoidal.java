package data.Deplacement;

import data.Domaine;
import data.Position;

/** Horizontal drift plus a depth oscillation around the exploration origin. */
public final class DeplacementSinusoidal implements Deplacement {
    private final double vitesseHorizontale;
    private final double amplitude;
    private final long periode;

    /** Speed in m/s, amplitude in metres, period in one-second steps. */
    public DeplacementSinusoidal(double vitesseHorizontale, double amplitude, long periode) {
        if (!Double.isFinite(vitesseHorizontale) || !Double.isFinite(amplitude)
                || amplitude < 0 || periode <= 0) {
            throw new IllegalArgumentException("Parametres sinusoidaux invalides");
        }
        this.vitesseHorizontale = vitesseHorizontale;
        this.amplitude = amplitude;
        this.periode = periode;
    }

    @Override
    public Position calculerPosition(Position origine, long pas, Domaine domaine) {
        verifierArguments(origine, pas, domaine);
        double phase = 2 * Math.PI * (pas % periode) / periode;
        return domaine.normaliser(origine.getX() + vitesseHorizontale * pas,
                origine.getProfondeur() + amplitude * Math.sin(phase));
    }
}
