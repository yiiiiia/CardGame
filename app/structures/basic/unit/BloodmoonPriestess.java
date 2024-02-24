package structures.basic.unit;

import java.util.List;
import java.util.Random;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class BloodmoonPriestess extends Unit {
        
    protected int health;
    protected int attack;

    public BloodmoonPriestess() {
        super();
        health = 3;
        attack = 3;
    }

    public void performDeathWatch(ActorRef out, GameState gameState) {
        //whenever a unit, friendly or enemy dies
        Random r = new Random();
        List<Tile> emptyTiles = gameState.getAllTiles();
        for(Tile tile: emptyTiles) {
            if(tile.getUnit()!=null) {
                emptyTiles.remove(tile);
            }
        }
        if (emptyTiles.size() > 0) {
            int randomTile = r.nextInt(emptyTiles.size());
            BasicCommands.playEffectAnimation(out, "f1_wraithsummon", emptyTiles.get(randomTile);
            Wraithling wraithling = (Wraithling)BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Wraithling.class);
            wraithling.setPositionByTile(emptyTiles.get(randomTile));
            BasicCommands.drawUnit(out, wraithling, emptyTiles.get(randomTile));
            gameState.getPlayerUnits().put(emptyTiles.get(randomTile), wraithling);
        }
    }

    public static BloodmoonPriestess getInstance(String configpaths) {
        return (BloodmoonPriestess)BasicObjectBuilders.loadUnit(configpaths, 0, BloodmoonPriestess.class);
    }
}
