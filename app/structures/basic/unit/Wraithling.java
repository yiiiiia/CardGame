package structures.basic.unit;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.ImageCorrection;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationSet;
import structures.basic.UnitAnimationType;

public class Wraithling extends Unit {

    public Wraithling() {
        //assume the id for wraithling is 8
        super(8, UnitAnimationType.idle, ImageCorrection.getDefaultCorrection(), 1, 1);
    }

    public Wraithling(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
        super(id, animations, correction, currentTile, 1, 1);
    }

    public Wraithling(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
                      ImageCorrection correction) {
        super(id, animation, position, animations, correction, 1, 1);
    }

    public Wraithling(int id, UnitAnimationSet animations, ImageCorrection correction, int health, int attack) {
        super(id, animations, correction, health, attack);
    }
    
}
