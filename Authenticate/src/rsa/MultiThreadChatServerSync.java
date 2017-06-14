package rsa;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.ServerSocket;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/*
*A chat server that delivers public and private messages.
*/
public class MultiThreadChatServerSync{

   private static ServerSocket serverSocket=null;
   private static Socket clientSocket=null;

   private static final int maxClientsCount=2;
   private static final clientThread [] threads=new clientThread[maxClientsCount];
   static encryptor en = new encryptor();
 //generate public and private keys for server
   static RSA rsa = new RSA();
   private static final KeyPair keyPair = rsa.rsaKeyGen();
   public static PublicKey publicKey = keyPair.getPublic();
   static PrivateKey privateKey = keyPair.getPrivate();
   //generate master key
   private static SecretKey masterKey = null;
   //Generate session key
   private static SecretKey sessionKey = null;

   public static void main(String [] args){
	   sessionKey = en.genKey();
	   masterKey = en.genKey();
      //the default port number;
      int portNumber=2050;
      if(args.length <1){
         System.out.println("Connection established using port number = "+portNumber);
      }
      else{
         portNumber=Integer.valueOf(args[0]).intValue();
         System.out.println("Connection established using port number = "+portNumber);
      }
      /*
   *Open a server socket on the portNumber(default 222). Note that we can
   *not choose a port less than 1023 if we are not privileged usera(root)
   */
   try{
      serverSocket=new ServerSocket(portNumber);

      //String keyCLientA = "";
      //String keyCLientB = "";
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
   
   //create NONCE to be used for authentication
   static byte[] getNonce(){
	   byte[] nonce = new byte[128];
	   try{
		   SecureRandom sr = new SecureRandom();
		   sr.nextBytes(nonce);
		   /**DEBUG
		   System.out.println(Array.toString(nonce));*/
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   
	   return nonce;
   }
   static SecretKey getSession(){
	   return sessionKey;
   }

public SecretKey getMaster() {
	return masterKey;
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
   private SecretKey master;
   private SecretKey session;
   static PublicKey serverPubKey;
   //generate public and private keys for client
   RSA rsa = new RSA();
   private KeyPair keyPair = null; 
   public PublicKey publicKey = null;  
   private PrivateKey privateKey = null; 
   
   MultiThreadChatServerSync server = new MultiThreadChatServerSync();
   encryptor encr = new encryptor();
  
   ByteArrayOutputStream bos;
   ObjectOutputStream oos;
   ArrayList<byte[]> signedData;
   
   public clientThread(Socket clientSocket,clientThread [] threads){
      this.clientSocket=clientSocket;
      this.threads=threads;
      maxClientsCount=threads.length;
      this.master = server.getMaster();
      this.serverPubKey = MultiThreadChatServerSync.publicKey;
      this.keyPair = rsa.rsaKeyGen();
      this.publicKey = keyPair.getPublic();
      this.privateKey = keyPair.getPrivate();
   }
   
   //method to send encrypted NONCE for authentication
   private ArrayList<byte[]> sendEncrypted(byte[] nonce, byte[] iv){
	   byte[] encrypted = null;
	   
	   try{
		   byte[] masterEncr = encryptor.encrypt(nonce, iv, this.master.getEncoded());
		   encrypted = rsa.encrypt(masterEncr, this.serverPubKey);
		   byte[] sign = rsa.sign(encrypted, this.privateKey);
		   //System.out.println(Arrays.toString(sign));
		   this.signedData = new ArrayList<byte[]>();
		   this.signedData.add(encrypted); 
		   this.signedData.add(sign);
		   //System.out.println(new String(encrypted));
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   
	   return signedData;
   }


public void run(){
      //int maxClientscount=this.maxClientsCount;
      clientThread [] threads=this.threads;

      try{
           /*
         *Create input and output streams for this client.
         */
         is=new DataInputStream(clientSocket.getInputStream());
         os=new PrintStream(clientSocket.getOutputStream());
         String name;
         byte[] nonce = null;
         byte[] initVector = null;
         byte[] encrypted = null;
         byte[] decrypt = null;;
         byte[] rsaEncr = null;
         
         os.println("Enter your name");
         name=is.readLine().trim();
         
         synchronized(this){
            for(int i=0;i<maxClientsCount;i++){
               if(threads[i] !=null && threads [i] ==this){
                  clientName=name;
                  /******authentication protocol******/
                  nonce = server.getNonce();
                  initVector = encryptor.initialisationVector();
                  ArrayList<byte[]> signed = this.sendEncrypted(nonce, initVector);
                  
                  
                  try {
                	  rsaEncr = signed.remove(0);
                	  encrypted = rsa.decrypt(rsaEncr, MultiThreadChatServerSync.privateKey); //decrypt first using server's key
                	  boolean verified = rsa.verifySign(encrypted, signed.remove(0), this.publicKey); //verify encrypted message came from client
                	  decrypt = encryptor.decrypt(encrypted, initVector, server.getMaster().getEncoded()); //decrypt the message if it came from client
                	  if(verified){   	
                	  }
				} catch (Exception e) {
					e.printStackTrace();
				}
                  this.os.println("NONCE: " + Arrays.toString(nonce));
                  this.os.println("Encrypted with server's public key: " + Arrays.toString(rsaEncr));
                  this.os.println("Encrypted NONCE with master key: " + Arrays.toString(encrypted));
                  this.os.println("Decrypted NONCE: " + Arrays.toString(decrypt));
                  this.os.flush();
                  //System.out.println(Arrays.toString(nonce).equals(Arrays.toString(decrypt)));
                  	
                  	 /**this.os.println(" You have been connected");
                	 this.session = server.getSession();
                     this.os.println("Session key "+ this.session + " has been generated for the session" );
                     this.os.println("Master key for thread: " + this.master);
                     this.os.println("Server Public Key: " + this.serverPubKey);
                     this.os.println("Your Public Key: " + this.publicKey);
                     this.os.println("Your private Key: " + this.privateKey);*/
                  
                  break;
               }
            }
			
            for(int i=0;i<maxClientsCount;i++){
               if(threads[i] !=null && threads[i] != this){
                  threads[i].os.println("***" + name + " connected on the server ***");
               }
            }
         }
        /* if(threads.length == 1){
           encryptor en = new encryptor();
           masterKey = en.genKey();
           System.out.println("Master key "+masterKey.toString() + " has been generated for the session" );
           //Generate session key
            sessionKey = en.genKey();
            System.out.println("Session key "+sessionKey.toString() + " has been generated for the session" );
         }else{}*/

try {
         /*Start conversation.*/
	 	
         while(true){
			String line=is.readLine();
            /*Hash the text user has entered*/
            //String hash = encr.hashText(line);
           
	    initVector = encryptor.initialisationVector();
	    	encrypted = encryptor.encrypt(line.getBytes(), initVector, this.session.getEncoded());
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
							threads[i].os.println("<"+name + "> "+ new String(encrypted));
							byte[] decrypted = encryptor.decrypt(encrypted, initVector, this.session.getEncoded());
							String msg = new String(decrypted);
			                       		threads[i].os.println("<"+name + "> "+ msg);
						}
                    }
                 }
              }

         }
} catch(Exception e) {e.printStackTrace();}

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
