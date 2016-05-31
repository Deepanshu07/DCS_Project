/*************************************************************************************
File: server.java
Authors: Deepanshu Gupta and Deepanshu Sapra
Description: Server which handles all client requests on an online library
Last Modified: 31 May 2016
**************************************************************************************/

import java.io.*;                                                        
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Date;
import java.text.*;


class ServerThread extends Thread
{
    	private Thread t;
       private Socket socket;
       private String threadName;
       
       ServerThread(String name, Socket socket){
       		threadName=name;
           this.socket=socket;
           //System.out.println("Creating ");
       }
       public String issueFunc(String[] words)
       {
       		String returnMessage=null;
       		try
       		{
       			String book=words[1];
                String memberID=words[2];
                //System.out.println(book+" "+memberID);

                InputStream fin1=new FileInputStream("books.txt");
                InputStreamReader isr1=new InputStreamReader(fin1);
                BufferedReader br1= new BufferedReader(isr1);

                String line1;
                int flag=0;
                outer:
                while((line1=br1.readLine())!=null)                                //handle whiles till null condition
                {
                    String[] wrds=line1.split(" ");
                    if(wrds[1].equals(book))                                        //could use threads for each search
                    {
                        flag=1;
                        int available=Integer.parseInt(wrds[3]);
                        //check if the book is available
                        if(available>=1)
                        {
                            flag=2;
                            int price=Integer.parseInt(wrds[4]);
                            InputStream fin2= new FileInputStream("members.txt");
                            InputStreamReader isr2= new InputStreamReader(fin2);
                            BufferedReader br2= new BufferedReader(isr2);
                            String line2;
                            while((line2=br2.readLine())!=null)
                            {
                                String[] w=line2.split(" ");
                                //check if the memberID is valid
                                if(w[0].equals(memberID))
                                {
                                    flag=3;
                                    int balance=Integer.parseInt(w[2]);
                                    int booksIssued=Integer.parseInt(w[3]);
                                    //check if sufficient balance and max limit of issues not reached
                                    if((float)balance>= (price*0.1)&&booksIssued<3)
                                    {
                                        flag=4;
                                        //update books.txt
                                        String oldFileName = "books.txt";
                                        String tmpFileName = "books_temp.txt";

                                        File f = new File(tmpFileName);
                                        f.createNewFile();

                                        BufferedReader br3 = null;
                                        BufferedWriter bw3 = null;
                                        br3 = new BufferedReader(new FileReader(oldFileName));
                                        bw3 = new BufferedWriter(new FileWriter(f));
                                        String l;
                                        synchronized(this)
                                        {
                                            while ((l = br3.readLine()) != null) 
                                            {
                                                String[] w1=l.split(" ");
                                                if(w1[1].equals(book))
                                                {   
                                                    int num1=Integer.parseInt(w1[3]);
                                                    num1--;
                                                    String snum = Integer.toString(num1);
                                                    l = w1[0]+" "+w1[1]+" "+w1[2]+" "+snum+" "+w1[4] ;
                                                }
                                                bw3.write(l+"\n");
                                                bw3.flush();
                                            }                 
                                            File oldFile = new File(oldFileName);
                                            oldFile.delete();

                                            File newFile = new File(tmpFileName);
                                            newFile.renameTo(oldFile);
                                        }

                                        //update members.txt
                                        oldFileName = "members.txt";
                                        tmpFileName = "members_temp.txt";

                                        f = new File(tmpFileName);
                                        f.createNewFile();

                                        br3 = new BufferedReader(new FileReader(oldFileName));
                                        bw3 = new BufferedWriter(new FileWriter(f));
                                        synchronized(this)
                                        {
                                            while ((l = br3.readLine()) != null) 
                                            {
                                                String[] w1=l.split(" ");
                                                if(w1[0].equals(memberID))
                                                {   
                                                    int num1=Integer.parseInt(w1[3]);
                                                    int num2=Integer.parseInt(w1[2]);
                                                    double cost=price*0.1;
                                                    int val=(int) cost;
                                                    num2-=val;
                                                    num1++;
                                                    String snum = Integer.toString(num1);
                                                    String snum2 = Integer.toString(num2);
                                                    l = w1[0]+" "+w1[1]+" "+snum2+" "+snum;

                                                }
                                                bw3.write(l+"\n");
                                                bw3.flush();
                                            }                 
                                            File oldFile = new File(oldFileName);
                                            oldFile.delete();

                                            File newFile = new File(tmpFileName);
                                            newFile.renameTo(oldFile);
                                        }
                                        //Make new entry in issued.txt file
                                        File issued=new File("issued.txt");
                                        if(!issued.exists())
                                        {
                                            issued.createNewFile();
                                        }
                                        bw3 = new BufferedWriter(new FileWriter(issued,true));

                                        Date date = new Date();
                                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MMM/yyyy");
                                        String dateText = df2.format(date);
                                        l=w[0]+" "+wrds[1]+" "+dateText;
                                        synchronized(this)
                                        {
                                        	bw3.write(l+"\n");
                                        	bw3.flush();
                                        }

                                        break outer;
                                    }

                                }

                            }
                        }
                    }
                }
                if (flag==0) {
                    returnMessage="There is no such book in the library\n";
                }
                else if (flag==1) {
                    returnMessage="The book is not available\n";
                }
                else if (flag==2) {
                    returnMessage="There is no member with that memberID\n";
                }
                else if (flag==3) {
                    returnMessage="Can't issue due to insufficient balance or max limit of books loaned reached !!\n";
                }
                else{
                    returnMessage="Book successfully issued\n";
                }
       		}
       		catch(Exception e)
       		{
       			e.printStackTrace();
       		}
       		return returnMessage;
       }

