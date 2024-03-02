package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import structures.basic.Card;

/**
 * This is a utility class that provides methods for loading the decks for each
 * player, as the deck ordering is fixed. 
 * @author Richard
 *
 */
public class OrderedCardLoader {

	public static String cardsDIR = "conf/gameconfs/cards/";
	
	/**
	 * Returns all of the cards in the human player's deck in order
	 * @return
	 */
	public static List<Card> getPlayer1Cards(int copies) {
	
		List<Card> cardsInDeck = new ArrayList<Card>(20);
		
		int cardID = 1;
		for (int i =0; i<copies; i++) {
			for (String filename : new File(cardsDIR).list()) {
				if (filename.equals("1_1_c_u_bad_omen.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, BadOmenCard.class));
				else if (filename.equals("1_2_c_s_hornoftheforsaken.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, HornoftheForsakenCard.class));
				else if (filename.equals("1_3_c_u_gloom_chaser.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, GloomChaserCard.class));
				else if (filename.equals("1_4_c_u_shadow_watcher.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, ShadowWatcherCard.class));
				else if (filename.equals("1_5_c_s_wraithling_swarm.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, WraithlingSwarmCard.class));
				else if (filename.equals("1_6_c_u_nightsorrow_assassin.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, NightsorrowAssassinCard.class));
				else if (filename.equals("1_7_c_u_rock_pulveriser.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, RockPulveriserCard.class));
				else if (filename.equals("1_8_c_s_dark_terminus.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, DarkTerminusCard.class));
				else if (filename.equals("1_9_c_u_bloodmoon_priestess.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, BloodmoonPriestessCard.class));
				else if (filename.equals("1_a1_c_u_shadowdancer.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, ShadowDancerCard.class));
				cardID++;

			}
			if(cardID==cardsInDeck.size())break;
		}

		return cardsInDeck;
	}
	
	
	/**
	 * Returns all of the cards in the human player's deck in order
	 * @return
	 */
	public static List<Card> getPlayer2Cards(int copies) {
	
		List<Card> cardsInDeck = new ArrayList<Card>(20);
		
		int cardID = 1;
		for (int i =0; i<copies; i++) {
			for (String filename : new File(cardsDIR).list()) {
				if (filename.equals("2_1_c_u_skyrock_golem.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, SkyrockGolemCard.class));
				else if (filename.equals("2_2_c_u_swamp_entangler.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, SwampEntanglerCard.class));
				else if (filename.equals("2_3_c_u_silverguard_knight.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, SilverguardKnightCard.class));
				else if (filename.equals("2_4_c_u_saberspine_tiger.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, SaberspineTigerCard.class));
				else if (filename.equals("2_5_c_s_beamshock.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, BeamShockCard.class));
				else if (filename.equals("2_6_c_u_young_flamewing.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, YoungFlamewingCard.class));
				else if (filename.equals("2_7_c_u_silverguard_squire.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, SilverguardSquireCard.class));
				else if (filename.equals("2_8_c_u_ironcliff_guardian.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, IroncliffeGuardianCard.class));
				else if (filename.equals("2_9_c_s_sundrop_elixir.json")) cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, SundropElixirCard.class));
				else cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, TrueStrikeCard.class));
				cardID++;
				if(cardID==cardsInDeck.size()) break;
			}
		}

		return cardsInDeck;
	}
	
}
