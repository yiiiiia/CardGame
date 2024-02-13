package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.stream.impl.fusing.Map;
import commands.BasicCommands;
import structures.GameState;
import structures.GameState.Status;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{
	public  void PlaceableArea(ActorRef out,GameState gameState,Tile tile,java.util.Map<Tile, Unit> map,int is)//参数需要调整
	{int x=tile.getTilex()-1;
		int y=tile.getTiley()-1;
		for(int i=0;i<3;i++)
	{for(int j=0;j<3;j++)
		{if(isPlaceBle(x+i,y+j,gameState))
		{
			BasicCommands.drawTile(out, gameState.boardTile[x+i][y+j], is);
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
		if(gameState.game_status.equals(Status.AI))
		{
			
		}
		else
		{
			int handPosition = message.get("position").asInt();
			int pos=handPosition-1;
			BasicCommands.drawCard(out, gameState.players[0].getCard()[pos], handPosition, 1);
		if(gameState.players[0].getCard()[pos].isCreature())
			{for(Tile cur:gameState.players[0].getUnit().keySet())
		{
			PlaceableArea(out,gameState,cur,gameState.players[0].getUnit(),1);
			
		}
			}
		
		else
		{for(Tile cur:gameState.players[1].getUnit().keySet())
		{
			BasicCommands.drawTile(out, cur, 2);
		}
			
		}
		}
		
		
		
		
	}

}
