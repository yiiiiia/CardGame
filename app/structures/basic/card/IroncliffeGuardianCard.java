package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unit.IroncliffeGuardian;

public class IroncliffeGuardianCard extends Card {
    public void summonUnit (ActorRef out, GameState gameState, int tilex, int tiley){
        IroncliffeGuardian ironcliffeGuardianUnit=new IroncliffeGuardian();
        ironcliffeGuardianUnit.setPositionByTile(gameState.getTileByPos(tilex,tiley));
        BasicCommands.drawUnit(out, ironcliffeGuardianUnit, gameState.getTileByPos(tilex,tiley));
        gameState.getAiPlayer().getAllUnits().put(gameState.getTileByPos(tilex,tiley),ironcliffeGuardianUnit);
    }
}
