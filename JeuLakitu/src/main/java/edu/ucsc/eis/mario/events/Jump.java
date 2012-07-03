package edu.ucsc.eis.mario.events;

import edu.ucsc.eis.mario.sprites.Mario;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Jump extends MarioEvent {
	private final int jumpTime;
    private final float xa;
    private final float ya;

    public Jump(Mario mario, int jumpTime) {
		super(mario);
        this.jumpTime = jumpTime;
        xa = mario.xa;
        ya = mario.ya;
	}

    public int getJumpTime() {
        return jumpTime;
    }

    public float getXAcceleration() {
        return xa;
    }

    @Override
	public String toString() {
		return new ToStringBuilder(this).
			append("Jump Time", this.jumpTime).
			toString();
	}
}
