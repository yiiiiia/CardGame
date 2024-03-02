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
import utils.BasicObjectBuilders;

public class Wraithling extends Unit {

    public static final int initialHealth = 1;
    public static final int initialAttack = 1;

    protected int health;
    protected int attack;

    public Wraithling() {
        super();
        health = 1;
        attack = 1;
    }

    public static Wraithling getInstance(String configpaths) {
        return (Wraithling)BasicObjectBuilders.loadUnit(configpaths, 9, Wraithling.class);
    }
}
