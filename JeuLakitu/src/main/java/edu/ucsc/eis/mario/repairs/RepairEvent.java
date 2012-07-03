package edu.ucsc.eis.mario.repairs;

import edu.ucsc.eis.mario.sprites.Mario;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: cflewis
 * Date: Dec 15, 2009
 * Time: 2:42:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RepairEvent implements Serializable {
    protected Mario mario;

    public RepairEvent() {
    }

    public abstract void execute();

    public Mario getMario() {
        return mario;
    }

    public void setMario(Mario mario) {
        this.mario = mario;
    }
    
}
