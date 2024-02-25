package draft;







import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.stream.impl.fusing.Map;
import commands.BasicCommands;
import events.EventProcessor;
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
public class CardcCicked implements EventProcessor{
	public  void PlaceableArea(ActorRef out,GameState gameState,Tile tile,int is)//参数需要调整
	{int x=tile.getTilex()-1;
		int y=tile.getTiley()-1;
		for(int i=0;i<3;i++)
	{for(int j=0;j<3;j++)
		{if(isPlaceBle(x+i,y+j,gameState))
		{
			BasicCommands.drawTile(out, gameState.getTileByPos(x+i,y+j), is);
		}
		
		}}
	
	}
	public boolean isPlaceBle(int x,int y,GameState gameState)
	{if(x<0||x>8||y<0||y>4||gameState.getUserPlayer().getUnit().containsKey(gameState.getTileByPos(x,y)))//这里得再加AI类的unit判断
	{return false;}
	return true;
	}
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		
			int handPosition = message.get("position").asInt();
			int pos=handPosition-1;
			BasicCommands.drawCard(out, gameState.getUserPlayer().getCard().get(pos), handPosition, 1);
		if(gameState.getUserPlayer().getCard().get(pos).isCreature())
			{for(Tile cur:gameState.getUserPlayer().getUnit().keySet())
		{
			PlaceableArea(out,gameState,cur,1);
			
		}
			}
		
		else
		{String name=gameState.getUserPlayer().getCard().get(pos).getCardname();
		
		switch(name)
		{
		case "DarkTerminus":for(Tile cur:gameState.getAiPlayer().getUnit().keySet())
		{
			BasicCommands.drawTile(out, cur, 2);
		}
		break;
		
		case "WraithlingSwarm":
			Set<Tile> keySet1 = gameState.getUserPlayer().getUnit().keySet();
			Set<Tile> keySet2 = gameState.getAiPlayer().getUnit().keySet();
			Set<Tile> combinedKeySet = new HashSet<>(keySet1);
			combinedKeySet.addAll(keySet2);
			for(Tile cur:combinedKeySet)
			{
				PlaceableArea(out,gameState,cur,1);
				
			}
			//To do，召唤生物
			break;
			
		case "HornoftheForsaken":for(Tile cur:gameState.getUserPlayer().getUnit().keySet())
		{
			if(gameState.getUserPlayer().getUnit().get(cur).getId()==0)//假设avatarid为0；
			{
				BasicCommands.drawTile(out, cur, 2);
				break;
			}
		}
			//显示Avatar位置
			break;
		
		}
			
		}
		
		
		
		
		
	}

}


