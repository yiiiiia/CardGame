package structures.basic.unit;

import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class PlayerAvatar extends Unit {

    protected int health;
    protected int attack;
    
    public PlayerAvatar() {
        super();
    }

    public static PlayerAvatar getInstance(String configpaths) {
        return (PlayerAvatar)BasicObjectBuilders.loadUnit(configpaths, 0, PlayerAvatar.class);
    }
}
