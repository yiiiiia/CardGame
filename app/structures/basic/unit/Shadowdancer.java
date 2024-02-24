package structures.basic.unit;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;

public class Shadowdancer extends Unit {
    
    protected int health;
    protected int attack;

    public Shadowdancer() {
        super();
        health = 4;
        attack = 5;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        int playerHealth = gameState.getUserPlayer().getHealth();
        if (playerHealth<20) {
            gameState.getUserPlayer().setHealth(playerHealth + 1);
            BasicCommands.setPlayer1Health(out, gameState.getUserPlayer());
        }
        int aiHealth = gameState.getAiPlayer().getHealth();
        gameState.getAiPlayer().setHealth(aiHealth - 1);
        BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
        if (aiHealth <= 0) {
            BasicCommands.playUnitAnimation(out, gameState.getAIAvatar(), UnitAnimationType.death);
            BasicCommands.deleteUnit(out, gameState.getAIAvatar());
        }
    }

    public static Shadowdancer getInstance(String configpaths) {
        return (Shadowdancer)BasicObjectBuilders.loadUnit(configpaths, 0, Shadowdancer.class);
    }
}
