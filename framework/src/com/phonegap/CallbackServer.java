package com.phonegap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import android.util.Log;

/**
 * This class provides a way for Java to run JavaScript in the web page that has loaded PhoneGap.
 * The CallbackServer class implements an XHR server and a list of JavaScript statements
 * that are to be executed on the web page.
 * 
 * The process flow is:
 * 1. JavaScript makes an async XHR call. 
 * 2. The server holds the connection open until data is available. 
 * 3. The server writes the data to the client and closes the connection. 
 * 4. The server immediately starts listening for the next XHR call. 
 * 5. The client receives this XHR response, processes it.
 * 6. The client sends a new async XHR request.
 *
 */
public class CallbackServer implements Runnable {
	
  private static final String LOG_TAG = "PhoneGapCallbackServer";
	/**
	 * The list of JavaScript statements to be sent to JavaScript.
	 */
	private LinkedList<String> javascript;
	
	/**
	 * The port to listen on.
	 */
	private int port;
	
	/**
	 * The server thread.
	 */
	private Thread serverThread;
	
	/**
	 * Indicates the server is running.
	 */
	private boolean active;
	
	/**
	 * Indicates that the JavaScript statements list is empty
	 */
	private boolean empty;
	
	/**
	 * Constructor.
	 */
	public CallbackServer() {
		//System.out.println("CallbackServer()");
		this.active = false;
		this.empty = true;
		this.port = 0;
		this.javascript = new LinkedList<String>();
		this.startServer();
	}
	
	/**
	 * Get the port that this server is running on.
	 * 
	 * @return
	 */
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Start the server on a new thread.
	 */
	public void startServer() {
		Log.d(LOG_TAG,"CallbackServer.startServer()");
		this.active = false;
		
		// Start server on new thread
		this.serverThread = new Thread(this);
		this.serverThread.start();
	}

	/**
	 * Restart the server on a new thread.
	 */
	public void restartServer() {
		
		// Stop server
		this.stopServer();
				
		// Start server again
		this.startServer();
	}

	/**
	 * Start running the server.  
	 * This is called automatically when the server thread is started.
	 */
	public void run() {
		
		// Start server
		try {
			this.active = true;
			String request;
			ServerSocket waitSocket = new ServerSocket(0);
			this.port = waitSocket.getLocalPort();
			Log.d(LOG_TAG," -- using port " +this.port);

			 while (this.active) {
				 Log.d(LOG_TAG,"CallbackServer: Waiting for data on socket");
				 Socket connection = waitSocket.accept();
				 BufferedReader xhrReader = new BufferedReader(new InputStreamReader(connection.getInputStream()),40);
				 DataOutputStream output = new DataOutputStream(connection.getOutputStream());
				 request = xhrReader.readLine();
				 Log.d(LOG_TAG,"Request="+request);
				 if(request.contains("GET"))
				 {
					 Log.d(LOG_TAG," -- Processing GET request");
					 
					 // Wait until there is some data to send, or send empty data every 30 sec 
					 // to prevent XHR timeout on the client 
					 synchronized (this) { 
						 while (this.empty) { 
							 try { 
								 this.wait(30000); // prevent timeout from happening
								 Log.d(LOG_TAG,">>> break <<<");
								 break;
							 } 
							 catch (Exception e) { }
						 } 
					 }
					 
					 // If server is still running
					 if (this.active) {
					
						 // If no data, then send 404 back to client before it times out
						 if (this.empty) {
							 Log.d(LOG_TAG," -- sending data 0");
							 output.writeBytes("HTTP/1.1 404 NO DATA\r\n\r\n");
						 }
						 else {
							 Log.d(LOG_TAG," -- sending item");
							 output.writeBytes("HTTP/1.1 200 OK\r\n\r\n"+this.getJavascript());
						 }
					 }					 
				 }
				 Log.d(LOG_TAG,"CallbackServer: closing output");
				 output.close();				 
			 }
		 } catch (IOException e) {
		   Log.d(LOG_TAG,"CallbackServer IOException");
			 e.printStackTrace();
		 }
		 this.active = false;
		 Log.d(LOG_TAG,"CallbackServer.startServer() - EXIT");
	}
		
	/**
	 * Stop server.  
	 * This stops the thread that the server is running on.
	 */
	public void stopServer() {
		Log.d(LOG_TAG,"CallbackServer.stopServer()");
		this.active = false;

		// Break out of server wait
		synchronized (this) { 
			this.notify();
		}		
	}

    /**
     * Destroy
     */
    public void destroy() {
    	this.stopServer();
    }

	/**
	 * Get the number of JavaScript statements.
	 * 
	 * @return int
	 */
	public int getSize() {
		int size = this.javascript.size();
		Log.d(LOG_TAG, "getSize() = " + size);
		return size;
	}
	
	/**
	 * Get the next JavaScript statement and remove from list.
	 *  
	 * @return String
	 */
	public String getJavascript() {
		if (this.javascript.size() == 0) {
			return null;
		}
		String statement = this.javascript.remove(0);
		Log.d(LOG_TAG, "CallbackServer.getJavascript() = " + statement);
		if (this.javascript.size() == 0) {
			synchronized (this) { 
				this.empty = true;
			}
		}
		return statement;
	}
	
	/**
	 * Add a JavaScript statement to the list.
	 * 
	 * @param statement
	 */
	public void sendJavascript(String statement) {
		Log.d(LOG_TAG, "CallbackServer.sendJavascript("+statement+")");
		this.javascript.add(statement);
		synchronized (this) { 
			this.empty = false;
			this.notify();
		}
	}
	
}
