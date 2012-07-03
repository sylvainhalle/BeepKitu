package edu.ucsc.eis.mario.repairs;

import edu.ucsc.eis.mario.level.Level;

/**
 * User: cflewis
 * Date: Feb 20, 2010
 * Time: 4:54:03 PM
 */
public class WriteBlocks extends RepairEvent {
    int endX;
    int startX;
    int height;

    public WriteBlocks(int endX, int startX, int height) {
        this.endX = endX;
        this.startX = startX;
        this.height = height;
    }

    @Override
    public void execute() {
        System.err.println("Writing blocks");
        Level level = this.mario.getWorld().level;

        for (int x = endX; x >= startX; x--) {
            level.setBlock(x, level.height - 1, (byte) (9 + 0 * 16));

            for (int y = 1; y <= height; y++) {
                level.setBlock(x, level.height - y, (byte) (9 + 0 * 16));
            }
        }
    }
}
