package data.Deplacement;

import data.Position;

public class DeplacementHorizontal implements Deplacement {


    @Override
    public Position calculerPosition(Position position, long pas) {
        return new Position(
                //chnache axe des X par 1
                position.getX() + 1,
                position.getProfondeur()
        );
    }
}
