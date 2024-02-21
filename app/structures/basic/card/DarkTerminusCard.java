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

    public boolean performSpell(ActorRef out, GameState gamestate, Unit unit) {
        //getter for aiUnits in gamestate may be needed
        List<Unit> units = gamestate.getAiUnits();
        if(units.contains(unit)) {
            //remove unit from board
            //currently not in GameState
            gamestate.removeUnit(unit);
            //spawning a wraithling
            summonWraithling(out, gamestate, unit.getPosition().getTilex(), unit.getPosition().getTiley());
            return true;
        }else {
            return false;
        }
    }

    public void summonWraithling (ActorRef out, GameState gamestate, int tilex, int tiley) {
        Wraithling wraithling = new Wraithling();
        wraithling.setPositionByTile(gamestate.getTileByPos(tilex, tiley));
        BasicCommands.drawUnit(out, wraithling, gamestate.getTileByPos(tilex, tiley));
        gamestate.getPlayerUnits().put(gamestate.getTileByPos(tilex, tiley), wraithling);
    }   
}
