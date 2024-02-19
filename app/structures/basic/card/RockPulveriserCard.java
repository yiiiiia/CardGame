package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.RockPulveriser;

public class RockPulveriserCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        RockPulveriser rockPulveriserUnit=new RockPulveriser();
        rockPulveriserUnit.setPositionByTile(gameState.getGameTiles()[tilex][tiley]);
        BasicCommands.drawUnit(out, rockPulveriserUnit, gameState.getGameTiles()[tilex][tiley]);
        gameState.getUserPlayer().getAllUnits().put(gameState.getGameTiles()[tilex][tiley],rockPulveriserUnit);
    }
}
