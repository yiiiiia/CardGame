package events;

import akka.stream.impl.FanOut;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

import java.util.*;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * <p>
 * {
 * messageType = “initalize”
 * }
 *
 * @author Dr. Richard McCreadie
 */
public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        //Turn Initialization
        gameState.setTurn(1);

        //PlayerMode Initialization
        gameState.setPlayerMode(gameState.HUMAN_MODE);

        //Players Initialization
        playersInitialization(out, gameState);

        //Board Initialization
        boardInitialization(out, gameState);

        //Avatars Initialization
        avatarsInitialization(out, gameState);

        //Deck Initialization
        deckInitialization(gameState);

        //Cards Initialization
        cardsInitialization(out, gameState);
    }


    private void playersInitialization(ActorRef out, GameState gameState) {
        Player player = new Player(20, 2);
        gameState.setUserPlayer(player);
        AI ai = new AI(20, 2);
        gameState.setAiPlayer(ai);

        //mana and health visualization
        BasicCommands.setPlayer1Mana(out, gameState.getUserPlayer());
        BasicCommands.setPlayer1Health(out, gameState.getAiPlayer());
        BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
    }

    private void boardInitialization(ActorRef out, GameState gameState) {
        gameState.initGameTiles();
        for (Tile gameTile : gameState.getGameTiles()) {
            BasicCommands.drawTile(out, gameTile, 0);
        }
    }

    private void avatarsInitialization(ActorRef out, GameState gameState) {
        //generate two values of id for each player
        Random random = new Random();
        List<Integer> ids = new ArrayList<>();
        while (ids.size() < 2) {
            int generatedId = random.nextInt(100 - 40 + 1) + 40;
            if (!ids.contains(generatedId)) {
                ids.add(generatedId);
            }
        }

        //create human avatar
        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, ids.get(0), Unit.class);
        humanAvatar.setHealth(20);
        humanAvatar.setAttack(2);
        //place human avatar
        humanAvatar.setPositionByTile(gameState.getTileByPos(1, 2));
        BasicCommands.drawUnit(out, humanAvatar, gameState.getTileByPos(1, 2));
        //add human avatar to player's allUnits map
        gameState.getUserPlayer().getAllUnits().put(gameState.getTileByPos(1, 2), humanAvatar);
        //set human avatar to the tile
        gameState.getTileByPos(1, 2).setUnit(humanAvatar);

        //create ai avatar
        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, ids.get(1), Unit.class);
        aiAvatar.setHealth(20);
        aiAvatar.setAttack(2);
        //place ai avatar
        aiAvatar.setPositionByTile(gameState.getTileByPos(7, 2));
        BasicCommands.drawUnit(out, aiAvatar, gameState.getTileByPos(7, 2));
        //add ai avatar to ai's allUnits map
        gameState.getAiPlayer().getAllUnits().put(gameState.getTileByPos(7, 2), aiAvatar);
        //set ai avatar to the tile
        gameState.getTileByPos(7, 2).setUnit(aiAvatar);

        gameState.sleepMilliseconds(2000);

        //setUnitAttack
        BasicCommands.setUnitAttack(out, humanAvatar, 2);
        BasicCommands.setUnitAttack(out, aiAvatar, 2);
        //setUnitHealth
        BasicCommands.setUnitHealth(out, humanAvatar, 20);
        BasicCommands.setUnitHealth(out, aiAvatar, 20);
    }

    private void deckInitialization(GameState gameState) {
        gameState.getUserPlayer().setCardsRemain(OrderedCardLoader.getPlayer1Cards(2));
        gameState.getAiPlayer().setCardsRemain(OrderedCardLoader.getPlayer2Cards(2));
    }


    private void cardsInitialization(ActorRef out, GameState gameState) {
        Random r = new Random();
        //draw three cards for human, and show them
        for (int i = 0; i < 3; i++) {
            int randomIndex = r.nextInt(gameState.getUserPlayer().getCardsRemain().size());
            Card drawnCard = gameState.getUserPlayer().getCardsRemain().get(randomIndex);
            gameState.getUserPlayer().getHandCard().add(drawnCard);
            BasicCommands.drawCard(out, drawnCard, i + 1, 0);
            gameState.getUserPlayer().getCardsRemain().remove(randomIndex);
        }
        //draw three cards for ai
        for (int i = 0; i < 3; i++) {
            int randomIndex = r.nextInt(gameState.getAiPlayer().getCardsRemain().size());
            Card drawnCard = gameState.getAiPlayer().getCardsRemain().get(randomIndex);
            gameState.getAiPlayer().getHandCard().add(drawnCard);
            gameState.getAiPlayer().getCardsRemain().remove(randomIndex);
        }
    }

}


