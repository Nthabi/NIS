import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.crypto.*;

public class Client {
  private string privateKey;
  public string publicKey;
  public string sessionKey;

  public Client(){
    /*Constructor to initialize the keys*/
  }

  public boolean Encrypt(){
  /*Encrypt Client's message using CLientB's publicKey*/

    return true;
  }

  public int HashMessage(){
  /*Create a hash of the message*/
    return 0;
  }



public static void main(String args[]){
   Socket clientSocket = null;
   DataInputStream is=null;
   PrintStream os = null;
   DataInputStream inputLine=null;




/*
Open a socket on port 2222. Open the input an the output streams
*/
  try{
   clientSocket = new Socket("localHost",2222);
   os= new PrintStream(clientSocket.getOutputStream());
   is=new DataInputStream(clientSocket.getInputStream());
   inputLine=new DataInputStream(new BufferedInputStream(System.in));
  }
  catch(UnknownHostException e){
   System.err.println("Don't know about host");
  }
  catch(IOException e){
   System.err.println("Couldn't get I/O for the connection to the host");
  }

  /*If everything has been initialised then we want to write some data to the
  *socket we have opened a connection to on port 2222
  */

  if(clientSocket != null && os != null && is != null){
   try{
      /*
      Keep reading from/to the socket till we recieve the "Ok" from the
      *sever, once we recieved that then we break
      */
      System.out.println("The client started. Type any text. To quit type 'Ok'");
      String responseLine;
      os.println(inputLine.readLine());
      while((responseLine =is.readLine()) != null){
         System.out.println(responseLine);
         if(responseLine.indexOf("Ok") !=-1){
            break;
         }
         os.println(inputLine.readLine());

      }
      /*
      Close the output stream, close the input stream, close the socket
      */
      os.close();
      is.close();
      clientSocket.close();
   }catch(UnknownHostException e){
      System.err.println("Trying to connect to unknown host: " + e);
   }
   catch(IOException e){
      System.err.println("IOException: "+ e);
   }

  }
  }
}
