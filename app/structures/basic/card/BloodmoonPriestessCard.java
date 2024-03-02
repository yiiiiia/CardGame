package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.BloodmoonPriestess;

public class BloodmoonPriestessCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        BloodmoonPriestess bloodmoonPriestessUnit=new BloodmoonPriestess();
        bloodmoonPriestessUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, bloodmoonPriestessUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getUserPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),bloodmoonPriestessUnit);
    }
}