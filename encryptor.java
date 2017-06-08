
import java.util.UUID;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import javax.crypto.Cipher;
import java.util.Base64;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import sun.misc.*;

public class encryptor{ 
   public static void main(String args[]){
      try {

	 System.out.println("Ayanda20");
	 String siseko = encrypt("Ayanda20", "sisekoqwertyuiop", "athabhazxcvbnmas");
      	 System.out.println(siseko) ;

	 System.out.println("decrypt");
	 String decr = decrypt(siseko, "sisekoqwertyuiop", "athabhazxcvbnmas");
      	 System.out.println(decr) ;

      }catch(UnsupportedEncodingException e){
         System.out.println("Error :" + e.getMessage());
      } catch (Exception ex) {
            Logger.getLogger(encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
   }

   public static String encrypt(String Data, String iv, String seckeyValue) throws Exception {
	IvParameterSpec initVector = new IvParameterSpec(iv.getBytes("utf-8"));
	SecretKeySpec key = new SecretKeySpec(seckeyValue.getBytes("UTF-8"), "AES");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, key, initVector);

        byte[] encVal = c.doFinal(Data.getBytes());
	
        String encryptedValue = Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData, String iv, String seckeyValue) throws Exception {
	IvParameterSpec initVector = new IvParameterSpec(iv.getBytes("utf-8"));
	SecretKeySpec key = new SecretKeySpec(seckeyValue.getBytes("UTF-8"), "AES");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, key, initVector);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
        
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
}
