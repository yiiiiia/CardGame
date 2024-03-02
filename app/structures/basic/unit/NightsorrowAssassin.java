package structures.basic.unit;

import java.util.List;

import commands.BasicCommands;
import controllers.GameScreenController;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;

public class NightsorrowAssassin extends Unit {

    public static final int initialHealth = 2;
    public static final int initialAttack = 4;

    protected int health;
    protected int attack;
    
    public NightsorrowAssassin() {
        super();
        health = 2;
        attack = 4;
    }

    public void performGambit(ActorRef out, GameState gameState, Unit unit) {
        // List<Unit> unitToDestroy = gameState.unitsWithinAttackRange(this);
        // for(Unit unit: unitToDestroy) {
        //     if(unit.getHealth()<unit.maxHealth) {
        //         Tile tile = gameState.getTileByPos(unit.getPosition().getTilex(), unit.getPosition().getTiley());
        //         BasicCommands.drawTile(out, tile, Tile.TILE_WHITE_MODE);
        //     }else {
        //         unitToDestroy.remove(unit);
        //     }
        // }

        // if(unitToDestroy.contains(unit)) {
        //     BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
        //     gameState.removeUnit(unit);
        //     BasicCommands.deleteUnit(out, unit);    
        // }
        BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
        gameState.removeUnit(unit);
        BasicCommands.deleteUnit(out, unit);    

    }

    public NightsorrowAssassin getInstance(String configpaths) {
        return (NightsorrowAssassin)BasicObjectBuilders.loadUnit(configpaths, 6, NightsorrowAssassin.class);
    }
}
