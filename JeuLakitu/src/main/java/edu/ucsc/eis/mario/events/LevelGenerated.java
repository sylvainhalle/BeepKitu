package edu.ucsc.eis.mario.events;

import edu.ucsc.eis.mario.level.Level;
import edu.ucsc.eis.mario.sprites.Mario;

/**
 * Created by IntelliJ IDEA.
 * User: cflewis
 * Date: Feb 20, 2010
 * Time: 5:24:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class LevelGenerated extends MarioEvent {
    Level level;

    public LevelGenerated(Mario mario, Level level) {
        super(mario);
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }
}
