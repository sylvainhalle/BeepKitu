package com.mojang.mario;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class FrameLauncher
{
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
    }
}