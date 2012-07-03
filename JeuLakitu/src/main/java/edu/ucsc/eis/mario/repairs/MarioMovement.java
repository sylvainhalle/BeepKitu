package edu.ucsc.eis.mario.repairs;

/**
 * Created by IntelliJ IDEA.
 * User: cflewis
 * Date: Dec 15, 2009
 * Time: 2:49:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarioMovement extends RepairEvent {
    public final Boolean jumping;
    public final Float ySpeed;
    public final Float xSpeed;

    public MarioMovement(Boolean jumping, Float ySpeed, Float xSpeed) {
        this.jumping = jumping;
        this.ySpeed = ySpeed;
        this.xSpeed = xSpeed;
    }

    @Override
    public void execute() {
        if (jumping != null) {
            if (jumping) {
                this.mario.setJumpTime(11);  
            } else {
                this.mario.setJumpTime(-1);
                this.mario.setYJumpSpeed(500f);
            }
        }

        if (mario.getJumpTime() == 0) {
            if (ySpeed != null) { this.mario.ya = ySpeed; }
            if (xSpeed != null) { this.mario.xa = xSpeed; }
        } else {
            if (ySpeed != null) { this.mario.setYJumpSpeed(ySpeed); }
            if (xSpeed != null) { this.mario.setXJumpSpeed(xSpeed); }
        }
    }
}
