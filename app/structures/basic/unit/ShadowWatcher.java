package structures.basic.unit;

import structures.GameState;
import structures.basic.Unit;

public class ShadowWatcher extends Unit {
    
    private int health;
    private int attack;

    public ShadowWatcher() {
        super();
        health = 2;
        attack = 3;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        int numUnit = gameState.getAllUnits().size();
        int prevNumUnit = numUnit;
        if (prevNumUnit > gameState.getAllUnits().size()) {
            this.setHealth(health + 1);
            this.setAttack(attack + 1);
        }
    }
}
