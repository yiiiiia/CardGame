package structures.basic.unit;

import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class AIAvatar extends Unit {
    
    protected int health;
    protected int attack;

    public AIAvatar() {
        super();
    }

    public static AIAvatar getInstance(String configpaths) {
        return (AIAvatar)BasicObjectBuilders.loadUnit(configpaths, 1, AIAvatar.class);
    }
}
