package com.mojang.mario;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	
	//public Server(int listenPort, FrameLauncher sendMessageTo) {
	public Server(int listenPort) {
		port = listenPort;
	    this.start();
	}
	
	private int port;

	@SuppressWarnings({ "resource" })
	public void run() {
		ServerSocket serversocket = null;
    
		try {
			serversocket = new ServerSocket(port);
		}
    
		catch (Exception e) {
			return;
		}
    
		while (true) {
			try { 
			    Socket connectionsocket = serversocket.accept();
			    InetAddress client = connectionsocket.getInetAddress();
			    
			    BufferedReader input = new BufferedReader(new InputStreamReader(connectionsocket.getInputStream()));
			    DataOutputStream output = new DataOutputStream(connectionsocket.getOutputStream());
			
			    httpHandler(input, output);
			}
			  
			catch (Exception e) {
				System.out.println("Error connect: " + e.toString());
			}
		}
	}

	private void httpHandler(BufferedReader input, DataOutputStream output) {
		int method = 0;
	    String http = new String();
	    String path = new String();
	    String file = new String();
	    String user_agent = new String();
    
	    try {
	    	String tmp = input.readLine();
	    	String tmp2 = new String(tmp);
      
	    	tmp.toUpperCase();
      
	    	if (tmp.startsWith("GET"))
	    		method = 1;
  
	    	if (tmp.startsWith("HEAD"))
	    		method = 2;

	    	if (method == 0) {
	    		try {
	    			output.writeBytes(constructHttpHeader(501, 0));
	    			output.close();
	    			
	    			return;
	    		}
	    		
	    		catch (Exception e3) {
	    			System.out.println("Error connect: " + e3.toString());
	    		}
	    	}
  
	    	int start = 0;
	    	int end = 0;
  
	    	for (int a = 0; a < tmp2.length(); a++) {
	    		if (tmp2.charAt(a) == ' ' && start != 0) {
	    			end = a;
	    			break;
	    		}

	    		if (tmp2.charAt(a) == ' ' && start == 0)
	    			start = a;
	    	}
  
	    	path = tmp2.substring(start + 2, end);
	    }

	    catch (Exception e) {
	    	System.out.println("Error connect: " + e.toString());
	    }

	    FileInputStream requestedfile = null;

	    try {
	    	requestedfile = new FileInputStream(path);
	    }

	    catch (Exception e) {
	    	try {
	    		output.writeBytes(constructHttpHeader(404, 0));
	    		output.close();
	    	}
	    	
	    	catch (Exception e2) {
	    		System.out.println("Error connect: " + e2.toString());
	    	}
	    }

	    try {
	    	int type_is = 0;
  
	    	if (path.endsWith(".zip"))
	    		type_is = 3;
  
	    	if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
	    		type_is = 1;
  
	    	if (path.endsWith(".gif"))
	    		type_is = 2;
  
	    	output.writeBytes(constructHttpHeader(200, 5));

	    	if (method == 1) {
	    		while (true) {
	    			int b = requestedfile.read();
	    			
	    			if (b == -1)
	    				break;
      
	    			output.write(b);
	    		}
	    	}
  
	    	output.close();
	    	requestedfile.close();
	    }

	    catch (Exception e) {
	    	System.out.println("Error connect: " + e.toString());
	    }
	}

	private String constructHttpHeader(int returnCode, int fileType) {
		String s = "HTTP/1.0 ";

		switch (returnCode) {
		case 200: s = s + "200 OK";
			break;
    
		case 400: s = s + "400 Bad Request";
			break;
    
		case 403: s = s + "403 Forbidden";
			break;
    
		case 404: s = s + "404 Not Found";
			break;
    
		case 500: s = s + "500 Internal Server Error";
			break;
    
		case 501: s = s + "501 Not Implemented";
			break;
    
		default: break;
		}

		s = s + "\r\n";
		s = s + "Connection: close\r\n";
		s = s + "Server: SimpleHTTPtutorial v0\r\n";

		switch (fileType) {
		case 0:
			break;
    
		case 1: s = s + "Content-Type: image/jpeg\r\n";
			break;
    
		case 2: s = s + "Content-Type: image/gif\r\n";
		case 3: s = s + "Content-Type: application/x-zip-compressed\r\n";
		default: s = s + "Content-Type: text/html\r\n";
			break;
		}

		s = s + "\r\n";
	    
	    return s;
	  }
	}
