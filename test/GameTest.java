import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.unit.AIAvatar;
import structures.basic.unit.PlayerAvatar;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

public class GameTest {

	private static ObjectMapper mapper = new ObjectMapper();
	private static GameState gameState;
	private static Map<String, Card> allCardsMap = new HashMap<>();
	private static Map<String, Unit> allUnitsMap = new HashMap<>();
	private static EventProcessor tileClicked = new TileClicked();
	private static EventProcessor cardClicked = new CardClicked();

	@BeforeClass
	public static void initGameState() {
		BasicCommands.altTell = new TestTell();
		gameState = new GameState();
		gameState.initGameTiles();
		Player user = new Player();
		Player ai = new AiPlayer(20, 0);
		gameState.setUserPlayer(user);
		gameState.setAiPlayer(ai);
		Unit userAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, GameState.genUnitId(),
				PlayerAvatar.class);
		Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, GameState.genUnitId(), AIAvatar.class);
		gameState.setUserAvatar(userAvatar);
		gameState.setAiAvatar(aiAvatar);

		List<Card> userCards = OrderedCardLoader.getPlayer1Cards(1);
		List<Card> aiCards = OrderedCardLoader.getPlayer2Cards(1);
		List<Card> allCards = new ArrayList<>();
		allCards.addAll(userCards);
		allCards.addAll(aiCards);
		for (Card card : allCards) {
			allCardsMap.put(card.getCardname(), card);
			if (card.getIsCreature()) {
				Unit u = BasicObjectBuilders.loadUnit(card.getUnitConfig(), GameState.genUnitId(),
						card.getSummonedCreatureClass());
				allUnitsMap.put(u.getName(), u);
			}
		}
	}

	@Before
	public void clearGameState() {
		gameState.clearActiveCard(null);
		gameState.clearActiveUnit();
		gameState.clearDelegateCard();
		gameState.clearHighlightedTiles(null);
		Map<Tile, Unit> map = gameState.getUserPlayer().getAllUnitsAndTile();
		var iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			var entry = iter.next();
			Tile tile = entry.getKey();
			tile.clearUnit();
			iter.remove();
		}
		map = gameState.getAiPlayer().getAllUnitsAndTile();
		iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			var entry = iter.next();
			Tile tile = entry.getKey();
			tile.clearUnit();
			iter.remove();
		}
		gameState.getUserPlayer().setMana(0);
		gameState.getUserPlayer().setHealth(20);
		gameState.getAiPlayer().setMana(0);
		gameState.getAiPlayer().setHealth(20);
		Tile userTile = gameState.getTileByPos(1, 2);
		gameState.getUserPlayer().putUnitOnTile(userTile, gameState.getUserAvatar());
		Tile aiTile = gameState.getTileByPos(7, 2);
		gameState.getAiPlayer().putUnitOnTile(aiTile, gameState.getAiAvatar());
	}

	@Test
	public void testTilesUnitCanMoveto() {
		Player user = gameState.getUserPlayer();
		Unit gloomChaser = allUnitsMap.get("GloomChaser");
		Tile t = gameState.getTileByPos(0, 0);
		user.putUnitOnTile(t, gloomChaser);
		Set<Tile> tiles = gameState.getTilesUnitCanMoveTo(gloomChaser);
		assertEquals(5, tiles.size());

		Unit badOmen = allUnitsMap.get("BadOmen");
		assertTrue(badOmen != null);
		Tile nextTile = gameState.getTileByPos(0, 1);
		user.putUnitOnTile(nextTile, badOmen);
		tiles = gameState.getTilesUnitCanMoveTo(gloomChaser);
		assertEquals(4, tiles.size());

		Unit yf = allUnitsMap.get("YoungFlamewing");
		nextTile = gameState.getTileByPos(1, 0);
		Player ai = gameState.getAiPlayer();
		ai.putUnitOnTile(nextTile, yf);
		tiles = gameState.getTilesUnitCanMoveTo(gloomChaser);
		assertEquals(2, tiles.size());
		Tile target = gameState.getTileByPos(1, 1);
		assertTrue(tiles.contains(target));
	}

	@Test
	public void testTileClick() {
		JsonNode message = mapper.valueToTree(Map.of("tilex", 1, "tiley", 2));
		tileClicked.processEvent(null, gameState, message);
		Tile t2 = gameState.getTileByPos(3, 2);
		var b = gameState.getHighlightedTiles().contains(t2);
		assertTrue(b);
	}

	@Test
	public void testCardClick() {
		Card pos1Card = allCardsMap.get("Dark Terminus");
		Card pos2Card = allCardsMap.get("Horn of the Forsaken");
		Player user = gameState.getUserPlayer();
		user.setMana(4);
		user.putCardAtPos(pos1Card, 0);
		user.putCardAtPos(pos2Card, 1);
		JsonNode message = mapper.valueToTree(Map.of("position", 1));
		cardClicked.processEvent(null, gameState, message);
		assertTrue(gameState.getHighlightedTiles().isEmpty());
		message = mapper.valueToTree(Map.of("position", 2));
		cardClicked.processEvent(null, gameState, message);
		assertTrue(gameState.getHighlightedTiles().size() == 1);
	}

	@Test
	public void testProvokeAbility() {
		Unit userAvatar = gameState.getUserAvatar();
		Unit swampEntangler = allUnitsMap.get("SwampEntangler");
		Unit silverguardSquire = allUnitsMap.get("SilverguardSquire");
		Tile userAvatarTile = gameState.getUnitTile(userAvatar);
		Tile t1 = gameState.getTileByPos(userAvatarTile.getTilex(), userAvatarTile.getTiley() + 1);
		Tile t2 = gameState.getTileByPos(userAvatarTile.getTilex() + 1, userAvatarTile.getTiley());
		gameState.getAiPlayer().putUnitOnTile(t1, silverguardSquire);
		gameState.getAiPlayer().putUnitOnTile(t2, swampEntangler);
		JsonNode message = mapper
				.valueToTree(Map.of("tilex", userAvatarTile.getTilex(), "tiley", userAvatarTile.getTiley()));
		tileClicked.processEvent(null, gameState, message);
		Set<Tile> highlightedTiles = gameState.getHighlightedTiles();
		assertTrue(highlightedTiles.contains(t2));
	}
}
