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

    private int health;
    private int attack;

    public Wraithling() {
        super();
        health = 1;
        attack = 1;
    }

}
