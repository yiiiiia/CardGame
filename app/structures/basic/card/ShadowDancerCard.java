package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.ShadowDancer;

public class ShadowDancerCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        ShadowDancer shadowDancerUnit=new ShadowDancer();
        shadowDancerUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, shadowDancerUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getUserPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),shadowDancerUnit);
    }
}
