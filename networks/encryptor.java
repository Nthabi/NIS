
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

public class encryptor {
   public static void main(String args[]){
      try {
	 byte[] initVector = initialisationVector();
	 System.out.println(initVector);
	 System.out.println("Ayanda20");
	 String siseko = encrypt("Ayanda20", initVector, "athabhazxcvbnmas");
      	 System.out.println(siseko) ;

	 System.out.println("decrypt");
	 String decr = decrypt(siseko, initVector, "athabhazxcvbnmas");
      	 System.out.println(decr) ;

      }catch(UnsupportedEncodingException e){
         System.out.println("Error :" + e.getMessage());
      } catch (Exception ex) {
            Logger.getLogger(encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
   }

   public static String encrypt(String Data, byte[] iv, String seckeyValue) throws Exception {
	//IvParameterSpec initVector = new IvParameterSpec(iv.getBytes("utf-8"));
	IvParameterSpec initVector = new IvParameterSpec(iv);
	SecretKeySpec key = new SecretKeySpec(seckeyValue.getBytes("UTF-8"), "AES");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, key, initVector);

        byte[] encVal = c.doFinal(Data.getBytes());

        String encryptedValue = Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData, byte[] iv, String seckeyValue) throws Exception {
	IvParameterSpec initVector = new IvParameterSpec(iv);
	SecretKeySpec key = new SecretKeySpec(seckeyValue.getBytes("UTF-8"), "AES");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, key, initVector);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedData);

        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    //This following methods generates 16 byte initialisation vectors
    public static byte[] initialisationVector() {
	 SecureRandom random = new SecureRandom();
	 byte[] values = new byte[16];
	 random.nextBytes(values);
	 return values;
    }


    public SecretKey genKey(){
      /*Sessionkey and master key generator*/
      try{
              KeyGenerator masterKey = KeyGenerator.getInstance("AES");
              masterKey.init(256);
              SecretKey key = masterKey.generateKey();
              //System.out.println("Master key established:" + key);
              return key;

            }catch(NoSuchAlgorithmException e){
              System.out.println("Unexpected error in creating key");
              return null;}
  }

  public String hashText (String s){
  /*String hashing function using MD5*/
    byte[] hash = null;
    try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        hash = md.digest(s.getBytes());

    } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < hash.length; ++i) {
        String hex = Integer.toHexString(hash[i]);
        if (hex.length() == 1) {
            sb.append(0);
            sb.append(hex.charAt(hex.length() - 1));
        } else {
            sb.append(hex.substring(hex.length() - 2));
        }
    }
    return sb.toString();
  }



}
