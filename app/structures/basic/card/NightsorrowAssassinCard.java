package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.NightsorrowAssassin;

public class NightsorrowAssassinCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        NightsorrowAssassin nightsorrowAssassinUnit=new NightsorrowAssassin();
        nightsorrowAssassinUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, nightsorrowAssassinUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getUserPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),nightsorrowAssassinUnit);
    }
}
