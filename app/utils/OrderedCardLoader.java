package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import structures.GameState;
import structures.basic.Card;
import structures.basic.card.BadOmenCard;
import structures.basic.card.BeamShockCard;
import structures.basic.card.BloodmoonPriestessCard;
import structures.basic.card.DarkTerminusCard;
import structures.basic.card.GloomChaserCard;
import structures.basic.card.HornOfTheForsakenCard;
import structures.basic.card.IroncliffeGuardianCard;
import structures.basic.card.NightsorrowAssassinCard;
import structures.basic.card.RockPulveriserCard;
import structures.basic.card.SaberspineTigerCard;
import structures.basic.card.ShadowDancerCard;
import structures.basic.card.ShadowWatcherCard;
import structures.basic.card.SilverguardKnightCard;
import structures.basic.card.SilverguardSquireCard;
import structures.basic.card.SkyrockGolemCard;
import structures.basic.card.SundropElixirCard;
import structures.basic.card.SwampEntanglerCard;
import structures.basic.card.TrueStrikeCard;
import structures.basic.card.WraithlingSwarmCard;
import structures.basic.card.YoungFlamewingCard;

/**
 * This is a utility class that provides methods for loading the decks for each
 * player, as the deck ordering is fixed.
 * 
 * @author Richard
 *
 */
public class OrderedCardLoader {

	public static String cardsDIR = "conf/gameconfs/cards/";

	/**
	 * Returns all of the cards in the human player's deck in order
	 * 
	 * @return
	 */
	public static List<Card> getPlayer1Cards(int copies) {
		List<Card> userCards = new ArrayList<Card>(20);
		for (int i = 0; i < copies; i++) {
			for (String filename : new File(cardsDIR).list()) {
				if (filename.equals("1_1_c_u_bad_omen.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							BadOmenCard.class));
				else if (filename.equals("1_2_c_s_hornoftheforsaken.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							HornOfTheForsakenCard.class));
				else if (filename.equals("1_3_c_u_gloom_chaser.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							GloomChaserCard.class));
				else if (filename.equals("1_4_c_u_shadow_watcher.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							ShadowWatcherCard.class));
				else if (filename.equals("1_5_c_s_wraithling_swarm.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							WraithlingSwarmCard.class));
				else if (filename.equals("1_6_c_u_nightsorrow_assassin.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							NightsorrowAssassinCard.class));
				else if (filename.equals("1_7_c_u_rock_pulveriser.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							RockPulveriserCard.class));
				else if (filename.equals("1_8_c_s_dark_terminus.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							DarkTerminusCard.class));
				else if (filename.equals("1_9_c_u_bloodmoon_priestess.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							BloodmoonPriestessCard.class));
				else if (filename.equals("1_a1_c_u_shadowdancer.json"))
					userCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							ShadowDancerCard.class));
			}
		}
		return userCards;
	}

	/**
	 * Returns all of the cards in the human player's deck in order
	 * 
	 * @return
	 */
	public static List<Card> getPlayer2Cards(int copies) {
		List<Card> aiCards = new ArrayList<Card>(20);
		for (int i = 0; i < copies; i++) {
			for (String filename : new File(cardsDIR).list()) {
				if (filename.equals("2_1_c_u_skyrock_golem.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							SkyrockGolemCard.class));
				else if (filename.equals("2_2_c_u_swamp_entangler.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							SwampEntanglerCard.class));
				else if (filename.equals("2_3_c_u_silverguard_knight.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							SilverguardKnightCard.class));
				else if (filename.equals("2_4_c_u_saberspine_tiger.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							SaberspineTigerCard.class));
				else if (filename.equals("2_5_c_s_beamshock.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							BeamShockCard.class));
				else if (filename.equals("2_6_c_u_young_flamewing.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							YoungFlamewingCard.class));
				else if (filename.equals("2_7_c_u_silverguard_squire.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							SilverguardSquireCard.class));
				else if (filename.equals("2_8_c_u_ironcliff_guardian.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							IroncliffeGuardianCard.class));
				else if (filename.equals("2_9_c_s_sundrop_elixir.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							SundropElixirCard.class));
				else if (filename.equals("2_a1_c_s_truestrike.json"))
					aiCards.add(BasicObjectBuilders.loadCard(cardsDIR + filename, GameState.genCardId(),
							TrueStrikeCard.class));
			}
		}
		return aiCards;
	}

}
