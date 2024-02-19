package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.NightsorrowAssassin;

public class NightsorrowAssassinCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        NightsorrowAssassin nightsorrowAssassinUnit=new NightsorrowAssassin();
        nightsorrowAssassinUnit.setPositionByTile(gameState.getGameTiles()[tilex][tiley]);
        BasicCommands.drawUnit(out, nightsorrowAssassinUnit, gameState.getGameTiles()[tilex][tiley]);
        gameState.getUserPlayer().getAllUnits().put(gameState.getGameTiles()[tilex][tiley],nightsorrowAssassinUnit);
    }
}
