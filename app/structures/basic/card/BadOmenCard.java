package structures.basic.card;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import structures.basic.unit.BadOmen;

public class BadOmenCard extends Card {

	public static final String CARD_NAME = "Bad Omen";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		highlightTilesAsCreatureCard(out, gameState, GameState.USER_MODE);
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return BadOmen.class;
	}
}
