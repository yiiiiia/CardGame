package structures;

import akka.actor.ActorRef;

public interface Action { 
    void doAction(ActorRef out, GameState gameState);
}