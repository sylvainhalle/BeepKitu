package edu.ucsc.eis.mario.events;

import edu.ucsc.eis.mario.sprites.Mario;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: cflewis
 * Date: Feb 20, 2010
 * Time: 10:52:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarioPosition extends MarioEvent {
    float x;
    float y;
    int spriteId;

    public MarioPosition(Mario mario) {
        super(mario);
        this.x = mario.getX();
        this.y = mario.getY();
        this.spriteId = mario.getId();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getId() {
        return this.spriteId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("ID:", this.getId()).
			append("X:", this.getX()).
			append("Y:", this.getY()).
			toString();
    }
}
