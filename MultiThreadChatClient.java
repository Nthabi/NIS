package rsa;

import java.io.DataInputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

public class MultiThreadChatClient implements Runnable{
   //The client socket
   private static Socket clientSocket= null;
   //the output stream
   private static PrintStream os= null;
   //input stream
   private static DataInputStream is=null;
   private static BufferedReader inputLine=null;
   private static boolean closed=false;
   static PublicKey serverPubKey;

   
   public static void main(String [] args){
	   //variables to store public and private keys
	   PublicKey publicKey;
	   PrivateKey privateKey;
	   
	   File pub_key = new File("public.key"); //name of public key file
	   File prv_key = new File("private.key"); //name of private key file
      // the default port
      int portNumber=2040;
      //default hos
      String host="localhost"; //change to connect to a dif server
      if(args.length <2){
         System.out.println("Connecting using "+ host+" ,portNumber "+portNumber);    
      }
      else{
         host=args[0];
         portNumber=Integer.valueOf(args[1]).intValue();
      }
      
      //creating public and private key for client and storing them in relevant files
      RSA rsa = new RSA();
      createFile(pub_key);
      createFile(prv_key);

      try {
    	  
    	  //generate keys and store them in separate files
    	  rsa.rsaKeyGen(pub_key.getName(), prv_key.getName());
    	  
    	  //get keys from file and store them in variable
    	  publicKey = rsa.getPublicKey(pub_key.getName());
    	  privateKey = rsa.getPrivateKey(prv_key.getName());
    	  
    	  /** **DEBUG
    	   * System.out.println(publicKey);
    	  System.out.println(privateKey);*/
    	  
    	  //delete files after accessing keys
    	  pub_key.delete();
    	  prv_key.delete();
    	  
    	  //get server's public key and send client's public key to server
    	  //serverPubKey = MultiThreadChatServerSync.publicKeyExchange(publicKey, "something");
    	  //serverPubKey = MultiThreadChatServerSync.publicKey;
    	  System.out.println("Server public key: " + serverPubKey);
    	  
		} catch (Exception e) {
			e.printStackTrace();
		}
      
      /*
      *Open a socket on a given host and port. Open input and output streams
      */
        try{
            clientSocket=new Socket(host,portNumber);
            inputLine=new BufferedReader(new InputStreamReader(System.in));
            os=new PrintStream(clientSocket.getOutputStream());
            is=new DataInputStream(clientSocket.getInputStream());
        }   catch(UnknownHostException e){
               System.err.println("Don't know about host "+ host);
        }
        catch(IOException e){
         System.err.println("Couldn't get I/O for the connection to host " + host);
        }
        /*
        *If everything has been initialised then we want to write some data to the
        *socket we have opened a connection to on the port portNumber.
        */
        if(clientSocket !=null && os != null && is!=null){
         try{
               //Create a thread to read from the server
               new Thread(new MultiThreadChatClient()).start();
               while(!closed){
                  os.println(inputLine.readLine().trim());
               }
               /*
               *Close the output stream, close the input stream, close the socket
               */
               os.close();
               is.close();
               clientSocket.close();

         }
         catch(IOException e){
            System.err.println("IOException: "+ e);
         }
        }

   }
   /*
   *Create a thread to read from a server.(non javadoc)
   *@see ja.lang.Runnable#run

   */
   public void run(){
     /*
     *Keep reading from the socket till we recieve "Bye" from the
     *server. Once we recieved that then we want to break.
     */
      String responseLine;
      try{
         while((responseLine=is.readLine()) !=null){
            System.out.println(responseLine);
            if(responseLine.indexOf("***Bye")!=-1)
               break;
         }
         closed=true;
      }catch(IOException e){
         System.err.println("IOException: "+ e);
      }
   }

   //create files
   private static void createFile(File fname){
	   File file = fname;
	      try {
	    	  if(!file.exists()){
	    	  
				file.createNewFile();
	    	  }
	      }
	      catch(IOException e){
	    	  e.printStackTrace();
	      }
   }
   
}
