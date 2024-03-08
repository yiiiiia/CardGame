package structures.basic.card;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import structures.basic.unit.IroncliffeGuardian;

public class IroncliffeGuardianCard extends Card {

	public static final String CARD_NAME = "Ironcliff Guardian";

	@Override
	public void highlightTiles(ActorRef out, GameState gameState) {
		highlightTilesAsCreatureCard(out, gameState, GameState.AI_MODE);
	}

	@Override
	public Class<? extends Unit> getSummonedCreatureClass() {
		return IroncliffeGuardian.class;
	}
}
