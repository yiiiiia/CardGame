package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.SilverguardSquire;

public class SilverguardSquireCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        SilverguardSquire silverguardSquireUnit=new SilverguardSquire();
        silverguardSquireUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, silverguardSquireUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getAiPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),silverguardSquireUnit);
    }
}
