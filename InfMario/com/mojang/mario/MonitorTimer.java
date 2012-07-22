package com.mojang.mario;


import ca.uqam.info.logic.*;
import ca.uqam.info.runtime.*;

import java.util.Iterator;
import java.util.Vector;
import java.awt.Graphics;
import java.awt.Color;
import java.text.DecimalFormat;;



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
		   
		  formulas.add(LTLStringParser.parseFromString("G ([m /action/name] (!((m) = ({Jump}))))"));
		  formulas.add(LTLStringParser.parseFromString("G ([m /action/name] (!((m) = ({CollisionEnemy}))))"));
		  formulas.add(LTLStringParser.parseFromString("G ([x1 /action/name] (((x1) = ({HaveShell})) -> (X ([x2 /action/name] (!((x2) = ({CollisionEnemy})))))))"));
		  formulas.add(LTLStringParser.parseFromString("G ([m /action/jumpHeight] (!((m) < ({20}))))"));
		  formulas.add(LTLStringParser.parseFromString("F ([m /action/name] ((m) = ({Jump})))"));
		  
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
	   
	   public void getOutcomes(Graphics g)
	   {
		   debutTime = System.nanoTime();
		   
		   Iterator<SymbolicWatcher> itr = monitors.iterator();
		   SymbolicWatcher current;
		   int positionXmodif = 0;
		   
		   while(itr.hasNext())
		   {
			   current = itr.next();
			   switch(current.getOutcome())
			   {
			   case TRUE: g.setColor(Color.green); g.fillOval(10 + positionXmodif,220,10,10); g.setColor(Color.black); g.drawOval(10 + positionXmodif,220,10,10); break;
			   case FALSE: g.setColor(Color.red); g.fillOval(10 + positionXmodif,220,10,10); g.setColor(Color.black); g.drawOval(10 + positionXmodif,220,10,10); break;
			   case INCONCLUSIVE: g.setColor(Color.yellow); g.fillOval(10 + positionXmodif,220,10,10); g.setColor(Color.black); g.drawOval(10 + positionXmodif,220,10,10); break; 
			   }
			   positionXmodif += 12;
		   }
		   
		   finTime = System.nanoTime();
		   addTime(finTime - debutTime);
	   }
	   
	   
	   public void resetMonitors()
	   {
		   Iterator<SymbolicWatcher> itr = monitors.iterator();
		   
		   while(itr.hasNext())
		   {
			   itr.next().reset();
		   }
	   }
	   
	   public SymbolicWatcher getWatcher(){
		   return monitors.lastElement();
	   }
	   
	   public void showPourcentage(Graphics g)
	   {
		   float pourcentage = ((float)monitorTime/(float)executionTime)*(float)100;
		   
		   DecimalFormat dFormat = new DecimalFormat("#.##");
		   
		   String pour = dFormat.format((double)pourcentage);
		   System.out.print(pour+" ");
		   //System.out.print(pour);
		   
		   g.setColor(Color.red);
		   g.drawString(pour+"%", 270, 230);
	   }
	}