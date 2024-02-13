package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.GameState.Status;
import structures.basic.Tile;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{


	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		for(int i=0;i<gameState.players[0].getNumCard();i++)
		{
			BasicCommands.drawCard(out, gameState.players[0].getCard()[i], i+1, 0);
		}
		
		
		
		
			int tilex = message.get("tilex").asInt();
			int tiley = message.get("tiley").asInt();
			//后面用switch
			
			if(gameState.players[0].getUnit().containsKey(gameState.boardTile[tilex][tiley]))//当前块有玩家单位
			{
				//高亮显示这个tile上的可移动区域
			}
			
		
		
	

		
		
	}

}
