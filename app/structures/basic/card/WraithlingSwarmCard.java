package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.TileClicked;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.unit.Wraithling;

import java.util.List;

//call processEvent in tileClicked

public class WraithlingSwarmCard extends Card {


    public void performSpell(ActorRef out, GameState gameState, JsonNode message) {
        //to spawn the <= 3 wraithlings
        for (int i = 0; i < 3; i++) {
            List<Tile> emptyTiles = highlightTiles(out, gameState);
            if(!emptyTiles.isEmpty()) {
                TileClicked tileClicked = new TileClicked();
                tileClicked.processEvent(out, gameState, message);
            }
        }
    }

    public void summonUnit(ActorRef out, GameState gameState, JsonNode message) {
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

    public List<Tile> highlightTiles(ActorRef out, GameState gameState) {
        List<Tile> emptyTiles = gameState.getAllTiles();
        for (Tile tile: emptyTiles) {
            if(tile.getUnit()!=null) {
                emptyTiles.remove(tile);
            }else {
                BasicCommands.drawTile(out, tile, 1);
            }
        }
        return emptyTiles;
    }
}