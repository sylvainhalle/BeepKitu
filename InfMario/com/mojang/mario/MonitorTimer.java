package com.mojang.mario;


import ca.uqam.info.logic.*;
import ca.uqam.info.runtime.*;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToolTipUI;



public class MonitorTimer {
	   private static MonitorTimer instance = null;
	   private static long monitorTime;
	   private static long executionTime;
	   private int numberEvents;
	   private long debutTime;
	   private long finTime;
	   
	   public static String fileName;
	   private FileWriter graphA;
	   private FileWriter graphB;
	   private BufferedWriter writerA;
	   private BufferedWriter writerB;
	   
	   private Vector<Operator> formulas;
	   private Vector<SymbolicWatcher> monitors;
	   private Vector<String> captions;
	   private Vector<String> timeList;
	   private Vector<String> eventList;
	   
	   public static enum Line {
		   FIRST, 
		   SECOND
	   };
	   
	   protected MonitorTimer() {
		   
			debutTime = System.nanoTime();
			
			int compteur = 1;
			
			timeList = new Vector<String>();
			eventList = new Vector<String>();
			formulas = new Vector<Operator>();
			monitors = new Vector<SymbolicWatcher>();
			captions = new Vector<String>();
			
			try {
				graphA = new FileWriter("numberEvents.csv");
			} catch (IOException e) {
				graphA = null;
				e.printStackTrace();
			}
			
			writerA = new BufferedWriter(graphA);

			try {
				graphB = new FileWriter("timeEvent.csv");
			} catch (IOException e) {
				graphB = null;
				e.printStackTrace();
			}
			
			writerB = new BufferedWriter(graphB);
			
			monitorTime = 0;
			fillFormulas();
			Iterator<Operator> itr = formulas.iterator();
			Iterator<String> itr2 = captions.iterator();
			
			FlowLayout flow = new FlowLayout();
			flow.setAlignment(FlowLayout.LEFT);
			JFrame frame2 = new JFrame("Règles");
			frame2.setPreferredSize(new Dimension(320,400));
			frame2.setLayout(flow);
			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			   
			JLabel label1 = new JLabel("Yo bitches");
			label1.setBounds(100,100,500,500);
					   
			while(itr.hasNext())
			{
			   monitors.add(new SymbolicWatcher(itr.next()));
			   monitors.lastElement().setCaption(itr2.next());
			   label1 = new JLabel(compteur + " - " + monitors.lastElement().getCaption());
			   frame2.getContentPane().add(label1);
			   frame2.validate();
			   compteur++;
			}
			
			frame2.pack();
			frame2.setVisible(true);
			
					   
			finTime = System.nanoTime();
			addTime(finTime - debutTime);
	   }
	   
	   private void fillFormulas()
	   {
		  debutTime = System.nanoTime();
		   
		  formulas.add(LTLStringParser.parseFromString("G ([m /action/name] (!((m) = ({Jump}))))"));
		  captions.add("Évènement \"Jump\" impossible");
		  formulas.add(LTLStringParser.parseFromString("G ([m /action/name] (!((m) = ({CollisionEnemy}))))"));
		  captions.add("\"CollisionEnemy\" impossible");
		  formulas.add(LTLStringParser.parseFromString("G ([x1 /action/name] (((x1) = ({HaveShell})) -> (X (G ([x2 /action/name] (!((x2) = ({CollisionEnemy}))))))))"));
		  captions.add("\"CollisionEnemy\" impossible si \"HaveShell\"");
		  formulas.add(LTLStringParser.parseFromString("G ([m /action/jumpHeight] (!((m) < ({20}))))"));
		  captions.add("\"Jump\" plus haut que l'écran impossible");
		  formulas.add(LTLStringParser.parseFromString("F ([m /action/name] ((m) = ({Jump})))"));
		  captions.add("Il faut avoir \"Jump\" au moins une fois");
		  formulas.add(LTLStringParser.parseFromString("G ([m /action/coinChange] ((m) = ({1})))"));
		  captions.add("On ne peut pas gagner plus d'un Coin à la fois");
		  formulas.add(LTLStringParser.parseFromString("G ([x1 /action/name] (((x1) = ({Stomp})) -> (X ([x2 /action/name] (!((x2) = ({Stomp})))))))"));
		  captions.add("Pas deux événements \"Stomp\" de suite");
		  formulas.add(LTLStringParser.parseFromString("G ([x1 /action/name] (((x1) = ({Crouch})) -> (X ([x2 /action/name] (!((x2) = ({Jump})))))))"));
		  captions.add("Si \"Crouch\", on ne peut pas avoir \"Jump\"");
	      formulas.add(LTLStringParser.parseFromString("G ([x1 /action/name] (((x1) = ({EnemyFireballDeath})) -> (X ([x2 /action/name] ((x2) = ({FireballDisappear}))))))))"));
	      captions.add("Une boule de feu doit disparaître après avoir touché un ennemi.");
	      formulas.add(LTLStringParser.parseFromString("G ([x1 /action/name] (((x1) = ({Stomp})) -> ([x3 /action/iswinged] (((x3) = ({true})) -> ([x2 /action/id] (X ([x5 /action/id] (((x2) = (x5)) -> ([x4 /action/name] (!((x4) = ({EnemyDead}))))))))))))"));
	      captions.add("Un ennemie sur lequel on \"Stomp\" et qui a des ailes ne peut mourir suite à cela.");
		  
		  //formulas.add(LTLStringParser.parseFromString(""));
		  
		  Iterator<Operator> itr = formulas.iterator();
		  
		  finTime = System.nanoTime();
		  addTime(finTime - debutTime);
	   }
	   
