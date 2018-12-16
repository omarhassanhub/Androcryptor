package com.myproject.androcryptor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.os.Environment;

public class DropboxEncryptionFragment {
	
	//Configure the file.
	public static File dBoxConFile(File file, String password) {

		FileInputStream dBoxInput = null;
		File dBoxEncF = null;
		
		try {
	
			dBoxInput = new FileInputStream(file);
			byte dBoxFCont[] = new byte[(int)file.length()];
	
			dBoxInput.read(dBoxFCont);

			dBoxEncF = new File(
                    Environment.getExternalStorageDirectory().getPath() +
                    File.separator + file.getName());
			
			BufferedOutputStream dBoxBuffOutput = new BufferedOutputStream(
					new FileOutputStream(dBoxEncF));

			byte[] dBoxYourKey = dBoxProduceKey(password);
			byte[] dBoxFileBytes = dBoxEncrData(dBoxYourKey, dBoxFCont);

			dBoxBuffOutput.write(dBoxFileBytes);
			dBoxBuffOutput.flush();
			dBoxBuffOutput.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File is not reachable" + e);
			return null;
		}
		catch (IOException ioe) {
			System.out.println("I/O reading file " + ioe);
			return null;
		}
		catch (Exception e) {
			System.out.println("I/O encoding file " + e);
			return null;
		}
		finally {

			try {
				if (dBoxInput != null) {
					dBoxInput.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Error when eliminating stream: " + ioe);
				return null;
			}
		}
		return dBoxEncF;
	}
	
	//Decode the file.
	public static int dBoxDecrFile(File file, String password) {

		FileInputStream dBoxInputS = null;
		
		try {
		
			dBoxInputS = new FileInputStream(file);
			byte dBoxFCon[] = new byte[(int)file.length()];
	
			dBoxInputS.read(dBoxFCon);

			File dBoxDecr = new File(
                    Environment.getExternalStorageDirectory().getPath() +
                    File.separator + "Download" + File.separator +
                    file.getName());
			
			BufferedOutputStream dBoxBuffOutput = new BufferedOutputStream(
					new FileOutputStream(dBoxDecr));
			
			byte[] dBoxHKey = dBoxProduceKey(password);
			byte[] dBoxFByt = dBoxDecrData(dBoxHKey, dBoxFCon);
	
			dBoxBuffOutput.write(dBoxFByt);
			dBoxBuffOutput.flush();
			dBoxBuffOutput.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File is not reachable" + e);
			return -1;
		}
		catch (IOException ioe) {
			System.out.println("I/O reading file " + ioe);
			return -1;
		}
		catch (Exception e) {
			System.out.println("I/O encrypting file " + e);
			return -1;
		}
		finally {

			try {
				if (dBoxInputS != null) {
					dBoxInputS.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Error when eliminating stream: " + ioe);
				return -1;
			}
		}
		return 0;
	}
	
	//Generate key.
	public static byte[] dBoxProduceKey(String password) throws Exception {
		byte[] dBoxKeyBegin = password.getBytes("UTF-8");

		KeyGenerator dBoxKproduce = KeyGenerator.getInstance("AES");
		SecureRandom dBoxSecureRan = SecureRandom.getInstance( "SHA1PRNG", "Crypto");
		dBoxSecureRan.setSeed(dBoxKeyBegin);
		dBoxKproduce.init(256, dBoxSecureRan);
		SecretKey dBoxSecretKey = dBoxKproduce.generateKey();
		return dBoxSecretKey.getEncoded();
	}
	//Encrypt data.
	public static byte[] dBoxEncrData(byte[] key, byte[] fileData)
			throws Exception {

		byte[] dBoxIv ={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
				0x00,0x00,0x00,0x00};
		IvParameterSpec dBoxParaSp = new IvParameterSpec(dBoxIv);

		SecretKeySpec dBoxSecSpec = new SecretKeySpec(key, "AES");
		Cipher dBoxEncrypted = Cipher.getInstance("AES/CBC/PKCS5Padding");
		dBoxEncrypted.init(Cipher.ENCRYPT_MODE, dBoxSecSpec, dBoxParaSp);

		byte[] dBoxEncr = dBoxEncrypted.doFinal(fileData);
		return dBoxEncr;
	}
	//Decrypt data.
	public static byte[] dBoxDecrData(byte[] key, byte[] fileData)
			throws Exception {

		byte[] dBoxIv ={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
				0x00,0x00,0x00,0x00};
		IvParameterSpec dBoxParaSp = new IvParameterSpec(dBoxIv);
				
		SecretKeySpec dBoxSecSpec = new SecretKeySpec(key, "AES");
		Cipher dBoxDecrypted = Cipher.getInstance("AES/CBC/PKCS5Padding");
		dBoxDecrypted.init(Cipher.DECRYPT_MODE, dBoxSecSpec, dBoxParaSp);

		byte[] dBoxDecr = dBoxDecrypted.doFinal(fileData);
		return dBoxDecr;
	}
	//Use MD5 hash.
	public static String dBoxObtainMD5(String str) throws Exception {
		MessageDigest dBoxMessageD = MessageDigest.getInstance("MD5");
		byte[] dBoxByte = dBoxMessageD.digest(str.getBytes());

		int dBoxSize = dBoxByte.length;
		StringBuilder dBoxBuilder = new StringBuilder(dBoxSize);
		for (int i = 0; i < dBoxSize; i++) {

			int u = dBoxByte[i] & 255;

			if (u < 16) {
				dBoxBuilder.append("0").append(Integer.toHexString(u));
			} else {
				dBoxBuilder.append(Integer.toHexString(u));
			}
		}
		return dBoxBuilder.toString();
	}
}

