package com.mojang.mario;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import com.mojang.mario.Server;

public class FrameLauncher extends JFrame
{
	JPanel jPanel1 = new JPanel();
	  JScrollPane jScrollPane1 = new JScrollPane();
	  JTextArea jTextArea2 = new JTextArea();
	  static Integer listen_port = null;
	  
	  public FrameLauncher() {
		    try {
		      jbInit();
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		  }
	  
    public static void main(String[] args)
    {	
        MarioComponent mario = new MarioComponent(640, 480);
        JFrame frame = new JFrame("Mario Test");
        frame.setContentPane(mario);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.addWindowListener(new WindowAdapter() {
        	
        	public void windowClosing(WindowEvent we) {
        		MonitorTimer.Instance().printEvent();
        	}
        	
		});
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        mario.start();
//        frame.addKeyListener(mario);
//        frame.addFocusListener(mario);
        
        try {
            listen_port = new Integer(args[0]);
          }
          catch (Exception e) {
            listen_port = new Integer(80);
          }
        
        //FrameLauncher webserver = new FrameLauncher();
    }
    
    private void jbInit() throws Exception {
        //new Server(listen_port.intValue(), this);
    }
}