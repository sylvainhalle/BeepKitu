package edu.ucsc.eis.mario.repairs;

/**
 * Created by IntelliJ IDEA.
 * User: cflewis
 * Date: Feb 13, 2010
 * Time: 12:33:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class DisableKeys extends RepairEvent {
    public DisableKeys() {
        super();
    }

    @Override
    public void execute() {
        this.mario.keys = new boolean[16];
    }
}
