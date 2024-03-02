package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.ShadowWatcher;

public class ShadowWatcherCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        ShadowWatcher shadowWatcherUnit=new ShadowWatcher();
        shadowWatcherUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, shadowWatcherUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getUserPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),shadowWatcherUnit);
    }
}
