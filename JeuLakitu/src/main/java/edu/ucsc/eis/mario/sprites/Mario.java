package edu.ucsc.eis.mario.sprites;


import com.mojang.sonar.FixedSoundSource;

import edu.ucsc.eis.mario.Art;
import edu.ucsc.eis.mario.LevelScene;
import edu.ucsc.eis.mario.MarioComponent;
import edu.ucsc.eis.mario.Scene;
import edu.ucsc.eis.mario.events.Death;
import edu.ucsc.eis.mario.events.Jump;
import edu.ucsc.eis.mario.events.Landing;
import edu.ucsc.eis.mario.events.ValueChange;
import edu.ucsc.eis.mario.level.*;

import java.io.Serializable;


public class Mario extends Sprite implements Serializable
{
    public static boolean large = false;
    public static boolean fire = false;
    public static int coins = 0;
    public static int lives = 3;
    public static String levelString = "none";
    public static int maxJumpTime = 7;
    public static boolean dieOnFall = true;
    public static boolean stopMovementOnDeath = true;
    public static int coinValue = 1;
    private static boolean isInvincible = false;


    public static void resetStatic()
    {
        large = false;
        fire = false;
        coins = 0;
        lives = 3;
        levelString = "none";
    }

    public static final int KEY_LEFT = 0;
    public static final int KEY_RIGHT = 1;
    public static final int KEY_DOWN = 2;
    public static final int KEY_UP = 3;
    public static final int KEY_JUMP = 4;
    public static final int KEY_SPEED = 5;

    private static float GROUND_INERTIA = 0.89f;
    private static float AIR_INERTIA = 0.89f;

    public boolean[] keys;
    private float runTime;
    boolean wasOnGround = false;
    boolean onGround = false;
    private boolean mayJump = false;
    private boolean ducking = false;
	private boolean sliding = false;

	private int jumpTime = 0;
    private float xJumpSpeed;
    private float yJumpSpeed;
    private boolean canShoot = false;

    int width = 4;
    int height = 24;

    private transient LevelScene world;
    public int facing;
    private int powerUpTime = 0;

    public int xDeathPos, yDeathPos;

    public int deathTime = 0;
    public int winTime = 0;
    private int invulnerableTime = 0;

    public transient Sprite carried = null;
    private transient static Mario instance;

    public Mario(LevelScene world)
    {
        Mario.instance = this;
        this.world = world;
        keys = Scene.keys;
        x = 32;
        y = 0;

        facing = 1;
        setLarge(Mario.large, Mario.fire);
    }
    
    private boolean lastLarge;
    private boolean lastFire;
    private boolean newLarge;
    private boolean newFire;
    
    private void blink(boolean on)
    {
        Mario.large = on?newLarge:lastLarge;
        Mario.fire = on?newFire:lastFire;
        
        if (large)
        {
            sheet = Art.mario;
            if (fire)
                sheet = Art.fireMario;

            xPicO = 16;
            yPicO = 31;
            wPic = hPic = 32;
        }
        else
        {
            sheet = Art.smallMario;

            xPicO = 8;
            yPicO = 15;
            wPic = hPic = 16;
        }

        calcPic();
    }

    void setLarge(boolean large, boolean fire)
    {
        if (fire) large = true;
        if (!large) fire = false;
        
        lastLarge = Mario.large;
        lastFire = Mario.fire;
        
        Mario.large = large;
        Mario.fire = fire;

        newLarge = Mario.large;
        newFire = Mario.fire;
        
        blink(true);
    }

