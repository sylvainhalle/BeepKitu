package com.mojang.mario;


import ca.uqam.info.logic.*;
import ca.uqam.info.runtime.*;

import java.util.Iterator;
import java.util.Vector;



public class MonitorTimer {
	   private static MonitorTimer instance = null;
	   private static long monitorTime;
	   private static long executionTime;
	   private long debutTime;
	   private long finTime;
	   
	   private Vector<Operator> formulas;
	   private Vector<SymbolicWatcher> monitors;
	   
	   protected MonitorTimer() {
		   
			debutTime = System.nanoTime();
			
			formulas = new Vector<Operator>();
			monitors = new Vector<SymbolicWatcher>();
			
			monitorTime = 0;
			fillFormulas();
			Iterator<Operator> itr = formulas.iterator();
					   
			while(itr.hasNext())
			{
			   monitors.add(new SymbolicWatcher(itr.next()));
			}
					   
			finTime = System.nanoTime();
			addTime(finTime - debutTime);
	   }
	   
	   private void fillFormulas()
	   {
		  debutTime = System.nanoTime();
		   
		  formulas.add(LTLStringParser.parseFromString("G ([m /action] (!((m) = ({Jump}))))"));
		  //formulas.add(LTLStringParser.parseFromString("G ([m /action] (!((m) = ({CollisionEnemy}))))"));
		  formulas.add(LTLStringParser.parseFromString("G ([x1 /action] (((x1) = ({CatchShell})) -> (X ([x2 /action] (!((x2) = ({CollisionEnemy})))))))"));
		  
		  Iterator<Operator> itr = formulas.iterator();
		  
		  finTime = System.nanoTime();
		  addTime(finTime - debutTime);
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
	   
	   public void updateWatchers(String s)
	   {
		   debutTime = System.nanoTime();
		   
		   Iterator<SymbolicWatcher> itr = monitors.iterator();
		   
		   while(itr.hasNext())
		   {
			   itr.next().update(s);
		   }
		   
		   
		   finTime = System.nanoTime();
		   addTime(finTime - debutTime);
	   }
	   
	   public void getOutcomes()
	   {
		   Iterator<SymbolicWatcher> itr = monitors.iterator();
		   SymbolicWatcher current;
		   
		   while(itr.hasNext())
		   {
			   current = itr.next();
			   if (current.getOutcome() == LTLFOWatcher.Outcome.FALSE)
			   {
				   System.out.print(current.getOutcome().toString());
				   System.out.println();
			   }
		   }
	   }
	   
	   public void resetMonitors()
	   {
		   Iterator<SymbolicWatcher> itr = monitors.iterator();
		   
		   while(itr.hasNext())
		   {
			   itr.next().reset();
		   }
	   }
	}