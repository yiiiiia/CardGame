package structures.basic.unit;

import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class SkyrockGolem extends Unit {
    
    public static final int initialHealth = 2;
    public static final int initialAttack = 4;

    protected int health;
    protected int attack;

    public SkyrockGolem() {
        super();
        health = 2;
        attack = 4;
        
    }

    public SkyrockGolem getInstance(String configpaths) {
        return (SkyrockGolem)BasicObjectBuilders.loadUnit(configpaths, 12, SkyrockGolem.class);
    }
}