       public String returnFunc(String[] words)
       {
       		String returnMessage=null;
       		try
       		{
       			String book=words[1];
                String memberID=words[2];
                //System.out.println(book+" "+memberID);

                //returnMessage="Return Request\n";

                //no.of copies++ delete from issue member isuued book++
                InputStream fin1= new FileInputStream("issued.txt");
                InputStreamReader isr1= new InputStreamReader(fin1);
                BufferedReader br1= new BufferedReader(isr1);

                String line1;
                String tmpFileName1 = "issued_tmp.txt";
                File f1 = new File(tmpFileName1);
                f1.createNewFile();
                BufferedWriter bw1 = null;
                bw1 = new BufferedWriter(new FileWriter(f1));
                int flag=0;
                while((line1=br1.readLine())!=null)
                {
                    String[] wrds=line1.split(" ");
                    //have to change this condition
                    if (wrds[0].equals(memberID) && wrds[1].equals(book))  
                    {
                    	flag=1;
                        //delete this record in issued.txt
                        InputStream fin2=new FileInputStream("books.txt");
                        InputStreamReader isr2=new InputStreamReader(fin2);
                        BufferedReader br2= new BufferedReader(isr2);
                        String line2;
                        String tmpFileName2 = "books_tmp.txt";
                        File f2 = new File(tmpFileName2);
                        f2.createNewFile();
                        BufferedWriter bw2 = null;
                        bw2 = new BufferedWriter(new FileWriter(f2));

                        synchronized(this)
                        {
                            while((line2=br2.readLine())!=null)                                //handle whiles till null condition
                            {
                                String[] w1=line2.split(" ");
                                if(w1[1].equals(book))                                        //could use threads for each search
                                {
                                    int num1=Integer.parseInt(w1[3]);
                                    num1++;
                                    String snum = Integer.toString(num1);
                                    line2 = w1[0]+" "+w1[1]+" "+w1[2]+" "+snum+" "+w1[4] ;
                                }
                                bw2.write(line2+"\n");
                                bw2.flush();
                            }

                            File oldFile2 = new File("books.txt");
                            oldFile2.delete();

                            File newFile2 = new File(tmpFileName2);
                            newFile2.renameTo(oldFile2);
                        }
                        InputStream fin3= new FileInputStream("members.txt");
                        InputStreamReader isr3= new InputStreamReader(fin3);
                        BufferedReader br3= new BufferedReader(isr3);
                        String line3;
                        String tmpFileName3 = "members_tmp.txt";
                        File f3 = new File(tmpFileName3);
                        f3.createNewFile();
                        BufferedWriter bw3 = null;
                        bw3 = new BufferedWriter(new FileWriter(f3));

                        synchronized(this)
                        {
                            while((line3=br3.readLine())!=null)                                //handle whiles till null condition
                            {
                                String[] w1=line3.split(" ");
                                if(w1[0].equals(memberID))                                        //could use threads for each search
                                {
                                    int num1=Integer.parseInt(w1[3]);
                                    num1--;
                                    String snum = Integer.toString(num1);
                                    line3 = w1[0]+" "+w1[1]+" "+w1[2]+" "+snum;
                                }
                                bw3.write(line3+"\n");
                                bw3.flush();
                            }

                            File oldFile3 = new File("members.txt");
                            oldFile3.delete();

                            File newFile3 = new File(tmpFileName3);
                            newFile3.renameTo(oldFile3);
                        }
                    }
                    else
                    {
                        bw1.write(line1+"\n");                       //check again
                        bw1.flush();
                    }
                }
                if (flag==0) {
                	returnMessage="This book hasn't been issued by this member\n";
                }
                else
                {
                	returnMessage="Book successfully returned\n";
                }
                synchronized(this)
                {
                    File oldFile1 = new File("issued.txt");
                    oldFile1.delete();

                    File newFile1 = new File(tmpFileName1);
                    newFile1.renameTo(oldFile1);
                }
       		}
       		catch(Exception e)
       		{
       			e.printStackTrace();
       		}
       		return returnMessage;
       }

