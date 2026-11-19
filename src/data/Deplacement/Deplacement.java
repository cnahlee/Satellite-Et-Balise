package data.Deplacement;

import data.Position;
import data.Domaine;

/**
 * Exploration Strategy. origine is fixed at the start of collection or a strategy
 * change; pas is the number of one-second exploration steps since that origin.
 * Implementations are stateless and must return a position inside domaine.
 */
public interface Deplacement {
    Position calculerPosition(Position origine, long pas, Domaine domaine);

    default void verifierArguments(Position origine, long pas, Domaine domaine) {
        if (pas < 0) throw new IllegalArgumentException("Nombre de pas negatif");
        domaine.verifierPosition(origine);
    }
}
