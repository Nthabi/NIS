import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
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
   */int connections = 0;
   try{
      serverSocket=new ServerSocket(portNumber);
      connections++;
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
         //int clientCount = 1;
         //while(true){
            os.println("Enter your name");
            name=is.readLine().trim();
         /*Welcome the new client and generate master key for each of the 2 clients*/
         if(maxClientsCount == 2){
           encryptor en = new encryptor();
           SecretKey masterKey = en.genMasterKey();
           System.out.println("Master key "+masterKey.toString() + " has been generated for the session" );
         }

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