	   public Operator formulaaa()
	   {
		   OperatorEquals f1 = new OperatorEquals(new Atom("x2"), new Constant("ElephantUpgrade"));
	        OperatorNot f2 = new OperatorNot();
	        f2.setOperand(f1);

	        FOForAll fo = new FOForAll(LTLStringParser.parseFromString("[x2 /action/name] (!((x2) = ({Stomp})))"));
	        OperatorG f3 = new OperatorG();
	        f3.setOperand(fo);
	        OperatorX secondPart = new OperatorX();
	        secondPart.setOperand(f3);

	        //OperatorEquals f4 = new OperatorEquals(new Atom("x1"), new Constant("ElephantUpgrade"));
	        FOForAll fo2 = new FOForAll(LTLStringParser.parseFromString("[x1 /action/name] (((x1) = ({Stomp}))"));

	        OperatorImplies f5 = new OperatorImplies(fo2, secondPart);
	        f5.setOperands(fo2,secondPart);
	        
	        OperatorG formula = new OperatorG();
	        formula.setOperand(f5);
	        
	        return formula;
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
		   numberEvents++;
		   double timeinSec = (double)executionTime/1000000000.0;
		   try {
			writerA.write(numberEvents+","+timeinSec+"\n");
			writerA.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		   Iterator<SymbolicWatcher> itr = monitors.iterator();
		   
		   long timeGraphBd = System.nanoTime();
		   while(itr.hasNext())
		   {
			   SymbolicWatcher w = itr.next();
			   w.update(s);
			   w.getOutcome();
			   
		   }
		   long timeGraphBf = System.nanoTime();
		   long timeGraphB = timeGraphBf - timeGraphBd;
		   double timeMili = (double)timeGraphB/1000000.0;
		   try {
			writerB.write(numberEvents+","+timeMili+"\n");
			writerB.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		   addEvent(s);
		   
		   finTime = System.nanoTime();
		   addTime(finTime - debutTime);
	   }
	   
	   public void getOutcomes(Graphics g, MarioComponent m)
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

		   g.setColor(Color.red);
		   g.drawString(pour+"%", 270, 230);
	   }
	   
	   public void addEvent(String event) {
		   String str = "", s = "";
		   char[] eventC = event.toCharArray();
		   boolean isEnd = false, isAction = false;
		   
		   for (int i = 0; i < event.length(); i++) {
			   if (eventC[i] == '<') {
				   
				   if (isAction && s != "") {
					   str = str + s + " - ";
				   }
				   
				   s = "";
				   isEnd = false;
				   isAction = false;
			   }
			   
			   else if (eventC[i] == '>' && !isEnd) {
				   str = str + s + ": ";
				   s = "";
				   
				   isAction = true;
			   }
			   
			   else if (eventC[i] == '/') {
				   isEnd = true;
			   }
			   
			   else if (!isEnd) {
				   s = s + eventC[i];
			   }
		   }
		   
		   Calendar cal = Calendar.getInstance();
           SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

           if (!eventList.isEmpty()) {
               if (!eventList.lastElement().equals(str)) {
            	   timeList.add(sdf.format(cal.getTime()).toString());
                   eventList.add(str);
               }
           }

           else {
               timeList.add(sdf.format(cal.getTime()).toString());
               eventList.add(str);
           }
	   }
	   
	   public int size() {
			if (monitors == null) {
				return 0;
			}
			
			return monitors.size();
		}
	   
	   public String showEvent(Line line) {
		   switch (line) {
		   case FIRST: 
			   
			   if (eventList.size() > 0) {
				   return eventList.get(eventList.size() - 1);
			   }
			   
			   return "";
			   
		   case SECOND: 
			   
			   if (eventList.size() > 1) {
				   return eventList.get(eventList.size() - 2);
			   }
			   
			   return "";
			   
		   default: return "";
		   }
	   }
	   
	   public void printEvent() {
			FileWriter fstream;
			
			try {
				if (timeList.isEmpty()) {
	                return;
	            }
			}
			catch (NullPointerException n) {
                System.out.println("No event");
                return;
            }
			
			try {
				fstream = new FileWriter(fileName);
			}
			
			catch (IOException e) {
				System.out.println("File missing");
				return;
			}
			
			
			
			try {
				BufferedWriter out = new BufferedWriter(fstream);
				
				for (int i = 0; i < timeList.size(); i++) {
					out.write("[" + timeList.get(i) + "]" + eventList.get(i));
					out.newLine();
				}
				
				timeList.clear();
				eventList.clear();
				
				out.close();
			}
			
			catch (Exception e) {
				System.out.println("Error writing the events");
			}
		}
	}