package structures.basic.unit;

import structures.GameState;
import structures.basic.Unit;

public class BadOmen extends Unit {

    private int health;
    private int attack;

    public BadOmen() {
        super();
        health = 1;
        attack = 0;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        int numUnit = gameState.getAllUnits().size();
        int prevNumUnit = numUnit;
        if (prevNumUnit > gameState.getAllUnits().size()) {
            this.setAttack(attack + 1);
        }
    }

}

