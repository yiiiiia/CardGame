package structures.basic.unit;

import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class SaberspineTiger extends Unit {
    
    public static final int initialHealth = 2;
    public static final int initialAttack = 3;

    protected int health;
    protected int attack;

    public SaberspineTiger() {
        super();
        health = 2;
        attack = 3;
    }

    public void rush() {
        //allow the saberspineTiger to attack on the turn it is summoned
    }

    public SaberspineTiger getInstance(String configpaths) {
        return (SaberspineTiger)BasicObjectBuilders.loadUnit(configpaths, 13, SaberspineTiger.class);
    }
}
