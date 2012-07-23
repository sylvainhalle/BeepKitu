package com.mojang.mario;

import java.util.Vector;
import ca.uqam.info.logic.*;
import ca.uqam.info.runtime.*;

public class WatchedFormula {

	private SymbolicWatcher monitor;
	
	public WatchedFormula()
	{
		monitor = new SymbolicWatcher();
	}
	
	public SymbolicWatcher getWatcher()
	{
		return monitor;
	}

}