    public void move()
    {
        if (winTime > 0)
        {
            winTime++;

            xa = 0;
            ya = 0;
            return;
        }

        if (deathTime > 0)
        {
            deathTime++;
            if (deathTime < 11)
            {
                xa = 0;
                ya = 0;
            }
            else if (deathTime == 11)
            {
                ya = -15;
            }
            else
            {
                ya += 2;
            }
            x += xa;
            y += ya;
            
            if (stopMovementOnDeath) { return; }
        }

        if (powerUpTime != 0)
        {
            if (powerUpTime > 0)
            {
                powerUpTime--;
                blink(((powerUpTime / 3) & 1) == 0);
            }
            else
            {
                powerUpTime++;
                blink(((-powerUpTime / 3) & 1) == 0);
            }

            if (powerUpTime == 0) world.paused = false;

            calcPic();
            return;
        }

        if (invulnerableTime > 0) invulnerableTime--;
        visible = ((invulnerableTime / 2) & 1) == 0;

        if (!wasOnGround && onGround) { MarioComponent.insertFact(new Landing(this)); }
        
        wasOnGround = onGround;
        float sideWaysSpeed = keys[KEY_SPEED] ? 1.2f : 0.6f;
        //        float sideWaysSpeed = onGround ? 2.5f : 1.2f;

        if (onGround)
        {
            ducking = keys[KEY_DOWN] && large;
        }

        if (xa > 2)
        {
            facing = 1;
        }
        if (xa < -2)
        {
            facing = -1;
        }

        if (keys[KEY_JUMP] || (jumpTime < 0 && !onGround && !sliding))
        {
            if (jumpTime < 0)
            {
                xa = xJumpSpeed;
                ya = -jumpTime * yJumpSpeed;
                jumpTime++;
            }
            else if (onGround && mayJump)
            {
                world.sound.play(Art.samples[Art.SAMPLE_MARIO_JUMP], this, 1, 1, 1);
                xJumpSpeed = 0;
                yJumpSpeed = -1.9f;
                jumpTime = maxJumpTime;
                ya = jumpTime * yJumpSpeed;
                onGround = false;
                sliding = false;
                MarioComponent.insertFact(new Jump(this, jumpTime));                
            }
            else if (sliding && mayJump)
            {
                world.sound.play(Art.samples[Art.SAMPLE_MARIO_JUMP], this, 1, 1, 1);
                xJumpSpeed = -facing * 6.0f;
                yJumpSpeed = -2.0f;
                jumpTime = -6;
                xa = xJumpSpeed;
                ya = -jumpTime * yJumpSpeed;
                onGround = false;
                sliding = false;
                facing = -facing;
                MarioComponent.insertFact(new Jump(this, jumpTime));                
            }
            else if (jumpTime > 0)
            {
                xa += xJumpSpeed;
                ya = jumpTime * yJumpSpeed;
                jumpTime--;
            }
        }
        else
        {
            jumpTime = 0;
        }

        if (keys[KEY_LEFT] && !ducking)
        {
            if (facing == 1) sliding = false;
            xa -= sideWaysSpeed;
            if (jumpTime >= 0) facing = -1;
        }

        if (keys[KEY_RIGHT] && !ducking)
        {
            if (facing == -1) sliding = false;
            xa += sideWaysSpeed;
            if (jumpTime >= 0) facing = 1;
        }

        if ((!keys[KEY_LEFT] && !keys[KEY_RIGHT]) || ducking || ya < 0 || onGround)
        {
            sliding = false;
        }
        
        if (keys[KEY_SPEED] && canShoot && Mario.fire && world.fireballsOnScreen<2)
        {
            world.sound.play(Art.samples[Art.SAMPLE_MARIO_FIREBALL], this, 1, 1, 1);
            world.addSprite(new Fireball(world, x+facing*6, y-20, facing));
        }
        
        canShoot = !keys[KEY_SPEED];

        mayJump = (onGround || sliding) && !keys[KEY_JUMP];

        xFlipPic = facing == -1;

        runTime += (Math.abs(xa)) + 5;
        if (Math.abs(xa) < 0.5f)
        {
            runTime = 0;
            xa = 0;
        }

        calcPic();

        if (sliding)
        {
            for (int i = 0; i < 1; i++)
            {
                world.addSprite(new Sparkle((int) (x + Math.random() * 4 - 2) + facing * 8, (int) (y + Math.random() * 4) - 24, (float) (Math.random() * 2 - 1), (float) Math.random() * 1, 0, 1, 5));
            }
            ya *= 0.5f;
        }

        onGround = false;
        move(xa, 0);
        move(0, ya);

        if (y > world.level.height * 16 + 16 && dieOnFall)
        {
            die();
        }

        if (x < 0)
        {
            x = 0;
            xa = 0;
        }

        if (x > world.level.xExit * 16)
        {
            win();
        }

        if (x > world.level.width * 16)
        {
            x = world.level.width * 16;
            xa = 0;
        }

        ya *= 0.85f;
        if (onGround)
        {
            xa *= GROUND_INERTIA;
        }
        else
        {
            xa *= AIR_INERTIA;
        }

        if (!onGround)
        {
            ya += 3;
        }

        if (carried != null)
        {
            carried.x = x + facing * 8;
            carried.y = y - 2;
            if (!keys[KEY_SPEED])
            {
                carried.release(this);
                carried = null;
            }
        }
    }

