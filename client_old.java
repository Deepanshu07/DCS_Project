/*************************************************************************************
File: client.java
Authors: Deepanshu Gupta and Deepanshu Sapra
Description: Client file which sends requests to the library server
Last Modified: 31 May 2016
**************************************************************************************/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
 

class ThreadClass extends Thread
{
    private Thread t;
       private String clientNum;
       private Socket socket;
       
       ThreadClass(String name){
           clientNum = name;
           System.out.println("Creating " +  clientNum );
       }
       public void run() 
       {
       		try
        	{
	            //String host = "10.8.19.95";
	            String host = "localhost";
	            int port = 25000;
	            InetAddress address = InetAddress.getByName(host);
	            socket = new Socket(address, port);
	 
	            //Send the message to the server
	            OutputStream os = socket.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            BufferedWriter bw = new BufferedWriter(osw);
      	 			String query;
      	 			if(clientNum.equals("1"))
      	 			{
	            	query = "issue electronics 2";
	            }
	            else
	            {
	            	query = "return electronics 3";
	            }
	            	                           //generate random queries
	 
	            String sendMessage = query + "\n";
	            bw.write(sendMessage);
	            bw.flush();
	            System.out.println("Message sent to the server : "+sendMessage);
	 
	            //Get the return message from the server
	            InputStream is = socket.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String message = br.readLine();
	            System.out.println("Message received from the server : "+message);
        	}
        	catch (Exception exception)
        	{
            	exception.printStackTrace();
        	}
        	finally
        	{
            	//Closing the socket
            	try
            	{
                	socket.close();
            	}
            	catch(Exception e)
            	{
                	e.printStackTrace();
            	}
        	}
	          /*System.out.println("Running " +  clientNum );
	          try {
	             for(int i = 4; i > 0; i--) {
	                System.out.println("Thread: " + clientNum + ", " + i);
	                // Let the thread sleep for a while.
	                Thread.sleep(50);
	             }
	         } catch (InterruptedException e) {
	             System.out.println("Thread " +  clientNum + " interrupted.");
	         }
	         System.out.println("Thread " +  clientNum + " exiting.");*/
       }
       
       public void start ()
       {
          System.out.println("Starting " +  clientNum );
          if (t == null)
          {
             t = new Thread (this, clientNum);
             t.start ();
          }
       }
}


public class client_old
{

 
    public static void main(String args[])
    {
    	  ThreadClass[] clients=new ThreadClass[3];
     	  for (int i=1;i<=2 ;i++ ) 
     	  {
     	  		clients[i] = new ThreadClass(Integer.toString(i));
      			clients[i].start();	
     	  }
    }
}