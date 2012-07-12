package com.mojang.mario;


import ca.uqam.info.logic.*;
import ca.uqam.info.runtime.*;



public class MonitorTimer {
	   private static MonitorTimer instance = null;
	   private static long monitorTime;
	   private static long executionTime;
	   
	   private SymbolicWatcher monitor;
	   
	   protected MonitorTimer() {
	      // Exists only to defeat instantiation.
		   monitor = new SymbolicWatcher();
		   
		   Operator o = LTLStringParser.parseFromString("G ([m1 /actions/action] != ({jump}) )");
		   monitor.setFormula(o);
		   monitorTime = 0;
		   
		   
	   }
	   public static MonitorTimer Instance() {
	      if(instance == null) {
	         instance = new MonitorTimer();
	      }

	      return instance;
	   }
	   
	   public void addTime(long time)
	   {
		  monitorTime += time;
		  
		  //System.out.print(monitorTime);
		  //System.out.println(" ");
	   }
	   
	   public void addExecTime(long time)
	   {
		  executionTime += time;
		  
		  //System.out.print(executionTime);
		  //System.out.println(" ");
	   }
	   
	   public SymbolicWatcher getWatcher()
	   {
		   return monitor;
	   }
	}