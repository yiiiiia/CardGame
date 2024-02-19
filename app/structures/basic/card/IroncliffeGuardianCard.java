package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.IroncliffeGuardian;
import structures.basic.unit.SwampEntangler;

public class IroncliffeGuardianCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        IroncliffeGuardian ironcliffeGuardianUnit=new IroncliffeGuardian();
        ironcliffeGuardianUnit.setPositionByTile(gameState.getGameTiles()[tilex][tiley]);
        BasicCommands.drawUnit(out, ironcliffeGuardianUnit, gameState.getGameTiles()[tilex][tiley]);
        gameState.getAiPlayer().getAllUnits().put(gameState.getGameTiles()[tilex][tiley],ironcliffeGuardianUnit);
    }
}
