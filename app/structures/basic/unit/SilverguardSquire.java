package structures.basic.unit;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class SilverguardSquire extends Unit {
    
    public static final int initialHealth = 1;
    public static final int initialAttack = 1;

    protected int health;
    protected int attack;

    public SilverguardSquire() {
        super();
        health = 1;
        attack = 1;
    }

    public void performGambit (ActorRef out, GameState gameState) {
        Unit avatar = gameState.getPlayerAvatar();
        Tile frontTile = gameState.getTileByPos(avatar.getPosition().getTilex()-1, avatar.getPosition().getTilex());
        Tile backTile = gameState.getTileByPos(avatar.getPosition().getTilex()+1, avatar.getPosition().getTilex());
        if(frontTile.getUnit()!=null && gameState.getAiUnits().contains(frontTile.getUnit())) {
            frontTile.getUnit().setHealth(frontTile.getUnit().getHealth() + 1);
            frontTile.getUnit().setAttack(frontTile.getUnit().getAttack() + 1);
            BasicCommands.setUnitHealth(out, frontTile.getUnit(), frontTile.getUnit().getHealth() + 1);
            BasicCommands.setUnitAttack(out, frontTile.getUnit(), frontTile.getUnit().getAttack() + 1);

        }
        if(backTile.getUnit()!=null && gameState.getAiUnits().contains(backTile.getUnit())) {
            backTile.getUnit().setHealth(backTile.getUnit().getHealth() + 1);
            backTile.getUnit().setAttack(backTile.getUnit().getAttack() + 1);
            BasicCommands.setUnitHealth(out, backTile.getUnit(), backTile.getUnit().getHealth() + 1);
            BasicCommands.setUnitAttack(out, backTile.getUnit(), backTile.getUnit().getAttack() + 1);

        }
    }

    public SilverguardSquire getInstance(String configpaths) {
        return (SilverguardSquire)BasicObjectBuilders.loadUnit(configpaths, 11, SilverguardSquire.class);
    }
}
