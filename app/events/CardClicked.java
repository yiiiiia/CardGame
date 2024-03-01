package draft;







import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.stream.impl.fusing.Map;
import commands.BasicCommands;
import events.EventProcessor;
import structures.GameState;

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
	
	/*This method is used to detect the placeable area near the tile, 
	 * and then mark the color of the placeable area as highlight white
	 *  @param tile:Tiles that need to be detected
	 *          is:Which state to convert the tile to
	 *  */
	
	public  void PlaceableArea(ActorRef out,GameState gameState,Tile tile,int is)
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
	
	/*This method is used to determine whether the coordinates are a placeable area.
	 *  @param x:The abscissa of the tile
	 *         y:vertical coordinate of tile
	 *  @return:Return value of Boolean type, true means it can be placed, 
	 *  false means it cannot be placed.
	 *  */
	public boolean isPlaceBle(int x,int y,GameState gameState)
	{if(x<0||x>8||y<0||y>4||gameState.getUserPlayer().getAllUnitsAndTile().containsKey(gameState.getTileByPos(x,y)))//这里得再加AI类的unit判断
	{return false;}
	return true;
	}
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		
			int handPosition = message.get("position").asInt();
			int pos=handPosition-1;
			if(gameState.getActivateCard()!=null)
			{
				{for(Tile cur:gameState.getUserPlayer().getAllUnitsAndTile().keySet())
				{
					PlaceableArea(out,gameState,cur,0);
					
				}
					}
			}
		
			//If it is a summoning card, detect the place where it can be placed
			if(gameState.getUserPlayer().getHandCards().get(pos).isCreature())
			{for(Tile cur:gameState.getUserPlayer().getAllUnitsAndTile().keySet())
		{
			PlaceableArea(out,gameState,cur,1);
			
		}
			gameState.setActivateCard(gameState.getUserPlayer().getHandCards().get(pos));
			}
		//Non-creature card, detect which spell card it is(based on the card's name)
		else
		{String name=gameState.getUserPlayer().getHandCards().get(pos).getCardname();
		
		switch(name)
		{
		case "DarkTerminusCard":for(Tile cur:gameState.getAiPlayer().getAllUnitsAndTile().keySet())
		{
			BasicCommands.drawTile(out, cur, 2);
		}
		break;
		
		case "WraithlingSwarmCard":
			Set<Tile> keySet1 = gameState.getUserPlayer().getAllUnitsAndTile().keySet();
			Set<Tile> keySet2 = gameState.getAiPlayer().getAllUnitsAndTile().keySet();
			Set<Tile> combinedKeySet = new HashSet<>(keySet1);
			combinedKeySet.addAll(keySet2);
			for(Tile cur:combinedKeySet)
			{
				PlaceableArea(out,gameState,cur,1);
				
			}
			
			break;
			
		case "HornOfTheForsakenCard":for(Tile cur:gameState.getUserPlayer().getAllUnitsAndTile().keySet())
		{
			if(gameState.getUserPlayer().getAllUnitsAndTile().get(cur).getId()==0)//假设avatarid为0；
			{
				BasicCommands.drawTile(out, cur, 2);
				break;
			}
		}		
			break;

		}
		gameState.setActivateCard(gameState.getUserPlayer().getHandCards().get(pos));
		}

	}
}


