package events;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.stream.impl.fusing.Map;
import commands.BasicCommands;
import events.EventProcessor;
import structures.GameState;

import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a card. The event returns the position in the player's hand the card
 * resides within.
 * <p>
 * { messageType = “cardClicked” position = <hand index position [1-6]> }
 *
 * @author Dr. Richard McCreadie
 */
public class CardcCicked implements EventProcessor {

    /*
     * This method is used to detect the placeable area near the tile, and then mark
     * the color of the placeable area as highlight white
     *
     * @param tile:Tiles that need to be detected is:Which state to convert the tile
     * to
     */
    // Later put this method it in GameState

    /*
     * This method is used to determine whether the coordinates are a placeable
     * area.
     *
     * @param x:The abscissa of the tile y:vertical coordinate of tile
     *
     * @return:Return value of Boolean type, true means it can be placed, false
     * means it cannot be placed.
     */
    // Later put this method it in GameState

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        int handPosition = message.get("position").asInt();
        int pos = handPosition - 1;
        if (gameState.getActivateCard() != null
                && gameState.getUserPlayer().getHandCards().get(pos) != gameState.getActivateCard()) {
            {
                for (Tile cur : gameState.getUserPlayer().getAllUnitsAndTile().keySet()) {
                    gameState.PlaceableArea(out, cur, 0);

                }
            }
        }

        // If it is a summoning card, detect the place where it can be placed
        if (gameState.getUserPlayer().getHandCards().get(pos).isCreature()) {
            for (Tile cur : gameState.getUserPlayer().getAllUnitsAndTile().keySet()) {
                gameState.PlaceableArea(out, cur, 1);

            }
            gameState.setActivateCard(gameState.getUserPlayer().getHandCards().get(pos));
        }
        // Non-creature card, detect which spell card it is(based on the card's name)
        else {
            String name = gameState.getUserPlayer().getHandCards().get(pos).getCardname();

            switch (name) {
                case "DarkTerminusCard":
                    for (Tile cur : gameState.getAiPlayer().getAllUnitsAndTile().keySet()) {
                        BasicCommands.drawTile(out, cur, 2);
                    }
                    break;

                case "WraithlingSwarmCard":
                    Set<Tile> keySet1 = gameState.getUserPlayer().getAllUnitsAndTile().keySet();
                    Set<Tile> keySet2 = gameState.getAiPlayer().getAllUnitsAndTile().keySet();
                    Set<Tile> combinedKeySet = new HashSet<>(keySet1);
                    combinedKeySet.addAll(keySet2);
                    for (Tile cur : combinedKeySet) {
                        gameState.PlaceableArea(out, cur, 1);

                    }

                    break;

                case "HornOfTheForsakenCard":
                    for (Tile cur : gameState.getUserPlayer().getAllUnitsAndTile().keySet()) {
                        if (gameState.getUserPlayer().getAllUnitsAndTile().get(cur).getId() == 0)// 假设avatarid为0；
                        {
                            BasicCommands.drawTile(out, cur, 2);
                            break;
                        }
                    }
                    break;

            }
            gameState.setActivateCard(gameState.getUserPlayer().getHandCards().get(pos));
        }

    }
}
