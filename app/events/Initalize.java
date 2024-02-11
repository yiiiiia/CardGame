package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change
		
		gameState.gameInitalised = true;
		
		gameState.something = true;
		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);

		 //new

        //player
        gameState.playerMode = 0;
        gameState.player = new Player(20,2);
        gameState.ai = new AI(20,2);
        //mana and health visualization
        BasicCommands.setPlayer1Mana(out,gameState.player);
        BasicCommands.setPlayer1Health(out,gameState.player);
        BasicCommands.setPlayer1Health(out,gameState.ai);



        //tile
        gameState.gameTiles = new Tile[9][5];
        for (int i = 0; i < gameState.gameTiles.length; i++) {
            for (int j = 0; j < gameState.gameTiles[0].length; j++) {
                gameState.gameTiles[i][j] = BasicObjectBuilders.loadTile(i, j);
                BasicCommands.drawTile(out, gameState.gameTiles[i][j], 0);
            }
        }

        //unit
        //create human avatars
        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 50, Unit.class);
        humanAvatar.setPositionByTile(gameState.gameTiles[1][2]);
        BasicCommands.drawUnit(out, humanAvatar, gameState.gameTiles[1][2]);
        gameState.player.m_unit.put(gameState.gameTiles[1][2],humanAvatar);
        //create ai avatar
        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 51, Unit.class);
        aiAvatar.setPositionByTile(gameState.gameTiles[7][2]);
        BasicCommands.drawUnit(out, aiAvatar, gameState.gameTiles[7][2]);
        gameState.ai.m_unit.put(gameState.gameTiles[7][2],aiAvatar);
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
        //setUnitAttack
        BasicCommands.setUnitAttack(out, humanAvatar, 2);
        BasicCommands.setUnitAttack(out, aiAvatar, 2);
        //setUnitHealth
        BasicCommands.setUnitHealth(out, humanAvatar, 20);
        BasicCommands.setUnitHealth(out, aiAvatar, 20);
        System.out.println("test");

        //deck
        System.out.println("deck");
        gameState.player.playerCardsRemain = OrderedCardLoader.getPlayer1Cards(2);
        gameState.ai.playerCardsRemain = OrderedCardLoader.getPlayer2Cards(2);
        //TODO: should be convert to the sub classes of card

        //draw three cards for human, and show them
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            int randomIndex = r.nextInt(gameState.player.playerCardsRemain.size());
            Card drawnCard=gameState.player.playerCardsRemain.get(randomIndex);
            gameState.player.playerCardsAtHand.add(drawnCard);
            BasicCommands.drawCard(out, drawnCard, i + 1, 0);
            gameState.player.playerCardsRemain.remove(randomIndex);
        }
        //draw three cards for ai
        for (int i = 0; i < 3; i++) {

            int randomIndex = r.nextInt(gameState.ai.playerCardsRemain.size());
            Card drawnCard=gameState.ai.playerCardsRemain.get(randomIndex);
            gameState.ai.playerCardsAtHand.add(drawnCard);
            gameState.ai.playerCardsRemain.remove(randomIndex);
        }
	}

}


