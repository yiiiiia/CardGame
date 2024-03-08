package structures.basic.card;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import structures.basic.unit.SwampEntangler;

public class SwampEntanglerCard extends Card {

	public static final String CARD_NAME = "Swamp Entangler";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		highlightTilesAsCreatureCard(out, gameState, GameState.AI_MODE);
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return SwampEntangler.class;
	}
}
