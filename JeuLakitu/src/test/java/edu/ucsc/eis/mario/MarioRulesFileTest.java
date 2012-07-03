package edu.ucsc.eis.mario;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import edu.ucsc.eis.mario.events.*;
import org.drools.runtime.rule.FactHandle;
import org.junit.*;
import static org.mockito.Mockito.*;

import edu.ucsc.eis.mario.level.Pit;
import edu.ucsc.eis.mario.sprites.BulletBill;
import edu.ucsc.eis.mario.sprites.Mario;

/**
 * Tests not valid for Infinite Mario:
 * - Script in invalid state: No script functions
 * - Invalid damage over time (Mario kills all enemies with one shot)
 * - Invalid resource accumulation over time (how many coins can Mario get a second?) 
 * @author cflewis
 *
 */
public class MarioRulesFileTest extends MarioRulesTest {
    private static final int CORRECT_JUMP_TIME = 7;
	
	// Simple Test for Required Action Not Possible
	@Test
	public void testPitDetection() {
		// Create a pit by hand
		for (int x = 20; x < 30; x++) {
			for (int y = 0; y < scene.getLevel().height; y++) {
				scene.level.setBlock(x, y, (byte) 0);
			}
		}
		
		scene.level.pits = new ArrayList<Pit>();
		scene.level.pits.add(new Pit(20, 29, false));
		
		ksession.insert(new LevelGenerated(scene.mario, scene.level));
		assertFired("pitTooLong");
		//assertFalse(scene.level.getBlock(29, scene.level.height - 1) == (byte) 0);
	}
	
	/**
	 * Test for invalid position over time: Mario can't jump that high from
	 * the ground, but of course he could be that high position-wise.
	 * 
	 * Does this count if I'm not doing a position check?
	 */
    @Test
    public void testBrokenEventJumpHeight() {
        FactHandle jumpEvent = ksession.insert(new Jump(mario, 50));
        tickScene(1);
        assertFired("jumpEventFound");
        assertFired("marioJumpTooHigh");

        // When Mario lands, we can retract this fact to show that he landed
        ksession.retract(jumpEvent);
    }

	
	/**
	 * Test for position invalid
	 */
	@Test
	public void testEscapeYBoundary() {
        mario.y = -71f;
        assertTrue(mario.getY() == -71f);
        ksession.insert(new LevelGenerated(mario, scene.level));
		ksession.insert(new MarioPosition(mario));
		assertTrue(mario.deathTime == 0);
		assertFired("marioOutOfBounds");
	}
	
	@Test
	public void testEscapeYBoundaryWithDeath() {
        mario.y = -71f;
        assertTrue(mario.getY() == -71f);
        ksession.insert(new LevelGenerated(mario, scene.level));
		ksession.insert(new MarioPosition(mario));
        ksession.insert(new Death(mario));
		assertNotFired("marioOutOfBounds");
	}
	
	@Test
	public void testEscapeXBoundary() {
        mario.x = -21f;
        assertTrue(mario.getX() == -21f);
        ksession.insert(new LevelGenerated(mario, scene.level));
		ksession.insert(new MarioPosition(mario));
		assertTrue(mario.deathTime == 0);
		assertFired("marioOutOfBounds");
	}
	
	@Test
	public void testEventJump() {
		FactHandle jumpEvent = ksession.insert(new Jump(mario, CORRECT_JUMP_TIME));
		tickScene(1);
		assertFired("jumpEventFound");
		
		FactHandle landingEvent = ksession.insert(new Landing(mario));
		tickScene(1);
		
		assertNotFired("marioJumpTooLong");
		// When Mario lands, we can retract this fact to show that he landed
		ksession.retract(jumpEvent);
		ksession.retract(landingEvent);
	}
	
	// Test for invalid position over time: Mario can't jump too long
	@Test
	public void testBrokenEventJump() {
		FactHandle jumpEvent = ksession.insert(new Jump(mario, CORRECT_JUMP_TIME));
		// Cause Mario to be able to jump for *ages*
		for (int i = 0; i < 100; i++) {
			mario.setJumpTime(7);
			tickScene(1);
		}

		assertFired("marioJumpTooLong");
		ksession.retract(jumpEvent);
	}
	
