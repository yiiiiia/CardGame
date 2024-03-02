package structures.basic.unit;

import java.util.List;

import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class YoungFlamewing extends Unit {
    
    public static final int initialHealth = 4;
    public static final int initialAttack = 5;

    protected int health;
    protected int attack;

    public YoungFlamewing() {
        super();
        health = 4;
        attack = 5;
    }

    public List<Tile> tilesFlyingUnitCanMoveTo (GameState gameState) {
        return gameState.getAllTiles();
    }

    public YoungFlamewing getInstance(String configpaths) {
        return (YoungFlamewing)BasicObjectBuilders.loadUnit(configpaths, 15, YoungFlamewing.class);
    }
}
