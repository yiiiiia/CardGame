package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.SilverguardSquire;
import structures.basic.unit.SwampEntangler;

public class SilverguardSquireCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        SilverguardSquire silverguardSquireUnit=new SilverguardSquire();
        silverguardSquireUnit.setPositionByTile(gameState.getGameTiles()[tilex][tiley]);
        BasicCommands.drawUnit(out, silverguardSquireUnit, gameState.getGameTiles()[tilex][tiley]);
        gameState.getAiPlayer().getAllUnits().put(gameState.getGameTiles()[tilex][tiley],silverguardSquireUnit);
    }
}
