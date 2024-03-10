package actors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.ActorRef;
import events.CardClicked;
import events.EndTurnClicked;
import events.EventProcessor;
import events.Heartbeat;
import events.Initalize;
import events.OtherClicked;
import events.TileClicked;
import events.UnitMoving;
import events.UnitStopped;
import play.libs.Json;
import structures.GameState;
import utils.ImageListForPreLoad;

/**
 * The game actor is an Akka Actor that receives events from the user front-end
 * UI (e.g. when the user clicks on the board) via a websocket connection. When
 * an event arrives, the processMessage() method is called, which can be used to
 * react to the event. The Game actor also includes an ActorRef object which can
 * be used to issue commands to the UI to change what the user sees. The
 * GameActor is created when the user browser creates a websocket connection to
 * back-end services (on load of the game web page).
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameActor extends AbstractActor {
	public static final String INITIALIZE_EVENT = "initalize";
	public static final String HEARTBEAT_EVENT = "heartbeat";
	public static final String UNIT_MOVING_EVENT = "unitMoving";
	public static final String UNIT_STOPPED_EVENT = "unitstopped";
	public static final String TILE_CLICK_EVENT = "tileclicked";
	public static final String CARD_CLICK_EVENT = "cardclicked";
	public static final String END_TURN_EVENT = "endturnclicked";
	public static final String OTHER_CLICK_EVENT = "otherclicked";

	private ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to turn java objects to
														// Strings
	private ActorRef out; // The ActorRef can be used to send messages to the front-end UI
	private Map<String, EventProcessor> eventProcessors; // Classes used to process each type of event
	private GameState gameState; // A class that can be used to hold game state information

	/**
	 * Constructor for the GameActor. This is called by the GameController when the
	 * websocket connection to the front-end is established.
	 * 
	 * @param out
	 */
	@SuppressWarnings("deprecation")
	public GameActor(ActorRef out) {

		this.out = out; // save this, so we can send commands to the front-end later

		// create class instances to respond to the various events that we might recieve
		eventProcessors = new HashMap<String, EventProcessor>();
		eventProcessors.put(INITIALIZE_EVENT, new Initalize());
		eventProcessors.put(HEARTBEAT_EVENT, new Heartbeat());
		eventProcessors.put(UNIT_MOVING_EVENT, new UnitMoving());
		eventProcessors.put(UNIT_STOPPED_EVENT, new UnitStopped());
		eventProcessors.put(TILE_CLICK_EVENT, new TileClicked());
		eventProcessors.put(CARD_CLICK_EVENT, new CardClicked());
		eventProcessors.put(END_TURN_EVENT, new EndTurnClicked());
		eventProcessors.put(OTHER_CLICK_EVENT, new OtherClicked());

		// Initalize a new game state object
		gameState = new GameState();

		// Get the list of image files to pre-load the UI with
		Set<String> images = ImageListForPreLoad.getImageListForPreLoad();

		try {
			ObjectNode readyMessage = Json.newObject();
			readyMessage.put("messagetype", "actorReady");
			readyMessage.put("preloadImages", mapper.readTree(mapper.writeValueAsString(images)));
			out.tell(readyMessage, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method simply farms out the processing of the json messages from the
	 * front-end to the processMessage method
	 * 
	 * @return
	 */
	public Receive createReceive() {
		return receiveBuilder().match(JsonNode.class, message -> {
			System.out.println(message);
			processMessage(message.get("messagetype").asText(), message);
		}).build();
	}

	/**
	 * This looks up an event processor for the specified message type. Note that
	 * this processing is asynchronous.
	 * 
	 * @param messageType
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "deprecation" })
	public void processMessage(String messageType, JsonNode message) throws Exception {
		EventProcessor processor = eventProcessors.get(messageType);
		if (processor == null) {
			// Unknown event type received
			System.err.println("GameActor: Recieved unknown event type " + messageType);
			return;
		}
		if (gameState.getDelegatedCard() != null) {
			gameState.getDelegatedCard().delegateEventProcess(out, gameState, messageType, message);
			return;
		}
		if (messageType.equals(HEARTBEAT_EVENT) || messageType.equals(UNIT_MOVING_EVENT)
				|| messageType.equals(UNIT_STOPPED_EVENT)) {
			processor.processEvent(out, gameState, message);
			return;
		}
		if (gameState.isGameOver()) {
			System.err.println("ignore incoming events: game is over");
		} else if (gameState.getGameMode() == GameState.AI_MODE) {
			System.err.println("Ignore incoming events: game is in AI mode");
		} else if (gameState.hasMovingUnit()) {
			System.err.println("Ignore incoming events: has unit moving");
		} else {
			processor.processEvent(out, gameState, message); // process the event
		}
	}

	public void reportError(String errorText) {
		ObjectNode returnMessage = Json.newObject();
		returnMessage.put("messagetype", "ERR");
		returnMessage.put("error", errorText);
		out.tell(returnMessage, out);
	}
}
