package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.SkyrockGolem;

public class SkyrockGolemCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        SkyrockGolem skyrockGolemUnit=new SkyrockGolem();
        skyrockGolemUnit.setPositionByTile(gameState.getGameTiles()[tilex][tiley]);
        BasicCommands.drawUnit(out, skyrockGolemUnit, gameState.getGameTiles()[tilex][tiley]);
        gameState.getAiPlayer().getAllUnits().put(gameState.getGameTiles()[tilex][tiley],skyrockGolemUnit);
    }
}
