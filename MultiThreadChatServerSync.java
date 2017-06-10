package rsa;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.crypto.*;
import java.io.PrintStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.net.ServerSocket;
/*
*A chat server that delivers public and private messages.
*/
public class MultiThreadChatServerSync{
	//server's public key 
	static PublicKey publicKey = null;
	static File pub_key = new File("public.key"); //name of public key file
	static File prv_key = new File("private.key"); //name of private key file
	//file to store public keys
	static File public_keys = new File("keys.public");
   //the server socket
   private static ServerSocket serverSocket=null;
   //The client socket
   private static Socket clientSocket=null;
   //This chat server can accept up to maxClientsCount' connections.
   private static final int maxClientsCount=10;
   private static final clientThread [] threads=new clientThread[maxClientsCount];
   public static void main(String [] args){
	  
	   PrivateKey privateKey;
	   
	   //create public and private keys for server
	   RSA rsa = new RSA();
	   createFile(pub_key);
	   createFile(prv_key);

	   try {
	    	  
		   //generate keys and store them in separate files
		   rsa.rsaKeyGen(pub_key.getName(), prv_key.getName());
	    	  
		   //get keys from file and store them in variable
		   publicKey = rsa.getPublicKey(pub_key.getName());
		   privateKey = rsa.getPrivateKey(prv_key.getName());
	    	  
		   ///** **DEBUG
		    System.out.println(publicKey);
			System.out.println(privateKey);//*/
	    	  
		   //delete files after accessing keys
		   pub_key.delete();
		   prv_key.delete();
	    	  
			} catch (Exception e) {
				e.printStackTrace();
			}
	   
      //the default port number;
      int portNumber=2040;
      if(args.length <1){
         System.out.println("Connection established using port number = "+portNumber);
      }
      else{
         portNumber=Integer.valueOf(args[0]).intValue();
      }
      /*
   *Open a server socket on the portNumber(default 222). Note that we can
   *not choose a port less than 1023 if we are not privileged usera(root)
   */
   try{
      serverSocket=new ServerSocket(portNumber);
      String keyCLientA = "";
      String keyCLientB = "";
   }catch(IOException e){
      System.out.println(e);
   }
   /*
   *Create a client socket for each connection and pass it to a new client
   *thread
   */
   while(true){
      try{
         clientSocket=serverSocket.accept();
         int i=0;
         for( i=0; i<maxClientsCount;i++){
            if(threads[i]==null){
               (threads[i]=new clientThread(clientSocket,threads)).start();
               break;
            }
         }
         if(i==maxClientsCount){
            PrintStream os=new PrintStream(clientSocket.getOutputStream());
            os.println("Server too busy.Try later");
            os.close();
            clientSocket.close();
         }
      }catch(IOException e){
         System.out.println(e);
      }
   }
   }
   
   /*This method allows clients to share their public keys with the server and get public key of the server
    * The server then stores the clients' public keys to use during RSA encryption
   
   static PublicKey publicKeyExchange(PublicKey clientKey, String username){
	//System.out.println(client);
	try {
		FileOutputStream fos;
		fos = new FileOutputStream(public_keys);
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
		
		oos.writeObject(username);
		oos.writeObject(clientKey);
		System.out.println("Client's public key stored: " + clientKey);
		oos.flush();
		oos.close();
		fos.flush();
		fos.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	   return publicKey;
   }*/
   
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
   static PublicKey getPublicKey(){
	   return publicKey;
   }
}

/*
*The chat client thread. this client thread opnes the input and the output
* streams for a particular client, asks the client's name , informs all the
*Clients connectred to the server about the the fact that a new client has joined
*the chat room, and as long as it recieve data, echoes that data back to all
* other clients. When a client leaves the chat room this thread informs
*also all other clients about that and terminates.
*/
class clientThread extends Thread{
   private String clientName = null;
   private DataInputStream is=null;
   private PrintStream os =null;
   private Socket clientSocket=null;
   private final clientThread [] threads;
   private int maxClientsCount;


   
   public clientThread(Socket clientSocket,clientThread [] threads){
      this.clientSocket=clientSocket;
      this.threads=threads;
      maxClientsCount=threads.length;

   }

   @SuppressWarnings("deprecation")
public void run(){
      int maxClientscount=this.maxClientsCount;
      clientThread [] threads=this.threads;
      try{
           /*
         *Create input and output streams for this client.
         */
         is=new DataInputStream(clientSocket.getInputStream());
         os=new PrintStream(clientSocket.getOutputStream());
         String name;
         while(true){
            os.println("Enter your name");
            name=is.readLine().trim();
            if(name.indexOf('@')==-1){
               break;
            }
            else{
               os.println("The name should not contain '@' character.");
            }
            //authentication protocol here...
           
         }
         /*Welcome the new client.*/
         os.println(" You have been connected");
         synchronized(this){
            for(int i=0;i<maxClientsCount;i++){
               if(threads[i] !=null && threads [i] ==this){
                  clientName=name;
                  break;
               }
            }
            for(int i=0;i<maxClientsCount;i++){
               if(threads[i] !=null && threads[i] != this){
                  threads[i].os.println("***" + name + " connected on the server ***");
               }
            }
         }
try {
         /*Start conversation.*/
	 encryptor object = new encryptor();
         while(true){
            String line=is.readLine();
	    byte[] initVector = object.initialisationVector();
	    String siseko = object.encrypt(line, initVector, "athabhazxcvbnmas");
            if(line.startsWith("/quit")){
               break;
            }
            /*Send private message to  client.*/
               /*
              The message is public, broadcast it to all other clients.
              */
              synchronized(this){
                 for(int i=0; i<maxClientsCount; i++){
                    if(threads[i] != null && threads[i].clientName != null){
			if (threads[i] != this) {
				threads[i].os.println("<"+name + "> "+ siseko);
				String decr = object.decrypt(siseko, initVector, "athabhazxcvbnmas");
                       		threads[i].os.println("<"+name + "> "+ decr);
			}
                    }
                 }
              }
        
         }
} catch(Exception e) { }

	 /*
         synchronized(this){
            for(int i=0; i<maxClientsCount;i++){
               if(threads[i] != null && threads [i] != this && threads[i].clientName !=null){
                  threads[i].os.println("*** The user "+ name + " has disconnected ***");
               }
            }
         }*/

         os.println("*** Bye "+ name + " ****");
         /*
         *Clean up. Set the current thread variable to null so that a new client
         *could be accepted by the server.
         */
         synchronized(this){
            for(int i=0; i<maxClientsCount; i++){
               if(threads[i]==this){
                  threads[i]=null;
               }
            }
         }
         /*Close the output stream, close the input stream, close the socket*/
         is.close();
         os.close();
         clientSocket.close();

      }
      catch(IOException e){

      }
   }
}
