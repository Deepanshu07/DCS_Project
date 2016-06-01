/*************************************************************************************
File: client.java
Authors: Deepanshu Gupta and Deepanshu Sapra
Description: Client file which sends requests to the library server
Last Modified: 1 June 2016
**************************************************************************************/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
 

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
	 			String[] command = new String[]{"issue ","return ","add ","show "};
	 			String[] bookname = new String[]{"touwm","awfub","yroei","mydxo","rioap","ydhna","ajdky","bxqks","kdose","ayjgd","lfhwm","bgloj","pbocf","loitp","zykfp","iweaa","nbtjj","nurde","dqldn","uduzc","dcjbi","qfwdf","lefvz","fmmca","xfyzg","hvrmh","ilmlm","remnf","yuijf","qqqnh","tagvi","hdsxn","whnam","ntmyr","tpcyq","yrpny","eptzk","pujbg","celot","jtgef","gvdgp","kboeg","nralc","bpcbq","ezxpc","xdtay","gqmzr","vzuhv","bgrew","dudvt","kuauk","dktbd","qrmpr","nxffe","odfsq","iofrm","tmphf","cwbad","kufdm","ubbtv","ijeft","bftzc","jkoxt","uzlew","swziu","icbum","dbxbp","uraff","fhfzg","rgjok","yfyya","gosak","rhetp","bimce","uvaoh","cxakx","wigyr","jtuif","otvhl","zosxm","wbvfx","vmumu","wsyyl","jjcph","znmmi","vvhkb","prgpa","gzkiw","iktsj","ofiti","ehqxo","qfvns","hqdki","wgbol","lgzmz","moovr","zkdko","bimol","ayujd","mnhqn"}; 
	 			Random rand = new Random();
	 			int val = rand.nextInt(1000000) +1;
	 			int idx = rand.nextInt(4);
	 			String query = command[idx];
	 			int idx1 = rand.nextInt(100);
	 			if(idx==0||idx==1)
		 			query += bookname[idx1] +" " + Integer.toString(val);	
	 			else if (idx==2)
        {
          String[] pass = new String[]{"library123","lib"};
          int id=rand.nextInt(2);
          int v = rand.nextInt(1);
          String str ;
          String isbn ;
          String auth ;
          char[] chas = "abcdefghijklmnopqrstuvwxyz".toCharArray();
          StringBuilder sb2 = new StringBuilder();
          for (int i = 0; i < 6; i++) {
              char c = chas[rand.nextInt(chas.length)];
              sb2.append(c);
          }
          auth = sb2.toString();
          
          char[] chass = "123456789".toCharArray();
          StringBuilder sb1 = new StringBuilder();
          for (int i = 0; i < 13; i++) {
              char c = chass[rand.nextInt(chass.length)];
              sb1.append(c);
          }
          isbn = sb1.toString();
          
          if(v==0)
            str = bookname[idx1];
          else 
          {
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                char c = chars[rand.nextInt(chars.length)];
                sb.append(c);
            }
            str = sb.toString();
          }
          int mem = rand.nextInt(4) + 1;
          int pr = rand.nextInt(1000) + 100; 
          query += str + " " + auth+ " " + isbn +" " + pr + " " + mem + " " + pass[id];  
        }
 			 	else
 			 		query += bookname[idx1];

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


public class client
{

 
    public static void main(String args[])
    {
    	  ThreadClass[] clients=new ThreadClass[101];
     	  for (int i=1;i<=10 ;i++ ) 
     	  {
     	  		clients[i] = new ThreadClass(Integer.toString(i));
      			clients[i].start();	
     	  }
    }
}