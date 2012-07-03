package edu.ucsc.eis.mario.repairs;

/**
 * Created by IntelliJ IDEA.
 * User: cflewis
 * Date: Feb 20, 2010
 * Time: 11:30:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveSprite extends RepairEvent {
    private int spriteId;

    public RemoveSprite(int spriteId) {
        this.spriteId = spriteId;
    }

    public void execute() {
        if (this.spriteId == this.mario.getId()) {
            this.mario.die();
        }
        else {
            this.mario.getWorld().removeSprite(this.spriteId);   
        }
    }
}
