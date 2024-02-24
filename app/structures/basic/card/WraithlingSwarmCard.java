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
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.List;

public class WraithlingSwarmCard extends Card {


    public void summonUnit (ActorRef out, GameState gameState, Tile tile) {
        //to spawn the <= 3 wraithlings
        for (int i = 0; i < 3;) {
            List<Tile> emptyTiles = gameState.getAllTiles();
            for (Tile emptyTile: emptyTiles) {
                if(emptyTile.getUnit()!=null) {
                    emptyTiles.remove(emptyTile);
                }else {
                    BasicCommands.drawTile(out, emptyTile, 1);
                }
            }
            if(!emptyTiles.isEmpty()) {
                if(tile.getUnit()==null) {
                    BasicCommands.playEffectAnimation(out, "f1_wraithsummon", tile);
                    Wraithling wraithling = (Wraithling)BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Wraithling.class);
                    wraithling.setPositionByTile(tile);
                    BasicCommands.drawUnit(out, wraithling, tile);
                    gameState.getPlayerUnits().put(tile, wraithling);
                    i++;
                }
            }else {
                break;
            }
        }
    }
}
