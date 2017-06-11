package rsa;

import java.io.*;
import java.math.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

public class RSA {
	//Generates key pairs and stores them in specified files. Keys stored separately
	public KeyPair rsaKeyGen(){
		KeyPair keyPair = null;
		try{
			
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(2048);
			keyPair = keyPairGen.generateKeyPair();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return keyPair;
	}
	
	//for encryption using public key
	byte[] encryptPublic(String data, PublicKey publicKey){
		
		byte[] encrypted = null;
		try{
			//PublicKey publicKey = getPublicKey(fname); //regenerates key and returns it
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
	
	//for encryption using private key
		byte[] encryptPrivate(String data, PrivateKey privateKey){
			
			byte[] encrypted = null;
			try{
				//PublicKey publicKey = getPublicKey(fname); //regenerates key and returns it
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, privateKey);
				encrypted = cipher.doFinal(data.getBytes());
				System.out.println("Encrypted Data " + encrypted);
			}
			
			catch(Exception e){
				e.printStackTrace();
			}
			
			return encrypted;
		}
	
		//dencryption using private key
		String decrypt(byte[] data, PublicKey publicKey){
			String decrypted = null;
			try{
				//PrivateKey privateKey = getPrivateKey(fname); //regenerates key and returns it
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, publicKey);
				byte[] decrByte = cipher.doFinal(data);
				decrypted = new String(decrByte);
				System.out.println("Decrypted Message: " + decrypted);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return decrypted;	
		}
	
	//dencryption using private key
	String decrypt(byte[] data, PrivateKey privateKey){
		String decrypted = null;
		try{
			//PrivateKey privateKey = getPrivateKey(fname); //regenerates key and returns it
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
}
