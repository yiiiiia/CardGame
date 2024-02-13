package structures;

import structures.basic.Player;
import structures.basic.Tile;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;
	public Status game_status;
	public Player[] players=new Player[2];
	
	
	public enum Status
	{
		IDLE,
		UNIT_SELECTED,
		AI
		
		
	}
	
	public Tile[][] boardTile;//后面再变私有
	public GameState()
	{boardTile=new Tile[9][5];
	game_status=Status.IDLE;
	
		
	}
	
	
	
	
 
	
	
}
