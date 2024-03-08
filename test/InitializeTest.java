import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Test;
import structures.basic.Card;
import structures.basic.unit.Wraithling;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

public class InitializeTest {

	@Test
	public void testLoadCard() {
		List<Card> cards = OrderedCardLoader.getPlayer1Cards(1);
		assertTrue(!cards.isEmpty());
	}

	@Test
	public void testWraithling() {
		Wraithling wraithling = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 0, Wraithling.class);
		assertEquals(wraithling.getAttack(), 1);
		assertEquals(wraithling.getHealth(), 1);
	}
}
