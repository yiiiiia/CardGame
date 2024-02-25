package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.YoungFlamewing;

public class YoungFlamewingCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        YoungFlamewing youngFlamewingUnit=new YoungFlamewing();
        youngFlamewingUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, youngFlamewingUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getAiPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),youngFlamewingUnit);
    }
}
