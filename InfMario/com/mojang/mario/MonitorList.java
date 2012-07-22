package com.mojang.mario;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import ca.uqam.info.runtime.SymbolicWatcher;

public class MonitorList {
	private MonitorList()	{}
	
	private static Hashtable<String, SymbolicWatcher> eventList;
	
	public static String fileName;
	
	public static SymbolicWatcher getEvent(int index) {
		if (eventList == null) {
			eventList = new Hashtable<String, SymbolicWatcher>();
		}
		
		if (eventList.size() > index) {
			Set<String> set = eventList.keySet();
			Iterator<String> it = set.iterator();
			String key;
			
			do {
				key = it.next();
				index--;
			}
			while (it.hasNext() && index > 0);
			
			return eventList.get(key);
		}
		
		return null;
	}
	
	public static void addToList(SymbolicWatcher msg) {
		if (eventList == null) {
			eventList = new Hashtable<String, SymbolicWatcher>();
		}
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		eventList.put(sdf.format(cal.getTime()).toString(), msg);
	}
	
	public static int size() {
		if (eventList == null) {
			return 0;
		}
		
		return eventList.size();
	}
	
	public static void printEvent() {
		FileWriter fstream;
		
		try {
			fstream = new FileWriter(fileName);
		}
		
		catch (IOException e) {
			System.out.println("File missing");
			return;
		}
		
		try {
			BufferedWriter out = new BufferedWriter(fstream);
			
			Set<String> set = eventList.keySet();
			Iterator<String> it = set.iterator();
			String key;
			
			while (it.hasNext()) {
				key = it.next();
				
				out.write("[" + key + "] " + eventList.get(key).getFormula().toString() + " " + eventList.get(key).getOutcome().toString());
				out.newLine();
			}
			
			out.close();
		}
		
		catch (Exception e) {
			System.out.println("Error writing the events");
		}
	}
}
