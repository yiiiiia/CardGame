package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.unit.Wraithling;
import structures.basic.UnitAnimationType;

import java.util.List;

public class DarkTerminusCard extends Card {
    
    public void highlightTiles(ActorRef out, GameState gamestate) {
        //highlight viable targets
        //need to access unitId of avatar and remove it from the list
        List<Unit> units = gamestate.getAiUnits();
        for(Unit enemyUnit: units) {
            int tilex = enemyUnit.getPosition().getTilex();
            int tiley = enemyUnit.getPosition().getTiley();
            Tile tile = gamestate.getTileByPos(tilex, tiley);
            BasicCommands.drawTile(out, tile, 1);
        }
    }

    public boolean performSpell(ActorRef out, GameState gameState, JsonNode message, Unit unit) {
        //getter for aiUnits in gamestate may be needed
        List<Unit> units = gameState.getAiUnits();
        if(units.contains(unit)) {
            //remove unit from board
            //currently not in GameState
            gameState.removeUnit(unit);
            //spawning a wraithling
            summonWraithling(out, gameState, message);
            return true;
        }else {
            return false;
        }
    }

    public void summonWraithling (ActorRef out, GameState gameState, JsonNode message) {
        int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		Tile tile = gameState.getTileByPos(tilex, tiley);
        if(tile.getUnit()==null) {
            Wraithling wraithling = new Wraithling();
            //Wraithling wraithling = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Unit.class);
            wraithling.setPositionByTile(tile);
            BasicCommands.drawUnit(out, wraithling, tile);
            gameState.getPlayerUnits().put(tile, wraithling);
        }
    }   
}
