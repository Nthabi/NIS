package rsa;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AES {
	
	public static void main(String[] args){
		KeyGenerator keyGen;
		int writeCount = 0; //used to know how many objects written by objectouputstream
		try {
			File file = new File("key.txt");
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			
			for(int i=0; i<3; i++){
				keyGen = KeyGenerator.getInstance("AES");
				keyGen.init(256);
				SecretKey key = keyGen.generateKey();
				
				System.out.println(key);
				
				oos.writeObject(key);
				writeCount++;
				
			}
			oos.flush();
			oos.close();
			fos.flush();
			fos.close();
			
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			SecretKey key;
			
			for(int i=0; i<writeCount; i++){
				key = (SecretKey) ois.readObject();
				System.out.println(key.getEncoded());
			}
			ois.close();
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

