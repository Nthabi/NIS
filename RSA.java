package rsa;

import java.io.*;
import java.math.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

public class RSA {
	//Generates key pairs and stores them in specified files. Keys stored separately
	public void rsaKeyGen(String data, String file_public, String file_private){
		//byte[] keyBytes;
		try{
			
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(2048);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA"); //converts keys into keyspecifications into their modulus and exponent constituents
			RSAPublicKeySpec rsaPublic = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			RSAPrivateKeySpec rsaPrivate = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			
			storeKeys(file_public, rsaPublic.getModulus(), rsaPublic.getPublicExponent()); //public key stored as 2 objects: modulus and exponent
			storeKeys(file_private, rsaPrivate.getModulus(), rsaPrivate.getPrivateExponent());		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Stores key in specified file. 2 objects stored - modulus and exponent which make up the key
	void storeKeys(String fname, BigInteger mod, BigInteger exp) throws IOException{
		
		try{
			File file = new File(fname);
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			
			oos.writeObject(mod);
			oos.writeObject(exp);
			
			oos.flush();
			oos.close();
			fos.flush();
			fos.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//for encryption using public key
	byte[] encrypt(String data, String fname){
		
		byte[] encrypted = null;
		try{
			PublicKey publicKey = getPublicKey(fname); //regenerates key and returns it
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encrypted = cipher.doFinal(data.getBytes());
			System.out.println("Encrypted Data " + encrypted);
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return encrypted;
	}
	
	//for encryption
	String decrypt(byte[] data, String fname) throws IOException{
		String decrypted = null;
		try{
			PrivateKey privateKey = getPrivateKey(fname); //regenerates key and returns it
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrByte = cipher.doFinal(data);
			decrypted = new String(decrByte);
			System.out.println("Decrypted Message: " + decrypted);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return decrypted;	
	}
	
	PublicKey getPublicKey(String fname) throws IOException{
		PublicKey publicKey = null;
		
		try{
			FileInputStream fis = new FileInputStream(fname);
			ObjectInputStream ois = new ObjectInputStream(fis);
		
			RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec((BigInteger) ois.readObject(), (BigInteger) ois.readObject()); //(modulus, exponent)
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
			
			fis.close();
			ois.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return publicKey;
	}
	
	PrivateKey getPrivateKey(String fname) throws IOException{
		PrivateKey privateKey = null;
		
		try{
			FileInputStream fis = new FileInputStream(fname);
			ObjectInputStream ois = new ObjectInputStream(fis);
		
			RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec((BigInteger) ois.readObject(), (BigInteger) ois.readObject()); //(modulus, exponent)
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
			
			fis.close();
			ois.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return privateKey;
	}
}
