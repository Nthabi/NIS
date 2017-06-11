import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class auth {

	public static void main(String[] args) throws Exception{
		// Generate a DES key
	    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
	    SecretKey key = keyGen.generateKey();
	    System.out.println(key);

	    // Generate a Blowfish key
	    keyGen = KeyGenerator.getInstance("Blowfish");
	    key = keyGen.generateKey();
	    System.out.println(key);
	    // Generate a triple DES key
	    keyGen = KeyGenerator.getInstance("DESede");
	    key = keyGen.generateKey();
	    System.out.println(key);
	}

}
