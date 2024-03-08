package structures.basic.card;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import structures.basic.unit.SilverguardSquire;

public class SilverguardSquireCard extends Card {

	public static final String CARD_NAME = "Silverguard Squire";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		highlightTilesAsCreatureCard(out, gameState, GameState.AI_MODE);
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return SilverguardSquire.class;
	}
}
