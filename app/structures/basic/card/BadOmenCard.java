package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.BadOmen;

public class BadOmenCard extends Card {

    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        BadOmen badOmenUnit=new BadOmen();
        badOmenUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, badOmenUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getUserPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),badOmenUnit);
    }
}