       public String addFunc(String[] words)
       {
       		String returnMessage=null;
       		try
       		{
       			returnMessage="adding a book\n";
                String title=words[1];
                String author=words[2];
                String isbn=words[3];
                String price=words[4];
                String adminID=words[5];
                String passwd=words[6];

                InputStream fin=new FileInputStream("admins.txt");
                InputStreamReader aisr=new InputStreamReader(fin);
                BufferedReader abr= new BufferedReader(aisr);


                String line1,line2;
                int flag=0;
                while((line1=abr.readLine())!=null)
                {
                    String wrds[]=line1.split(" ");
                    if(wrds[0].equals(adminID)&&passwd.equals("library123"))
                    {
                        flag=1;
                        InputStream fin1=new FileInputStream("books.txt");
                        InputStreamReader isr1=new InputStreamReader(fin1);
                        BufferedReader br1= new BufferedReader(isr1);

                        while((line2=br1.readLine())!=null)
                        {
                            String w[]=line2.split(" ");
                            if(w[1].equals(title))
                            {
                                flag=2;

                                String oldFileName = "books.txt";
                                String tmpFileName = "books_temp.txt";

                                File f = new File(tmpFileName);
                                f.createNewFile();

                                BufferedReader br3 = null;
                                BufferedWriter bw3 = null;
                                br3 = new BufferedReader(new FileReader(oldFileName));
                                bw3 = new BufferedWriter(new FileWriter(f));
                                String l;
                                synchronized(this)
                                {
                                    while ((l = br3.readLine()) != null) 
                                    {
                                        String[] w1=l.split(" ");
                                        if(w1[1].equals(title))
                                        {   
                                            int num1=Integer.parseInt(w1[3]);
                                            num1++;
                                            String snum = Integer.toString(num1);
                                            l = w1[0]+" "+w1[1]+" "+w1[2]+" "+snum+" "+w1[4] ;
                                        }
                                        bw3.write(l+"\n");
                                        bw3.flush();
                                    }                 
                                    File oldFile = new File(oldFileName);
                                    oldFile.delete();

                                    File newFile = new File(tmpFileName);
                                    newFile.renameTo(oldFile);
                                }
                                break;
                            }
                        }
                        if (flag==1)
                        {
                            File fbook=new File("books.txt");
                            synchronized(this)
                            {
                            	if(!fbook.exists())
                            	{
                                	fbook.createNewFile();
                                }
                                BufferedWriter bw3 = new BufferedWriter(new FileWriter(fbook,true));

                                String l=isbn+" "+title+" "+author+" "+1+" "+price;
                                bw3.write(l+"\n");
                                bw3.flush();
                            }   
                        }

                        String oldFileName = "admins.txt";
                        String tmpFileName = "admins_temp.txt";

                        File f = new File(tmpFileName);
                        f.createNewFile();

                        BufferedReader br3 = new BufferedReader(new FileReader(oldFileName));
                        BufferedWriter bw3 = new BufferedWriter(new FileWriter(f));
                        String l;
                        synchronized(this)
                        {
                            while ((l = br3.readLine()) != null) 
                            {
                                String[] w1=l.split(" ");
                                if(w1[0].equals(adminID))
                                {   
                                    int num1=Integer.parseInt(w1[3]);
                                    num1++;
                                    String snum = Integer.toString(num1);
                                    l = w1[0]+" "+w1[1]+" "+w1[2]+" "+snum;

                                }
                                bw3.write(l+"\n");
                                bw3.flush();
                            }                 
                            File oldFile = new File(oldFileName);
                            oldFile.delete();

                            File newFile = new File(tmpFileName);
                            newFile.renameTo(oldFile);
                        }
                        returnMessage="The book has been successfully added to the library !\n";
                    }
                }
                if(flag==0)
                {
                    returnMessage="Invalid MemberID or Password !\n";
                }

       		}
       		catch(Exception e)
       		{
       			e.printStackTrace();
       		}
       		return returnMessage;
       }

