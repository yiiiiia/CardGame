import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Test;
import structures.basic.Card;
import structures.basic.card.WraithlingSwarmCard;
import structures.basic.unit.Wraithling;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

public class InitializeTest {

	@Test
	public void testLoadCard() {
		List<Card> cards = OrderedCardLoader.getPlayer1Cards(1);
		for (Card card : cards) {
			int castType = card.getSpellCastType();
			if (card.getIsCreature()) {
				assertEquals("Cards that summon creatres do not have cast type", 0, castType);
			} else if (card.getCardname().equals(WraithlingSwarmCard.CARD_NAME)) {
				assertEquals(" WraithlingSwarmCard card do not have cast type", 0, castType);
			} else {
				assertTrue(castType == Card.CAST_TO_ALLY || castType == Card.CAST_TO_ENEMY);
			}
		}
	}

	@Test
	public void testWraithling() {
		Wraithling wraithling = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 0, Wraithling.class);
		assertEquals(wraithling.getAttack(), 1);
		assertEquals(wraithling.getHealth(), 1);
	}
}
