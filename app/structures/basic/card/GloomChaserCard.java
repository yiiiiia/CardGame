package structures.basic.card;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import structures.basic.unit.GloomChaser;

public class GloomChaserCard extends Card {

	public static final String CARD_NAME = "Gloom Chaser";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		highlightTilesAsCreatureCard(out, gameState, GameState.USER_MODE);
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return GloomChaser.class;
	}
}
