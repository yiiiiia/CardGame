package structures.basic.unit;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class GloomChaser extends Unit{

    public static final int initialHealth = 1;
    public static final int initialAttack = 3;
    
    protected int health;
    protected int attack;
    
    public GloomChaser() {
        super();
        health = 1;
        attack = 3;
    }

    public void performGambit(ActorRef out, GameState gameState) {
        Tile tile = gameState.getTileByPos(this.getPosition().getTilex()-1, this.getPosition().getTiley());
        if(tile.getUnit()==null) {
            BasicCommands.playEffectAnimation(out, "f1_wraithsummon", tile);
            Wraithling wraithling = (Wraithling)BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 1, Wraithling.class);
            wraithling.setPositionByTile(tile);
            BasicCommands.drawUnit(out, wraithling, tile);
            gameState.getPlayerUnits().put(tile, wraithling);
        }
    }

    public static GloomChaser getInstance(String configpaths) {
        return (GloomChaser) BasicObjectBuilders.loadUnit(configpaths, 3w, GloomChaser.class);
    }
}