    private void calcPic()
    {
        int runFrame = 0;

        if (large)
        {
            runFrame = ((int) (runTime / 20)) % 4;
            if (runFrame == 3) runFrame = 1;
            if (carried == null && Math.abs(xa) > 10) runFrame += 3;
            if (carried != null) runFrame += 10;
            if (!onGround)
            {
                if (carried != null) runFrame = 12;
                else if (Math.abs(xa) > 10) runFrame = 7;
                else runFrame = 6;
            }
        }
        else
        {
            runFrame = ((int) (runTime / 20)) % 2;
            if (carried == null && Math.abs(xa) > 10) runFrame += 2;
            if (carried != null) runFrame += 8;
            if (!onGround)
            {
                if (carried != null) runFrame = 9;
                else if (Math.abs(xa) > 10) runFrame = 5;
                else runFrame = 4;
            }
        }

        if (onGround && ((facing == -1 && xa > 0) || (facing == 1 && xa < 0)))
        {
            if (xa > 1 || xa < -1) runFrame = large ? 9 : 7;

            if (xa > 3 || xa < -3)
            {
                for (int i = 0; i < 3; i++)
                {
                    world.addSprite(new Sparkle((int) (x + Math.random() * 8 - 4), (int) (y + Math.random() * 4), (float) (Math.random() * 2 - 1), (float) Math.random() * -1, 0, 1, 5));
                }
            }
        }

        if (large)
        {
            if (ducking) runFrame = 14;
            height = ducking ? 12 : 24;
        }
        else
        {
            height = 12;
        }

        xPic = runFrame;
    }

