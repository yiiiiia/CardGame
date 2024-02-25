package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.SilverguardKnight;

public class SilverguardKnightCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        SilverguardKnight silverguardKnightUnit=new SilverguardKnight();
        silverguardKnightUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, silverguardKnightUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getAiPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),silverguardKnightUnit);
    }
}
