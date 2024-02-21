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

public class WraithlingSwarmCard extends Card {

    private int emptyTileCount;
    //highlight the unoccupied tiles
    public List<Tile> highlightTiles(ActorRef out, GameState gameState) {
        List<Tile> emptyTiles = gameState.getAllTiles();
        
        for(Tile tile: emptyTiles) {
            if(tile.getUnit()!=null) {
            emptyTiles.remove(tile);
            }else {
                BasicCommands.drawTile(out,tile,1);
            }
        }
        return emptyTiles;
    }

    //to spawn the <= 3 wraithlings
    public void spawnWraithlings(ActorRef out, GameState gameState, JsonNode message, List<Tile> emptyTiles) {
        this.emptyTileCount = 3;
        for(int i = 0; i < emptyTileCount; i++) {
            this.highlightTiles(out, gameState);
            TileClicked tileClicked = new TileClicked();
            int tilex = message.get("tilex").asInt();
            int tiley = message.get("tiley").asInt();
            Tile tile = gameState.getTileByPos(tilex, tiley);
            tileClicked.handleNoUnitOnTile(out, gameState, tile);
            //if player clicks on one of the tiles in emptyTiles
            if(emptyTiles.contains(tile)) {
                Wraithling wraithling = new Wraithling();
                wraithling.setPositionByTile(tile);
                BasicCommands.drawUnit(out, wraithling, tile);
                gameState.getPlayerUnits().put(tile, wraithling);
                emptyTiles.remove(tile);
                //check what tile the player clicks
                //summon a wraithling on that tile if available
            }
        }
    }

}
