package structures.basic.card;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

import java.util.Collection;

public class SundropElixirCard extends Card {

    public void highlightTiles(ActorRef out, GameState gameState){
        //highlight the allied units
        Collection<Unit> units=gameState.getAiPlayer().getAllUnits().values();
        for(Unit unit:units){
            int tilex = unit.getPosition().getTilex();
            int tiley = unit.getPosition().getTiley();
            Tile tile = gameState.getGameTiles()[tilex][tiley];
            BasicCommands.drawTile(out,tile,1);//mode=1 means highlight
        }
    }

    public boolean performSpell(ActorRef out, GameState gameState,Unit unit){
        Collection<Unit> units=gameState.getAiPlayer().getAllUnits().values();
        if(!units.contains(unit)){
            return false;
        }
        unit.setHealth(Math.min(unit.getHealth()+4,unit.getMaximumHealth()));
        BasicCommands.playUnitAnimation(out,unit, UnitAnimationType.channel);
        return true;
    }
}
