package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.SwampEntangler;

public class SwampEntanglerCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        SwampEntangler swampEntanglerUnit=new SwampEntangler();
        swampEntanglerUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, swampEntanglerUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getAiPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),swampEntanglerUnit);
    }
}
