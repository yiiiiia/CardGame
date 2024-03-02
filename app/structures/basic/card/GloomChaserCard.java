package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.GloomChaser;

public class GloomChaserCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        GloomChaser gloomChaserUnit=new GloomChaser();
        gloomChaserUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, gloomChaserUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getUserPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),gloomChaserUnit);
    }
}
