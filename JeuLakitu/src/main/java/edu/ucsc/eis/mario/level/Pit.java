package edu.ucsc.eis.mario.level;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class Pit implements Serializable {
	private int startX;
	private int endX;
	public boolean hasStairs;

	public Pit(int startX, int endX, boolean hasStairs) {
		this.startX = startX;
		this.endX = endX;
		this.hasStairs = hasStairs;
	}
	
	public int getLength() {
		/**
		 * Need to +1 as it's inclusive: eg. 162 - 160 = 2, but there 
		 * are *3* spaces: 160, 161 and 162.
		 */
		return (endX - startX) + 1;
	}

	public int getStartX() {
		return startX;
	}

	public int getEndX() {
		return endX;
	}
	
	// Stupid accessor for Drools
	public boolean isHasStairs() {
		return hasStairs;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("Start X", startX).
			append("End X", endX).
			append("Has Stairs", hasStairs).
			toString();
	}
}
