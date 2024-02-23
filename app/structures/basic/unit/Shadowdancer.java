package structures.basic.unit;

import structures.GameState;
import structures.basic.Unit;

public class Shadowdancer extends Unit {
    
    private int health;
    private int attack;

    public Shadowdancer() {
        super();
        health = 4;
        attack = 5;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        int numUnit = gameState.getAllUnits().size();
        int prevNumUnit = numUnit;
        if (prevNumUnit > gameState.getAllUnits().size()) {
            int playerHealth = gameState.getUserPlayer().getHealth();
            if (playerHealth<20) {
                gameState.getUserPlayer().setHealth(playerHealth + 1);
            }
            int aiHealth = gameState.getAiPlayer().getHealth();
            gameState.getAiPlayer().setHealth(aiHealth - 1);
        }
    }
}
