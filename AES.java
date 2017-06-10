import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AES {

	
	SecretKey aesKeyGen(String fname){
		SecretKey key = null;
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			key = keyGen.generateKey();
				
			System.out.println(key);				
		}
			
		catch(Exception e){
			e.printStackTrace();
		}
		return key;
	}
}

