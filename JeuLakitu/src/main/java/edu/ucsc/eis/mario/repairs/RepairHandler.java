package edu.ucsc.eis.mario.repairs;

import com.google.common.base.Preconditions;
import edu.ucsc.eis.mario.sprites.Mario;

/**
 * Created by IntelliJ IDEA.
 * User: cflewis
 * Date: Dec 15, 2009
 * Time: 2:52:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepairHandler {
    protected Mario mario;

    public RepairHandler() {
    }

    public void setMario(Mario mario) {
        this.mario = mario;
    }

    public void execute(RepairEvent event) {
        event.setMario(mario);
        event.execute();
    }
}
