package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.unit.Wraithling;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.List;
import java.util.Random;

public class HornOfTheForsakenCard extends Card {

    //maybe putting it to GameState
    // public void artifact3 (ActorRef out, GameState gameState) {
    //     int robustness = 3;
    //     Unit avatar = gameState.getPlayerUnit().get(0);
    //     final int currentHp = avatar.getHealth();
    //     for(int i=0;i>robustness;) {
    //         //if the avatar attack (should be in avatar unit?
    //         if(avatar.equals(gameState.getActiveUnit())) {
    //             List<Tile> emptyTile = checkEmptyTiles(out, gameState);
    //             if (emptyTile.size()>0) {
    //                 spawnWraithlings(out, gameState, emptyTile);
    //             }
    //         }
    //         if(avatar.getHealth()<currentHp) {
    //             avatar.setHealth(currentHp);
    //             i++;
    //         }
    //     }
    // }

    //summon a wraithling on a random tile if available
    public void spawnWraithling (ActorRef out, GameState gameState, List<Tile> emptyTiles) {
        if (emptyTiles.size() > 0) {
            Random r = new Random();
            int randomTile = r.nextInt(emptyTiles.size());
            Tile tile = emptyTiles.get(randomTile);
            BasicCommands.playEffectAnimation(out, "f1_wraithsummon", tile);
            Wraithling wraithling = (Wraithling)BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Wraithling.class);
            wraithling.setPositionByTile(emptyTiles.get(randomTile));
            BasicCommands.drawUnit(out, wraithling, emptyTiles.get(randomTile));
            gameState.getPlayerUnits().put(emptyTiles.get(randomTile), wraithling);
        }
    }

    public List<Tile> checkEmptyTiles (ActorRef out, GameState gameState) {
        List<Tile> emptyTiles = gameState.getAllTiles();
        for(Tile tile: emptyTiles) {
            if(tile.getUnit()!=null) {
                emptyTiles.remove(tile);
            }
        }
        return emptyTiles;
    }
}