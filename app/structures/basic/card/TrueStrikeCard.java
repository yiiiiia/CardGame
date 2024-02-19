package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

import java.util.Collection;

public class TrueStrikeCard extends Card {
    public void highlightTiles(ActorRef out, GameState gameState){
        //highlight the enemy units
        Collection<Unit> units=gameState.getUserPlayer().getAllUnits().values();
        for(Unit unit:units){
            int tilex = unit.getPosition().getTilex();
            int tiley = unit.getPosition().getTiley();
            Tile tile = gameState.getGameTiles()[tilex][tiley];
            BasicCommands.drawTile(out,tile,1);//mode=1 means highlight
        }
    }

    public boolean performSpell(ActorRef out, GameState gameState,Unit unit){
        Collection<Unit> units=gameState.getUserPlayer().getAllUnits().values();
        if(!units.contains(unit)){
            return false;
        }
        unit.setHealth(Math.max(unit.getHealth()-2,0));//health=0, this unit is dead, where do we write the logic?
        BasicCommands.playUnitAnimation(out,unit, UnitAnimationType.channel);
        return true;
    }
}