       public String showFunc(String[] words)
       {
       		String returnMessage=null;
       		try
       		{
       			returnMessage="Book Details Request";
                String title=words[1];
                InputStream fin1=new FileInputStream("books.txt");
                InputStreamReader isr1=new InputStreamReader(fin1);
                BufferedReader br1= new BufferedReader(isr1);
                String l;
                int flag=0;
                while((l=br1.readLine())!=null)
                {
                    String[] w=l.split(" ");
                    if(w[1].equals(title))
                    {
                        flag=1;
                        returnMessage="\tISBN->"+w[0]+"\tTitle->"+w[1]+"\tAuthor->"+w[2]+"\tNo. of Copies->"+w[3]+"\tPrice->"+w[4]+"\n";
                    }
                }
                if(flag==0)
                {
                    returnMessage="The book is not present in the library.\n";
                }
       		}
       		catch(Exception e)
       		{
       			e.printStackTrace();
       		}
       		return returnMessage;
       }

       public void run() 
		{
			try
			{
				InputStream is = socket.getInputStream();               //reads input in bytes
                InputStreamReader isr = new InputStreamReader(is);      //maps bytes to charcacter one at a time
                BufferedReader br = new BufferedReader(isr);                       //to read entire line into buffer so that wont have to do I/O for each charcater
                String query = br.readLine();
                System.out.println("Message received from client is: "+query);
 		
                //Multiplying the number by 2 and forming the return message
                String returnMessage;
                String[] words = query.split(" ");
                //handling issue request
                if(words[0].equals("issue"))                  
                {
                    returnMessage=issueFunc(words);	
                }
                else if (words[0].equals("return")) 
                {
                    returnMessage=returnFunc(words);
                }
                else if (words[0].equals("add")) 
                {
                    returnMessage=addFunc(words);
                } 
                else if (words[0].equals("show")) 
                {
                    returnMessage=showFunc(words);
            	}
            	else
                {
                    returnMessage="Invalid Request\n";
                }                                                    //add conditions
                //Sending the response back to the client.
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(returnMessage);
                System.out.println("Message sent to the client is: "+returnMessage);
                bw.flush();
            }
    		catch (Exception e)
    		{
        		e.printStackTrace();
    		}

          /*System.out.println("Running " +  threadName );
          try {
             for(int i = 4; i > 0; i--) {
                System.out.println("Thread: " + threadName + ", " + i);
                // Let the thread sleep for a while.
                Thread.sleep(50);
             }
         } catch (InterruptedException e) {
             System.out.println("Thread " +  threadName + " interrupted.");
         }
         System.out.println("Thread " +  threadName + " exiting.");*/
       }
       
       public void start ()
       {
          System.out.println("Starting processing a request");
          if (t == null)
          {
             t = new Thread (this, threadName);
             t.start ();
          }
       }
}
 
public class server
{
 
    private static Socket socket;
 
    public static void main(String[] args)
    {
        try
        {
 
            int port = 25000,i=0;                                                               //make program menu driven
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server Started and listening to the port 25000");
 			ServerThread[] th=new ServerThread[101];  
            //server is always running
            while(true)
            {
                //Fetching the message from the client
                socket = serverSocket.accept();
                th[i] =new ServerThread(Integer.toString(i),socket);
                th[i].start();
                i++;
                
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch(Exception e){}
        }
    }
}