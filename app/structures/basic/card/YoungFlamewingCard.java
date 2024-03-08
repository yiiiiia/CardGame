package structures.basic.card;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import structures.basic.unit.YoungFlamewing;

public class YoungFlamewingCard extends Card {

	public static final String CARD_NAME = "Young Flamewing";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		highlightTilesAsCreatureCard(out, gameState, GameState.AI_MODE);
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return YoungFlamewing.class;
	}
}
