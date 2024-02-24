package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.unit.Wraithling;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.basic.UnitAnimationType;

import java.util.List;

public class DarkTerminusCard extends Card {
    
    public void highlightTiles(ActorRef out, GameState gamestate) {
        //highlight viable targets
        List<Unit> units = gamestate.getAiUnits();
        for(Unit enemyUnit: units) {
            int tilex = enemyUnit.getPosition().getTilex();
            int tiley = enemyUnit.getPosition().getTiley();
            Tile tile = gamestate.getTileByPos(tilex, tiley);
            BasicCommands.drawTile(out, tile, 1);
        }
    }

    public boolean performSpell(ActorRef out, GameState gameState, Unit unit) {
        //getter for aiUnits in gamestate may be needed
        List<Unit> units = gameState.getAiUnits();
        if(units.contains(unit)) {
            //remove unit from board
            //currently not in GameState
            Tile tile = gameState.getTileByPos(unit.getPosition().getTilex(), unit.getPosition().getTiley());
            BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death)
            gameState.removeUnit(unit);
            BasicCommands.deleteUnit(out, unit);
            //spawning a wraithling
            summonWraithling(out, gameState, tile);
            return true;
        }else {
            return false;
        }
    }

    public void summonWraithling (ActorRef out, GameState gameState, Tile tile) {
        if(tile.getUnit()==null) {
            BasicCommands.playEffectAnimation(out, "f1_wraithsummon", tile);
            Wraithling wraithling = (Wraithling)BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Wraithling.class);
            wraithling.setPositionByTile(tile);
            BasicCommands.drawUnit(out, wraithling, tile);
            gameState.getPlayerUnits().put(tile, wraithling);
        }
    }   
}
