package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.SilverguardKnight;

public class SilverguardKnightCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        SilverguardKnight silverguardKnightUnit=new SilverguardKnight();
        silverguardKnightUnit.setPositionByTile(gameState.getGameTiles()[tilex][tiley]);
        BasicCommands.drawUnit(out, silverguardKnightUnit, gameState.getGameTiles()[tilex][tiley]);
        gameState.getAiPlayer().getAllUnits().put(gameState.getGameTiles()[tilex][tiley],silverguardKnightUnit);
    }
}
