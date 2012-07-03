package edu.ucsc.eis.mario.repairs;

import edu.ucsc.eis.mario.events.ValueChange;
import edu.ucsc.eis.mario.sprites.Mario;

/**
 * User: cflewis
 * Date: Feb 21, 2010
 * Time: 12:20:49 AM
 */
public class ValueRepair extends RepairEvent {
    private int valueToRepair;
    private int newValue;

    public ValueRepair(int valueToRepair, int newValue) {
        this.valueToRepair = valueToRepair;
        this.newValue = newValue;
    }

    public void execute() {
        switch (valueToRepair) {
            case ValueChange.COIN_CHANGE: Mario.coins = newValue; break;
        }
    }
}
