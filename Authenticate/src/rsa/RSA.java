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
	byte[] encrypt(byte[] data, PublicKey publicKey){
		
		byte[] encrypted = null;
		try{
			//PublicKey publicKey = getPublicKey(fname); //regenerates key and returns it
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encrypted = cipher.doFinal(data);
			//System.out.println("Encrypted Data " + encrypted.length);
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return encrypted;
	}
	
	//for encryption using private key
		byte[] encrypt(byte[] data, PrivateKey privateKey){
			
			byte[] encrypted = null;
			try{
				//PublicKey publicKey = getPublicKey(fname); //regenerates key and returns it
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, privateKey);
				encrypted = cipher.doFinal(data);
				System.out.println("Encrypted Data " + encrypted);
			}
			
			catch(Exception e){
				e.printStackTrace();
			}
			
			return encrypted;
		}
	
		//dencryption using private key
		byte[] decrypt(byte[] data, PublicKey publicKey){
			byte[] decrypted = null;
			try{
				//PrivateKey privateKey = getPrivateKey(fname); //regenerates key and returns it
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, publicKey);
				decrypted = cipher.doFinal(data);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return decrypted;	
		}
	
	//dencryption using private key
	byte[] decrypt(byte[] data, PrivateKey privateKey){
		byte[] decrByte = null;
		try{
			//PrivateKey privateKey = getPrivateKey(fname); //regenerates key and returns it
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			decrByte = cipher.doFinal(data);
			//decrypted = new String(decrByte);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return decrByte;	
	}
	
	//method to sign message
	byte[] sign(byte[] data, PrivateKey privateKey){
		byte[] signed = null;
		try{
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initSign(privateKey);
			sig.update(data);
			signed = sig.sign();
		}catch(Exception e){
			e.printStackTrace();
		}
		return signed;
	}
	
	
	//method to verify signature
	boolean verifySign(byte[] data, byte[] signed, PublicKey publicKey){
		boolean verified = false;
		try{
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(publicKey);
			sig.update(data);
			verified = sig.verify(signed);
		}catch(Exception e){
			e.printStackTrace();
		}
		return verified;
	}
}
