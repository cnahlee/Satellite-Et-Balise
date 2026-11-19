package data.Deplacement;

import data.Domaine;
import data.Position;

public final class DeplacementStationnaire implements Deplacement {
    @Override
    public Position calculerPosition(Position origine, long pas, Domaine domaine) {
        verifierArguments(origine, pas, domaine);
        return origine;
    }
}
