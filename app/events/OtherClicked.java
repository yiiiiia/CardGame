package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{
	public  void PlaceableArea(ActorRef out,GameState gameState,Tile tile,java.util.Map<Tile, Unit> map,int type)
	{
		int x=tile.getTilex()-1;
		int y=tile.getTiley()-1;
		for(int i=0;i<3;i++)
	{for(int j=0;j<3;j++)
		{if(isPlaceBle(x+i,y+j,gameState))
		{
			BasicCommands.drawTile(out, gameState.boardTile[x+i][y+j], type);
		}
		
		}}
	}
	
	public boolean isPlaceBle(int x,int y,GameState gameState)
	{if(x<0||x>8||y<0||y>4||gameState.players[0].getUnit().containsKey(gameState.boardTile[x][y]))//这里得再加AI类的unit判断
	{return false;}
	return true;
	}
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		
		
		for(int i=0;i<gameState.players[0].getNumCard();i++)//后续在gamestate里面加属性精准定位。
		{
			BasicCommands.drawCard(out, gameState.players[0].getCard()[i], i+1, 0);
		}
		
		for(Tile cur:gameState.players[0].getUnit().keySet())
		{
			PlaceableArea(out,gameState,cur,gameState.players[0].getUnit(),0);
			
		}
		for(Tile cur:gameState.players[1].getUnit().keySet())
		{
			BasicCommands.drawTile(out, cur, 0);
		}
		
	}

}


