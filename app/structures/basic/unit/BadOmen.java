package structures.basic.unit;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class BadOmen extends Unit {

    protected int health;
    protected int attack;

    public BadOmen() {
        super();
        health = 1;
        attack = 0;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        this.setAttack(attack + 1);
        BasicCommands.setUnitAttack(out, this, attack + 1);
    }

    public static BadOmen getInstance(String configpaths) {
        return (BadOmen)BasicObjectBuilders.loadUnit(configpaths, 0, BadOmen.class);
    }
}
