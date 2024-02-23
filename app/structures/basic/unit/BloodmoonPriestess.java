package structures.basic.unit;

import java.util.List;
import java.util.Random;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class BloodmoonPriestess extends Unit {
        
    private int health;
    private int attack;

    public BloodmoonPriestess() {
        super();
        health = 3;
        attack = 3;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        int numUnit = gameState.getAllUnits().size();
        int prevNumUnit = numUnit;
        if (prevNumUnit > gameState.getAllUnits().size()) {
            Random r = new Random();
            List<Tile> emptyTiles = gameState.getAllTiles();
            for(Tile tile: emptyTiles) {
                if(tile.getUnit()!=null) {
                    emptyTiles.remove(tile);
                }
            }
            int randomTile = r.nextInt(emptyTiles.size());
            Wraithling wraithling = new Wraithling();
            //Wraithling wraithling = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Unit.class);
            wraithling.setPositionByTile(emptyTiles.get(randomTile));
            BasicCommands.drawUnit(out, wraithling, emptyTiles.get(randomTile));
            gameState.getPlayerUnits().put(emptyTiles.get(randomTile), wraithling);
        }
    }

}