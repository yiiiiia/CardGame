package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

public class AI extends Player {

	public AI(int health, int mana) {
		super(health, mana);
	}

	public void playAiLogic(ActorRef out, GameState gameState) {
		BasicCommands.addPlayer1Notification(out, "In Ai mode now, sleep 2 seconds", 2);
		BasicCommands.sleep(2 * 1000);
		BasicCommands.addPlayer1Notification(out, "Ai mode is over", 2);
		gameState.setGameMode(GameState.USER_MODE);

		// TODO incorporate with jiangdong's code
	}
}
