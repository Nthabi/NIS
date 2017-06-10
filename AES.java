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
				
			oos.writeObject(key);
				
			}
			oos.flush();
			oos.close();
			fos.flush();
			fos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return key;
	}
	SecretKey getAesKey(String fname){
		SecretKey key = null;
		try{
			FileInputStream fis = new FileInputStream(fname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			for(int i=0; i<writeCount; i++){
				key = (SecretKey) ois.readObject();
				System.out.println(key.getEncoded());
			}
			ois.close();
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
}

