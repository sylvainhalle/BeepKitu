package com.mojang.mario.level;

import com.mojang.mario.LevelScene;
import com.mojang.mario.MonitorTimer;
import com.mojang.mario.sprites.*;

public class SpriteTemplate
{
    public int lastVisibleTick = -1;
    public Sprite sprite;
    public boolean isDead = false;
    private boolean winged;
    
	public static int numberEnemies = 0;
    public int id;
    
    private int type;
    
    public SpriteTemplate(int type, boolean winged)
    {
        this.type = type;
        this.winged = winged;
    }
    
    public void spawn(LevelScene world, int x, int y, int dir)
    {
        if (isDead)
        {
        	return;
        }

        if (type==Enemy.ENEMY_FLOWER)
        {
            sprite = new FlowerEnemy(world, x*16+15, y*16+24);
        }
        else
        {
        	numberEnemies++;
        	id = numberEnemies;
            sprite = new Enemy(world, x*16+8, y*16+15, dir, type, true,id);
        }
        sprite.spriteTemplate = this;
        world.addSprite(sprite);
    }
}