    private boolean move(float xa, float ya)
    {
        while (xa > 8)
        {
            if (!move(8, 0)) return false;
            xa -= 8;
        }
        while (xa < -8)
        {
            if (!move(-8, 0)) return false;
            xa += 8;
        }
        while (ya > 8)
        {
            if (!move(0, 8)) return false;
            ya -= 8;
        }
        while (ya < -8)
        {
            if (!move(0, -8)) return false;
            ya += 8;
        }

        boolean collide = false;
        if (ya > 0)
        {
            if (isBlocking(x + xa - width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, 0)) collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, xa, ya)) collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, xa, ya)) collide = true;
        }
        if (ya < 0)
        {
            if (isBlocking(x + xa, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
        }
        if (xa > 0)
        {
            sliding = true;
            if (isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
            else sliding = false;
            if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya)) collide = true;
            else sliding = false;
            if (isBlocking(x + xa + width, y + ya, xa, ya)) collide = true;
            else sliding = false;
        }
        if (xa < 0)
        {
            sliding = true;
            if (isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
            else sliding = false;
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya)) collide = true;
            else sliding = false;
            if (isBlocking(x + xa - width, y + ya, xa, ya)) collide = true;
            else sliding = false;
        }

        if (collide)
        {
            if (xa < 0)
            {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            if (xa > 0)
            {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            if (ya < 0)
            {
                y = (int) ((y - height) / 16) * 16 + height;
                jumpTime = 0;
                this.ya = 0;
            }
            if (ya > 0)
            {
                y = (int) ((y - 1) / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        }
        else
        {
            x += xa;
            y += ya;
            return true;
        }
    }

    private boolean isBlocking(float _x, float _y, float xa, float ya)
    {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) return false;

        boolean blocking = world.level.isBlocking(x, y, xa, ya);

        byte block = world.level.getBlock(x, y);

        if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0)
        {
            this.getCoin();
            world.sound.play(Art.samples[Art.SAMPLE_GET_COIN], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
            world.level.setBlock(x, y, (byte) 0);
            for (int xx = 0; xx < 2; xx++)
                for (int yy = 0; yy < 2; yy++)
                    world.addSprite(new Sparkle(x * 16 + xx * 8 + (int) (Math.random() * 8), y * 16 + yy * 8 + (int) (Math.random() * 8), 0, 0, 0, 2, 5));
        }

        if (blocking && ya < 0)
        {
            world.bump(x, y, large);
        }

        return blocking;
    }

    public void stomp(Enemy enemy)
    {
        if (deathTime > 0 || world.paused) return;

        float targetY = enemy.y - enemy.height / 2;
        move(0, targetY - y);

        world.sound.play(Art.samples[Art.SAMPLE_MARIO_KICK], this, 1, 1, 1);
        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        sliding = false;
        invulnerableTime = 1;
    }

    public void stomp(Shell shell)
    {
        if (deathTime > 0 || world.paused) return;

        if (keys[KEY_SPEED] && shell.facing == 0)
        {
            carried = shell;
            shell.carried = true;
        }
        else
        {
            float targetY = shell.y - shell.height / 2;
            move(0, targetY - y);

            world.sound.play(Art.samples[Art.SAMPLE_MARIO_KICK], this, 1, 1, 1);
            xJumpSpeed = 0;
            yJumpSpeed = -1.9f;
            jumpTime = 8;
            ya = jumpTime * yJumpSpeed;
            onGround = false;
            sliding = false;
            invulnerableTime = 1;
        }
    }

    public void getHurt()
    {
        if (isInvincible) return;
        if (deathTime > 0 || world.paused) return;
        if (invulnerableTime > 0) return;

        if (large)
        {
            world.paused = true;
            powerUpTime = -3 * 6;
            world.sound.play(Art.samples[Art.SAMPLE_MARIO_POWER_DOWN], this, 1, 1, 1);
            if (fire)
            {
                world.mario.setLarge(true, false);
            }
            else
            {
                world.mario.setLarge(false, false);
            }
            invulnerableTime = 32;
        }
        else
        {
            die();
        }
    }

    private void win()
    {
        xDeathPos = (int) x;
        yDeathPos = (int) y;
        world.paused = true;
        winTime = 1;
        Art.stopMusic();
        world.sound.play(Art.samples[Art.SAMPLE_LEVEL_EXIT], this, 1, 1, 1);
    }

    public void die()
    {
        xDeathPos = (int) x;
        yDeathPos = (int) y;
        world.paused = true;
        deathTime = 1;
        Art.stopMusic();
        world.sound.play(Art.samples[Art.SAMPLE_MARIO_DEATH], this, 1, 1, 1);
        MarioComponent.insertFact(new Death(this));
    }


    public void getFlower()
    {
        if (deathTime > 0 || world.paused) return;

        if (!fire)
        {
            world.paused = true;
            powerUpTime = 3 * 6;
            world.sound.play(Art.samples[Art.SAMPLE_MARIO_POWER_UP], this, 1, 1, 1);
            world.mario.setLarge(true, true);
        }
        else
        {
            this.getCoin();
            world.sound.play(Art.samples[Art.SAMPLE_GET_COIN], this, 1, 1, 1);
        }
    }

    public void getMushroom()
    {
        if (deathTime > 0 || world.paused) return;
                
        if (!large)
        {
            world.paused = true;
            powerUpTime = 3 * 6;
            world.sound.play(Art.samples[Art.SAMPLE_MARIO_POWER_UP], this, 1, 1, 1);
            world.mario.setLarge(true, false);
        }
        else
        {
            this.getCoin();
            world.sound.play(Art.samples[Art.SAMPLE_GET_COIN], this, 1, 1, 1);
        }
    }

    public void kick(Shell shell)
    {
        if (deathTime > 0 || world.paused) return;

        if (keys[KEY_SPEED])
        {
            carried = shell;
            shell.carried = true;
        }
        else
        {
            world.sound.play(Art.samples[Art.SAMPLE_MARIO_KICK], this, 1, 1, 1);
            invulnerableTime = 1;
        }
    }

    public void stomp(BulletBill bill)
    {
        if (deathTime > 0 || world.paused) return;

        float targetY = bill.y - bill.height / 2;
        move(0, targetY - y);

        world.sound.play(Art.samples[Art.SAMPLE_MARIO_KICK], this, 1, 1, 1);
        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        sliding = false;
        invulnerableTime = 1;
    }

    public byte getKeyMask()
    {
        int mask = 0;
        for (int i = 0; i < 7; i++)
        {
            if (keys[i]) mask |= (1 << i);
        }
        return (byte) mask;
    }

    public void setKeys(byte mask)
    {
        for (int i = 0; i < 7; i++)
        {
            keys[i] = (mask & (1 << i)) > 0;
        }
    }

    public static void get1Up()
    {
        instance.world.sound.play(Art.samples[Art.SAMPLE_MARIO_1UP], instance, 1, 1, 1);
        lives++;
        if (lives==99)
        {
            lives = 99;
        }
    }
    
    public void getCoin()
    {
    	int oldCoin = coins;
        coins = coins + coinValue;
        
        MarioComponent.insertFact(new ValueChange(this, ValueChange.COIN_CHANGE, 
				oldCoin, coins));
        if (coins==100)
        {
            coins = 0;
            get1Up();
        }
    }
    
    // TODO: Generated by me
    public boolean isDucking() {
		return ducking;
	}
    
    // TODO: Generated by me
    public int getJumpTime() {
		return jumpTime;
	}

	public void setJumpTime(int jumpTime) {
		this.jumpTime = jumpTime;
	}

	public float getYJumpSpeed() {
		return yJumpSpeed;
	}

	public void setYJumpSpeed(float yJumpSpeed) {
		this.yJumpSpeed = yJumpSpeed;
	}
	
	public void setXJumpSpeed(float xJumpSpeed) {
		this.xJumpSpeed = xJumpSpeed;
	}

	public int getFacing() {
		return facing;
	}

	public LevelScene getWorld() {
		return world;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}

	public int getDeathTime() {
		return deathTime;
	}

	public static boolean isLarge() {
		return large;
	}

	public static boolean isFire() {
		return fire;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public boolean isSliding() {
		return sliding;
	}

	public void setSliding(boolean sliding) {
		this.sliding = sliding;
	}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isInvincible() {
        return this.isInvincible;
    }

    public void setInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }
}
