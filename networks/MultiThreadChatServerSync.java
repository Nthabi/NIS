import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;


/*
*A chat server that delivers public and private messages.
*/
public class MultiThreadChatServerSync{

   private static ServerSocket serverSocket=null;
   private static Socket clientSocket=null;
   private static final int maxClientsCount=2;
   private static final clientThread [] threads=new clientThread[maxClientsCount];
   private static SecretKey masterKey = null;

   public static void main(String [] args){
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
               if(i==1){
                 encryptor en = new encryptor();
                 masterKey = en.genKey();
                 System.out.println("Master key "+masterKey.toString() + " has been generated" );
               }
               break;
            }
         }
         if(i==maxClientsCount){
            PrintStream os=new PrintStream(clientSocket.getOutputStream());
            os.println("Server to busy.Try later");
            os.close();
            clientSocket.close();
         }
      }catch(IOException e){
         System.out.println(e);
      }
   }
   }

}

/*
*The chat client thread. this client thread opnes the input and the output
* streams for a particular client, asks the client's name and as long as it recieve data, echoes that data back
* to other client. When a client leaves other client is informed.
*/
class clientThread extends Thread{
   private String clientName = null;
   private DataInputStream is=null;
   private PrintStream os =null;
   private Socket clientSocket=null;
   private final clientThread [] threads;
   private int maxClientsCount;
   private static SecretKey sessionKey = null;
   //public static ArrayList<String> encryptedData; //Store the hash, encrypted msg, recipient publicKey


   public clientThread(Socket clientSocket,clientThread [] threads){
      this.clientSocket=clientSocket;
      this.threads=threads;
      maxClientsCount=threads.length;
   }

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

          os.println("Enter your name");
          name=is.readLine().trim();

         /*Welcome the new client and generate a shared master key the 2 clients
            Also generates a shared session key used for encryption
         */

         encryptor object = new encryptor();
         os.println(" You have been connected");
         synchronized(this){
            for(int i=0;i<maxClientsCount;i++){
               if(threads[i] !=null && threads [i] ==this){
                  clientName=name;
                  if(i==1){
                    //encryptor en = new encryptor();
                    //Generate session key
                     sessionKey = object.genKey();
                     System.out.println("Session key "+sessionKey.toString() + " has been generated for the session" );
                  }
                  break;
               }
            }
            for(int i=0;i<maxClientsCount;i++){
               if(threads[i] !=null && threads[i] != this){
                  threads[i].os.println("***" + name + " connected on the server ***");
               }
            }}


try {
         /*Start conversation.*/

          //encryptedData = new ArrayList<String>();
          while(true){
               String line=is.readLine();

               /*Hash the text user has entered*/
               String hash = object.hashText(line);
               /*Encrypt message with sessionKey*/
    	         byte[] initVector = object.initialisationVector();
    	         String encryptedMessage = object.encrypt(line, initVector, sessionKey.toString());

                if(line.startsWith("/quit")){
                   break;
                }

            /*Send hash and encrypted message to  client.*/
              synchronized(this){
                 for(int i=0; i<maxClientsCount; i++){
                    if(threads[i] != null && threads[i].clientName != null){
			                   if (threads[i] != this) {
				                       threads[i].os.println("<"+name + "> "+ encryptedMessage);
				                           String decryptedMessage = object.decrypt(encryptedMessage, initVector,sessionKey.toString());

                                   /*Check if hash of decryptedMessage == original message hash and print else show error*/
                                   String decryptedMessageHash = object.hashText(decryptedMessage);
                                   if(decryptedMessageHash.equals(hash)){
                                      threads[i].os.println("<"+name + "> "+ decryptedMessage);
                                   }else{
                                     System.out.println("Your device has been compromised! Message from unreliable source");
                                   }

			                   }
                    }
                 }
              }

         }
} catch(Exception e) { }

         synchronized(this){
            for(int i=0; i<maxClientsCount;i++){
               if(threads[i] != null && threads [i] != this && threads[i].clientName !=null){
                  threads[i].os.println("*** The user "+ name + " has disconnected ***");
               }
            }
         }

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

      //}
   }
}

}
