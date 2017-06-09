package rsa;

import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.Cipher;

import java.math.*;

public class RSA_KeyGen {
	
	private static final String file_public = "public.key";
	private static final String file_private = "private.key";
	
	public void keyGen(String data){
		//byte[] keyBytes;
		try{
			
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(2048);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec rsaPublic = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			RSAPrivateKeySpec rsaPrivate = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			
			storeKeys(file_public, rsaPublic.getModulus(), rsaPublic.getPublicExponent());
			storeKeys(file_private, rsaPrivate.getModulus(), rsaPrivate.getPrivateExponent());		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
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
			
			oos.close();
			fos.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	byte[] encrypt(String data){
		
		byte[] encrypted = null;
		try{
			System.out.println("Before Encryption " + data);
			PublicKey publicKey = getPublicKey(this.file_public);
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
	

	String decrypt(byte[] data) throws IOException{
		String decrypted = null;
		try{
			PrivateKey privateKey = getPrivateKey(this.file_private);
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
			FileInputStream fis = new FileInputStream(new File(fname));
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
			FileInputStream fis = new FileInputStream(new File(fname));
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
	