	// Tests for invalid animation context
	@Ignore
	public void testBrokenSmallAnimationSheet() {
		Mario.large = false;
		Mario.fire = false;
		assertFalse(Mario.large);
		assertFalse(Mario.fire);
		mario.sheet = Art.fireMario;
		assertTrue(mario.sheet == Art.fireMario);
		assertFired("marioAnimationSmall");
		assertTrue(mario.sheet == Art.smallMario);
	}
	
	@Ignore
	public void testBrokenLargeAnimationSheet() {
		Mario.large = true;
		Mario.fire = false;
		assertTrue(Mario.large);
		assertFalse(Mario.fire);
		mario.sheet = Art.fireMario;
		assertTrue(mario.sheet == Art.fireMario);
		assertFired("marioAnimationLarge");
		assertTrue(mario.sheet == Art.mario);
	}
	
	@Ignore
	public void testBrokenFireAnimationSheet() {
		Mario.large = true;
		Mario.fire = true;
		mario.sheet = Art.smallMario;
		assertFired("marioAnimationFire");
		assertTrue(mario.sheet == Art.fireMario);
	}
	
	// Test for invalid event occurrence over time
	// This one uses events (jumping without landing)...
	@Test
	public void testValidDoubleJump() {
		// Mario can't double jump ie. jump without landing
		ksession.insert(new Jump(mario, CORRECT_JUMP_TIME));
		tickScene(5);
		mario.setJumpTime(5);
		mario.setSliding(true);
		ksession.insert(new Jump(mario, CORRECT_JUMP_TIME));
		assertNotFired("marioDoubleJump");
	}
	
	@Test
	public void testBrokenDoubleJump() {
		// Mario can't double jump ie. jump without landing
		ksession.insert(new Jump(mario, CORRECT_JUMP_TIME));
		tickScene(5);
		mario.setJumpTime(5);
		mario.setSliding(false);
		ksession.insert(new Jump(mario, CORRECT_JUMP_TIME));
		assertFired("marioDoubleJump");
	}
	
	// Test for invalid event occurrence over time
	// ...this one is purely temporal
	@Test
	public void testBulletBillFiring() {
		BulletBill bill = mock(BulletBill.class);
		when(bill.getWorld()).thenReturn(scene);
		ksession.insert(new BulletBillSpawn(bill, 1, getClockTime()));
		tickScene(1000);
		ksession.insert(new BulletBillSpawn(bill, 1, getClockTime()));
		assertNotFired("bulletBillSpawn");
		ksession.insert(new BulletBillSpawn(bill, 2, getClockTime()));
		ksession.insert(new BulletBillSpawn(bill, 3, getClockTime()));
		assertNotFired("bulletBillSpawn");
	}
	
	@Test
	public void testBrokenBulletBillFiring() {
		BulletBill bill = mock(BulletBill.class);
		when(bill.getWorld()).thenReturn(scene);
		ksession.insert(new BulletBillSpawn(bill, 1, getClockTime()));
		tickScene(10);
		ksession.insert(new BulletBillSpawn(bill, 1, getClockTime()));
		assertFired("bulletBillSpawn");
	}

	// Invalid value change
	@Test
	public void testCoinValue() {
		int oldCoin = Mario.coins;
		Mario.coins = Mario.coins + 2;
		ksession.insert(new ValueChange(mario, ValueChange.COIN_CHANGE, 
				oldCoin, Mario.coins));
		assertFired("coinValue");
	}

    @Test
    public void testSpriteIdIncrement() {
        BulletBill bill1 = new BulletBill(scene, 0, 0, 0);
        BulletBill bill2 = new BulletBill(scene, 50, 50, 0);
        System.err.println("Bill 1 is " + bill1.getId() + " Bill 2 is " + bill2.getId());
        assertTrue(bill1.getId() < bill2.getId());
    }
	
	/**
	 * Test for "Action when not allowed" bug.
	 * As this kills Mario, this should either be tested at the end,
	 * or I have to work out why the Mario instance variable
	 * isn't being set to the new Mario that is created once this one
	 * dies.
	 */
	@Test
	public void testDeathInteraction() {
		float oldX = mario.getX();
		mario.die();
        ksession.insert(new Death(mario));
		tickScene(1);
		mario.keys[Mario.KEY_RIGHT] = true;
		tickScene(1);
		assertTrue(oldX == mario.x);
		assertFired("stopMarioInteractionWhenDead");
        ksession.insert(new NewLife());
        tickScene(1);
        assertFired("retractDeath");
	}
}
