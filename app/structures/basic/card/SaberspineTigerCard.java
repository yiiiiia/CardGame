package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.SaberspineTiger;
import structures.basic.unit.SwampEntangler;

public class SaberspineTigerCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        SaberspineTiger saberspineTigerUnit=new SaberspineTiger();
        saberspineTigerUnit.setPositionByTile(gameState.getGameTiles()[tilex][tiley]);
        BasicCommands.drawUnit(out, saberspineTigerUnit, gameState.getGameTiles()[tilex][tiley]);
        gameState.getAiPlayer().getAllUnits().put(gameState.getGameTiles()[tilex][tiley],saberspineTigerUnit);
    }
}
